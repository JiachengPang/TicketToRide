package Admin;

import Admin.Commands.EndCommand;
import Admin.Commands.PlayerCommand;
import Admin.Commands.StartCommand;
import Common.TrainMap;
import Player.IPlayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * An implementation of IManager. This manager keeps track of:
 *  - the standing players
 *  - the map for the entire tournament
 *  - the kicked players
 *  - the deck
 *  - selectors for cards and destinations
 * After the tournament ends, the manager remembers the winners, losers, and the kicked players.
 */
public class Manager implements IManager {
  private final List<IPlayer> standingPlayers;
  private final List<IPlayer> winners;
  private final List<IPlayer> losers;
  private final List<IPlayer> kicked;
  private TrainMap map;
  private final List<Color> deck;
  private final ICardSelector cardSelector;
  private final IDestinationSelector destinationSelector;
  private boolean isLastGame;

  private final int TIMEOUT_LENGTH = 5;
  private final int MIN_PLAYERS_PER_GAME = 2;
  private final int MAX_PLAYERS_PER_GAME = 8;

  /**
   * Constructor
   * @param standingPlayers list of participants
   * @param deck the deck to be used for every game
   * @param cardSelector a selector to decide the sequence of the deck
   * @param destinationSelector a selector to decide the sequence of the destinations
   */
  public Manager(List<IPlayer> standingPlayers, List<Color> deck, ICardSelector cardSelector,
      IDestinationSelector destinationSelector) {
    if (standingPlayers == null || cardSelector == null || destinationSelector == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.standingPlayers = standingPlayers;
    this.winners = new ArrayList<>();
    this.losers = new ArrayList<>();
    this.kicked = new ArrayList<>();
    this.deck = deck;
    this.cardSelector = cardSelector;
    this.destinationSelector = destinationSelector;
    this.isLastGame = false;
  }

  @Override
  public void start() {
    try {
      this.setup();
    } catch(RuntimeException e) {
      return;
    }
    run();
    endTournament();
  }

  @Override
  public TrainMap setup() {
    List<TrainMap> playerMaps = new ArrayList<>();
    for (IPlayer player: standingPlayers) {
      PlayerReturn reply = handlePlayerCommunication(new StartCommand(player), player);
      if (reply.hasMalfunctioned()) {
        continue;
      }
      playerMaps.add(reply.getMap());
    }
    if (playerMaps.size() == 0) {
      throw new RuntimeException("All players have malfunctioned");
    }
    this.map = playerMaps.get(0);
    return this.map;
  }

  /**
   * Handles the communication with a player. This method invokes the player's method. If player
   * malfunctions or times out, they are ejected from the game, otherwise, the return value of
   * the player's method is stored in a PlayerReturn object to be future processed.
   * @param command A RefereeCommand that executes the player method call
   * @param player the player
   * @return PlayerReturn object that stores the return value of the player's method
   */
  private PlayerReturn handlePlayerCommunication(PlayerCommand command, IPlayer player) {
    PlayerReturn reply;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<PlayerReturn> future = executor.submit(command::execute);
    try {
      reply = future.get(TIMEOUT_LENGTH, TimeUnit.SECONDS);
    } catch(Exception e) {
      future.cancel(true);
      reply = new PlayerReturn(false);
      reply.setHasMalfunctioned(true);
      ejectPlayer(player);
    }
    return reply;
  }

  /**
   * eject a player from the tournament
   * @param player player to eject
   */
  private void ejectPlayer(IPlayer player) {
    this.standingPlayers.remove(player);
    this.kicked.add(player);
  }

  @Override
  public void run() {
    List<IPlayer> standingPlayersFromLastRound = new ArrayList<>();
    while (!tournamentEnded(standingPlayersFromLastRound)) {
      standingPlayersFromLastRound = startNextRound();
    }
  }

  @Override
  public List<IPlayer> startNextRound() {
    List<List<IPlayer>> playersByGame = allocatePlayers();
    for (List<IPlayer> players : playersByGame) {
      startNextGame(players);
    }
    return new ArrayList<>(standingPlayers);
  }

  @Override
  public List<List<IPlayer>> allocatePlayers() {
    List<List<IPlayer>> playersByGame = new ArrayList<>();
    List<IPlayer> playersForOneGame = new ArrayList<>();
    int count = 0;
    for (int i = 0; i < standingPlayers.size(); i++) {
      if (count == MAX_PLAYERS_PER_GAME) {
        playersByGame.add(playersForOneGame);
        playersForOneGame = new ArrayList<>();
        count = 0;
      }
      if (i == standingPlayers.size() - 1 && count < MIN_PLAYERS_PER_GAME - 1) {
        List<IPlayer> playersForLastGame = playersByGame.get(playersByGame.size() - 1);
        IPlayer playerToRetract = playersForLastGame.get(MAX_PLAYERS_PER_GAME - 1);
        playersForLastGame.remove(playerToRetract);
        i -= 2;
      } else {
        playersForOneGame.add(standingPlayers.get(i));
        count++;
      }
    }
    playersByGame.add(playersForOneGame);
    return playersByGame;
  }

  @Override
  public List<IPlayer> startNextGame(List<IPlayer> players) {
    IReferee ref = new Referee(map, players, deck, cardSelector, destinationSelector);
    ref.start();
    kicked.addAll(ref.kickedOut());
    standingPlayers.removeAll(ref.kickedOut());
    List<IPlayer> winners = ref.getWinners();
    List<IPlayer> gameLosers = new ArrayList<>(players);
    gameLosers.removeAll(winners);
    losers.addAll(gameLosers);
    standingPlayers.removeAll(gameLosers);
    return winners;
  }

  @Override
  public void endTournament() {
    this.informResults(standingPlayers, true);
    this.informResults(losers, false);
    this.winners.addAll(standingPlayers);
  }

  /**
   * Inform the list of players their result in the tournament
   * @param players list of players
   * @param result tournament result
   */
  private void informResults(List<IPlayer> players, boolean result) {
    List<IPlayer> toInform = new ArrayList<>(players);
    for (IPlayer player : toInform) {
      PlayerReturn reply = this.handlePlayerCommunication(new EndCommand(player, result), player);
      if (reply.hasMalfunctioned()) {
        if (result) {
          standingPlayers.remove(player);
        } else {
          losers.remove(player);
        }
        kicked.add(player);
      }
    }
  }

  @Override
  public List<IPlayer> getWinners() {
    return new ArrayList<>(this.winners);
  }

  @Override
  public List<IPlayer> getKickedPlayers() {
    return new ArrayList<>(this.kicked);
  }

  /**
   * the tournament should end if
   *  - there are not enough players left
   *  - 2 rounds produced the same winners
   *  - there are enough players for a single game, it runs the final game with these players
   * @param standingPlayersFromLastRound standing players from last round
   * @return true if the tournament should end, false otherwise
   */
  private boolean tournamentEnded(List<IPlayer> standingPlayersFromLastRound) {
    boolean res = standingPlayersFromLastRound.equals(standingPlayers)
            || standingPlayers.size() < MIN_PLAYERS_PER_GAME
            || this.isLastGame;

    if (standingPlayers.size() <= MAX_PLAYERS_PER_GAME && standingPlayers.size() >= MIN_PLAYERS_PER_GAME) {
      this.isLastGame = true;
    }
    return res;
  }
}
