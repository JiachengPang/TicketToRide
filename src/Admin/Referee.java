package Admin;

import Admin.Commands.PickCommand;
import Admin.Commands.PlayCommand;
import Admin.Commands.ReceiveCardsCommand;
import Admin.Commands.PlayerCommand;
import Admin.Commands.SetupCommand;
import Admin.Commands.WinCommand;
import Common.GeneralGameState;
import Common.ICity;
import Common.IConnection;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.TrainMap;
import Common.map;
import Player.Action;
import Player.IPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An implementation for IReferee. The referee keeps track of
 *  - the game state
 *  - list of players currently in the game
 *  - number of turns since last time the game state changed
 *  - destination availvable
 *  - rankings of players
 *  - kicked out players
 * The referee runs the game and stores the result into its rankings and kickOut fields.
 */
public class Referee implements IReferee {
  private IRefereeGameState rgs;
  private final List<IPlayer> players;
  private int turnsSinceLastChange;
  private final List<IDestination> initialDestinations;
  private final Map<IPlayer, Scores> scores;
  private final List<IPlayer> kickedOut;


  // constants
  private final int PLAYER_RAILS = 45;
  private final int PLAYER_CARDS = 4;
  private final int DEST_CHOICES = 5;
  private final int INITIAL_DESTS = 2;
  private final int RAILS_TO_END_GAME = 2;
  private final int DRAWN_CARDS = 2;
  private final int LONGEST_POINTS = 20;
  private final int DEST_POINTS = 10;
  private final int TIMEOUT_LENGTH = 5;

  /**
   * Constructor
   * @param map the map
   * @param players ordered list of players
   * @param deck the deck
   * @param cardSelector cardSelector to decide the sequence of the deck
   * @param destinationSelector destinationSelector to decide the sequence of destinations
   */
  public Referee(TrainMap map, List<IPlayer> players, List<Color> deck, ICardSelector cardSelector, IDestinationSelector destinationSelector) {
    checkInput(map, players, deck, cardSelector, destinationSelector);
    Map<IPlayer, Set<IConnection>> acquiredConnections = new HashMap<>();
    Map<IPlayer, PlayerInfo> allPlayerInfo = new HashMap<>();
    List<IPlayer> turnOrder = new ArrayList<>();
    for (IPlayer player: players) {
      acquiredConnections.put(player, new HashSet<>());
      allPlayerInfo.put(player, new PlayerInfo());
      turnOrder.add(player);
    }
    IGeneralGameState ggs = new GeneralGameState(map, acquiredConnections);

    this.rgs = new RefereeGameState(ggs, cardSelector.shuffle(deck), allPlayerInfo, turnOrder, 0);
    this.players = players;
    this.initialDestinations = destinationSelector.shuffle(map.getDestinations());
    this.scores = new HashMap<>();
    this.kickedOut = new ArrayList<>();
  }

  /**
   * Verify that all inputs are valid, throw IllegalArgumentException otherwise.
   * @param map the map on which the game is played
   * @param players the players of the game
   * @param deck the deck of colored cards
   * @param cardSelector cardSelector to decide the sequence of the deck
   * @param destinationSelector destinationSelector to decide the sequence of destinations
   */
  private void checkInput(TrainMap map, List<IPlayer> players, List<Color> deck, ICardSelector cardSelector, IDestinationSelector destinationSelector) {

    if (map == null || players == null || deck == null || cardSelector == null || destinationSelector == null) {
      throw new IllegalArgumentException("All arguments should be non-null.");
    }

    Set<Color> validColors = new HashSet<>(
        Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));
    if (!validColors.containsAll(deck)) {
      throw new IllegalArgumentException("The deck contains invalid color cards.");
    }

    if (deck.size() < 4 * players.size()) {
      throw new IllegalArgumentException("The deck is not large enough to start a game.");
    }

    if (map.getDestinations().size() < players.size() * INITIAL_DESTS + (DEST_CHOICES - INITIAL_DESTS)) {
      throw new IllegalArgumentException("The map does not have enough potential destinations to "
          + "start a game.");
    }

    if (players.size() < 2 || players.size() > 8) {
      throw new IllegalArgumentException("Cannot start game with " + players.size() + " players. "
          + "Must have between 2 and 8 players, inclusive.");
    }
  }

  @Override
  public void start() {
    this.setup();
    this.playTurns();
    this.scoreGame();
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

  @Override
  public void setup() {
    List<Color> deck = this.rgs.getDeck();
    Map<IPlayer, PlayerInfo> playerInfos = new HashMap<>();
    List<IPlayer> playerCopies = new ArrayList<>(players);
    for (IPlayer player: playerCopies) {
      Map<Color, Integer> hand = this.dealCards(deck, PLAYER_CARDS);
      PlayerReturn reply = handlePlayerCommunication(new SetupCommand(player, this.rgs.getMap(), PLAYER_RAILS, hand), player);
      if (reply.hasMalfunctioned()) {
        continue;
      }
      Set<IDestination> chosenDests = this.dealDestinations(player);
      if (!chosenDests.isEmpty()) {
        playerInfos.put(player, new PlayerInfo(hand, PLAYER_RAILS, chosenDests));
      }
    }
    this.rgs = new RefereeGameState(this.rgs.getGGS(), deck, playerInfos, this.rgs.getTurnOrder(),
        0);
  }

  /**
   * Select a number of cards from the top of the deck to deal to a player.
   * @param deck the deck
   * @param num number of cards
   * @return a map of Color to Integer representing cards
   */
  private Map<Color, Integer> dealCards(List<Color> deck, int num) {
    Map<Color, Integer> hand = new HashMap<>();
    for (int i = 0; i < Math.min(num, deck.size()); i++) {
      Color card = deck.remove(0);
      if (!hand.containsKey(card)) {
        hand.put(card, 0);
      }
      hand.put(card, hand.get(card) + 1);
    }
    return hand;
  }

  /**
   * Select destination for a player to choose from.
   * @param player the player
   * @return destinations chosen by a player
   */
  private Set<IDestination> dealDestinations(IPlayer player) {
    Set<IDestination> initialChoices = new HashSet<>(initialDestinations.subList(0, DEST_CHOICES));
    PlayerReturn reply = handlePlayerCommunication(new PickCommand(player, initialChoices), player);
    if (reply.hasMalfunctioned()) {
      return new HashSet<>();
    }
    Set<IDestination> remainingDests = reply.getDestinations();
    if (!(remainingDests.size() == initialChoices.size() - INITIAL_DESTS) || !initialChoices.containsAll(remainingDests)) {
      this.ejectPlayer(player);
    }
    initialChoices.removeAll(remainingDests);
    initialDestinations.removeAll(initialChoices);
    return initialChoices;
  }

  /**
   * Eject a player from the game. Their connections and cards are discarded.
   * They are added to the list of kicked out players.
   * @param player player to eject
   */
  private void ejectPlayer(IPlayer player) {
    Map<IPlayer, PlayerInfo> playerInfos = this.rgs.getAllPlayerInfo();
    playerInfos.remove(player);
    Map<IPlayer, Set<IConnection>> acquiredConnections = new HashMap<>(this.rgs.getAcquiredConnections());
    acquiredConnections.remove(player);
    IGeneralGameState newGGS = new GeneralGameState(this.rgs.getMap(), acquiredConnections);
    List<IPlayer> newTurnOrder = new ArrayList<>(this.rgs.getTurnOrder());
    newTurnOrder.remove(player);
    int newTurn;
    if (newTurnOrder.size() == 0) {
      newTurn = -1;
    } else {
      newTurn = this.rgs.getCurrentTurn() % newTurnOrder.size();
    }
    this.rgs = new RefereeGameState(newGGS, this.rgs.getDeck(), playerInfos,
            newTurnOrder, newTurn);
    turnsSinceLastChange = 0;
    this.kickedOut.add(player);
    this.players.remove(player);
  }

  @Override
  public void playTurns() {
    while (!this.gameEnd()) {
      playTurn();
    }
  }

  @Override
  public void playTurn() {
    IPlayer player = this.players.get(this.rgs.getCurrentTurn());
    PlayerReturn reply = handlePlayerCommunication(new PlayCommand(player, this.rgs.currentPlayerGameState()), player);
    if (reply.hasMalfunctioned()) {
      return;
    }
    Action action = reply.getAction();
    if (action.requestCards) {
      this.handleRequestCards();
    } else if (action.connection != null) {
      this.handleRequestConnection(action.connection);
    }
  }

  /**
   * Whether the game should end. It ends if :
   *  - there is no player left
   *  - a round has gone by and nobody changed the game state
   *  - A player's rails drops below 3, everyone else gets to player 1 more turn before the game ends
   * @return true if the game should end, false otherwise.
   */
  private boolean gameEnd() {
    return this.players.size() == 0
        || this.turnsSinceLastChange == players.size()
        || this.rgs.getPlayerRails() <= RAILS_TO_END_GAME;
  }

  /**
   * Handle request card Action. A number of cards are dealt to the player. If the deck is empty,
   * no card is dealt to the player and turnsSinceLastChange increments.
   */
  private void handleRequestCards() {
    List<Color> deck = this.rgs.getDeck();
    IPlayer player = this.players.get(this.rgs.getCurrentTurn());
    Map<Color, Integer> dealtCards = this.dealCards(deck, DRAWN_CARDS);
    PlayerReturn reply = handlePlayerCommunication(new ReceiveCardsCommand(player, dealtCards), player);
    if (reply.hasMalfunctioned()) {
      return;
    }
    Map<IPlayer, PlayerInfo> playerInfoMap = this.rgs.getAllPlayerInfo();
    Map<Color, Integer> hand = playerInfoMap.get(player).getPlayerHand();
    for (Color c : dealtCards.keySet()) {
      hand.put(c, hand.get(c) + dealtCards.get(c));
    }

    if (dealtCards.isEmpty()) {
      turnsSinceLastChange++;
    } else {
      turnsSinceLastChange = 0;
    }

    playerInfoMap.put(player, new PlayerInfo(hand, this.rgs.getPlayerRails(),
        this.rgs.getPlayerDestination()));
    this.rgs = new RefereeGameState(this.rgs.getGGS(), deck, playerInfoMap,
        this.rgs.getTurnOrder(), this.nextPlayerIndex());
  }

  /**
   * Compute the index of the player that should go next. It wraps around if index reaches the
   * length of the player list
   * @return index of the next player
   */
  private int nextPlayerIndex() {
    int currentIndex = this.rgs.getCurrentTurn();
    return (currentIndex + 1) % this.rgs.getTurnOrder().size();
  }

  /**
   * Handle request connection Action. If the connection can be acquired by the player, update
   * the rgs to contain that information. If not, eject the player.
   * @param connection connection to acquire
   */
  private void handleRequestConnection(IConnection connection) {
    IPlayer player = this.players.get(this.rgs.getCurrentTurn());
    if (!this.rgs.legalAcquisition(connection)) {
      this.ejectPlayer(player);
      return;
    }
    Map<IPlayer, PlayerInfo> playerInfos = this.rgs.getAllPlayerInfo();
    PlayerInfo currInfo = playerInfos.get(player);
    Map<Color, Integer> hand = currInfo.getPlayerHand();
    hand.put(connection.getColor(), hand.get(connection.getColor()) - connection.getLength());
    playerInfos.put(player, new PlayerInfo(hand,
        currInfo.getPlayerRails() - connection.getLength(),
        currInfo.getPlayerDestinations()));

    Map<IPlayer, Set<IConnection>> acquiredConnections = new HashMap<>(this.rgs.getAcquiredConnections());
    Set<IConnection> connectionSet = new HashSet<>(acquiredConnections.get(player));
    connectionSet.add(connection);
    acquiredConnections.put(player, connectionSet);

    IGeneralGameState newGGS = new GeneralGameState(this.rgs.getMap(), acquiredConnections);

    this.rgs = new RefereeGameState(newGGS, this.rgs.getDeck(), playerInfos,
        this.rgs.getTurnOrder(), this.nextPlayerIndex());
    turnsSinceLastChange = 0;
  }

  @Override
  public Map<IPlayer, Scores> scoreGame() {
    if (this.players.size() == 0) {
      return new HashMap<>();
    }
    List<IPlayer> longestPathPlayers = this.findLongestPathPlayers();
    for (IPlayer player : this.players) {
      int segmentScore = this.scoreSegments(player);
      int destinationScore = this.scoreDestinations(player);
      int longestPathScore = 0;
      if (longestPathPlayers.contains(player)) {
        longestPathScore = LONGEST_POINTS;
      }
      scores.put(player, new Scores(segmentScore, destinationScore, longestPathScore));
    }

    List<IPlayer> winners = getWinners();
    for (IPlayer player : players) {
      PlayerReturn reply = handlePlayerCommunication(new WinCommand(player, winners.contains(player)), player);
      if (reply.hasMalfunctioned()) {
        scores.remove(player);
        kickedOut.add(player);
      }
    }
    return new HashMap<>(scores);
  }

  /**
   * Compute the segment score for a player. 1 segment is worth 1 point.
   * @param player the player
   * @return segment score for this player
   */
  private int scoreSegments(IPlayer player) {
    Set<IConnection> acquiredConnections = this.rgs.getAcquiredConnections().get(player);
    int res = 0;

    for (IConnection connection : acquiredConnections) {
      res += connection.getLength();
    }

    return res;
  }

  /**
   * Compute the destination score for a player. Each completed destination is 10 points. Each
   * destination that is not completed is -10 points
   * @param player the player
   * @return destination score for this player
   */
  private int scoreDestinations(IPlayer player) {
    Set<IConnection> acquiredConnections = this.rgs.getAcquiredConnections().get(player);
    int score = 0;

    Dimension d = this.rgs.getMap().getDimension();
    TrainMap playerConnectionsMap = new map(d.width, d.height, this.rgs.getMap().getCities(), acquiredConnections);

    Map<IPlayer, PlayerInfo> playerInfo = this.rgs.getAllPlayerInfo();
    for (IDestination destination : playerInfo.get(player).getPlayerDestinations()) {
      List<ICity> pair = new ArrayList<>(destination.getCities());
      if (playerConnectionsMap.havePath(pair.get(0).getName(), pair.get(1).getName())) {
        score += DEST_POINTS;
      } else {
        score -= DEST_POINTS;
      }
    }
    return score;
  }

  /**
   * Find the players that have the longest path.
   * @return a list of players with the longest path
   */
  private List<IPlayer> findLongestPathPlayers() {
    List<IPlayer> players = new ArrayList<>();
    Map<IPlayer, Integer> playerPathLengths = new HashMap<>();
    Map<IPlayer, Set<IConnection>> acquiredConnections = this.rgs.getAcquiredConnections();

    for (IPlayer player : this.players) {
      Set<ICity> playerCities = new HashSet<>();
      Set<IConnection> connections = acquiredConnections.get(player);
      for (IConnection connection : connections) {
        playerCities.addAll(connection.getCities());
      }

      playerPathLengths.put(player, this.longestPathLength(playerCities, connections));
    }

    int max = Collections.max(playerPathLengths.entrySet(), Map.Entry.comparingByValue()).getValue();

    for (IPlayer player : playerPathLengths.keySet()) {
      if (playerPathLengths.get(player) == max) {
        players.add(player);
      }
    }

    return players;
  }

  /**
   * Compute the length of the longest path with the given cities and connections
   * @param cities cities
   * @param connections connections
   * @return length of the longest path
   */
  private int longestPathLength(Set<ICity> cities, Set<IConnection> connections) {
    int max = 0;
    for (ICity city : cities) {
      int length = this.longestPathFromCity(city, connections);
      max = Math.max(max, length);
    }

    return max;
  }

  /**
   * Compute the length of the longest path starting from the given city
   * @param city a city
   * @param connections all connections
   * @return the length of the longest path from this city
   */
  private int longestPathFromCity(ICity city, Set<IConnection> connections) {
    List<ICity> visited = new ArrayList<>();
    List<ICity> queue = new ArrayList<>();
    queue.add(city);

    Map<ICity, Integer> segments = new HashMap<>();
    segments.put(city, 0);
    while(queue.size() > 0) {
      ICity current = queue.remove(0);
      if (visited.contains(current)) {
        continue;
      }

      visited.add(current);
      for (IConnection connection : connections) {
        if (connection.getCities().contains(current)) {
          ICity endpoint = connection.connectedTo(current.getName());
          queue.add(endpoint);
          if (!segments.containsKey(endpoint)) {
            segments.put(endpoint, segments.get(current) + connection.getLength());
          } else {
            segments.put(endpoint, Math.max(segments.get(endpoint), segments.get(current) + connection.getLength()));
          }
        }
      }
    }

    return Collections.max(segments.entrySet(), Map.Entry.comparingByValue()).getValue();
  }

  @Override
  public List<List<IPlayer>> rankings() {
    List<List<IPlayer>> rankings = new ArrayList<>();
    if (scores.isEmpty()) {
      return rankings;
    }

    List<IPlayer> order = new ArrayList<>(scores.keySet());
    order.sort((p1, p2) -> scores.get(p2).compareTo(scores.get(p1)));

    int previousScore = scores.get(order.get(0)).total();
    rankings.add(new ArrayList<>());
    for (IPlayer player : order) {
      if (previousScore != scores.get(player).total()) {
        rankings.add(new ArrayList<>());
      }
      rankings.get(rankings.size() - 1).add(player);
    }

    return rankings;
  }

  @Override
  public List<IPlayer> getWinners() {
    List<IPlayer> winners = new ArrayList<>();
    Scores max = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getValue();
    for (IPlayer player : scores.keySet()) {
      if (scores.get(player).total() == max.total()) {
        winners.add(player);
      }
    }
    return winners;
  }

  @Override
  public List<IPlayer> kickedOut() {
    return new ArrayList<>(this.kickedOut);
  }

  @Override
  public IRefereeGameState getRGS() {
    return this.rgs;
  }


}
