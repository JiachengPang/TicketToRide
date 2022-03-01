package Admin;

import Player.IPlayer;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.IConnection;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.IPlayerGameState;
import Common.PlayerGameState;
import Common.TrainMap;
import Player.Player;

/**
 * A referee game state that contains information visible to a referee. It keeps track of all of:
 *  - the general game state
 *  - the deck
 *  - each player's hand
 *  - each player's # rails
 *  - each player's destinations
 *  - the turn order
 *  - the current player
 */
public class RefereeGameState implements IRefereeGameState {
  private final IGeneralGameState general;
  private final List<Color> deck;
  private final Map<IPlayer, PlayerInfo> allPlayerInfo;
  private final List<IPlayer> turnOrder;
  private final int currentTurn;

  /**
   * Constructor.
   * Constrains:
   *  - all inputs are non-null
   *  - player ids match playerInfo
   *  - playerHands contain only valid colors and non-negative # cards
   *  - playerRails contain non-negative # rails
   *  - playerDestinations contain valid destinations
   *  - turn order is valid
   *  - current turn is in turn order
   * @param general the general game state
   * @param deck the deck
   * @param allPlayerInfo player info
   * @param turnOrder the turn order
   * @param currentTurn the current player
   */
  public RefereeGameState(IGeneralGameState general,
                          List<Color> deck,
                          Map<IPlayer, PlayerInfo> allPlayerInfo,
                          List<IPlayer> turnOrder,
                          int currentTurn) {
    this.checkInput(general, deck, allPlayerInfo, turnOrder, currentTurn);
    Set<Color> validColors = new HashSet<>(
            Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));
    for (IPlayer player : turnOrder) {
      if (!allPlayerInfo.containsKey(player)) {
        allPlayerInfo.put(player, new PlayerInfo());
      }
      for (Color c : validColors) {
        if (!allPlayerInfo.get(player).getPlayerHand().containsKey(c)) {
          PlayerInfo info = allPlayerInfo.get(player);
          Map<Color, Integer> hand = new HashMap<>(info.getPlayerHand());
          hand.put(c, 0);
          allPlayerInfo.put(player, new PlayerInfo(hand, info.getPlayerRails(), info.getPlayerDestinations()));
        }
      }
    }

    this.general = general;
    this.deck = deck;
    this.allPlayerInfo = allPlayerInfo;
    this.turnOrder = turnOrder;
    this.currentTurn = currentTurn;
  }

  /**
   * Verify that all inputs are valid, throw IllegalArgumentException otherwise.
   * @param general the general game state
   * @param deck the deck
   * @param allPlayerInfo player info
   * @param turnOrder the turn order
   * @param currentTurn the current player
   */
  private void checkInput(
          IGeneralGameState general,
          List<Color> deck,
          Map<IPlayer, PlayerInfo> allPlayerInfo,
          List<IPlayer> turnOrder,
          int currentTurn) {
    if (general == null || deck == null || allPlayerInfo == null || turnOrder == null) {
      throw new IllegalArgumentException("All arguments should be non-null.");
    }

    Set<Color> validColors = new HashSet<>(
            Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));
    if (!validColors.containsAll(deck)) {
      throw new IllegalArgumentException("The deck contains invalid color cards.");
    }

    Set<IPlayer> players = new HashSet<>(turnOrder);
    if (turnOrder.size() > players.size()) {
      throw new IllegalArgumentException("Invalid turn order: duplicate turns.");
    }

    if (turnOrder.size() <= currentTurn) {
      throw new IllegalArgumentException("Current turn is not recognized.");
    }

    if (!players.containsAll(allPlayerInfo.keySet())) {
      throw new IllegalArgumentException("Player information does not match player id.");
    }

    for (IPlayer player : allPlayerInfo.keySet()) {
      Map<Color, Integer> hand = allPlayerInfo.get(player).getPlayerHand();
      if (!validColors.containsAll(hand.keySet())) {
        throw new IllegalArgumentException("Player is holding an invalid color card.");
      }
      for (Color c : hand.keySet()) {
        if (hand.get(c) < 0) {
          throw new IllegalArgumentException(
                  "Player is holding a negative number of cards of a color.");
        }
      }
    }

    for (IPlayer player : allPlayerInfo.keySet()) {
      if (allPlayerInfo.get(player).getPlayerRails() < 0) {
        throw new IllegalArgumentException(
                "Player is holding negative number of rails.");
      }
    }

    Set<IDestination> validDestinations = general.getMap().getDestinations();
    for (IPlayer player : allPlayerInfo.keySet()) {
      if (!validDestinations.containsAll(allPlayerInfo.get(player).getPlayerDestinations())) {
        throw new IllegalArgumentException("Player has an invalid destination." );
      }
    }
  }

  @Override
  public TrainMap getMap() {
    return this.general.getMap();
  }

  @Override
  public Map<IPlayer, Set<IConnection>> getAcquiredConnections() {
    return this.general.getAcquiredConnections();
  }

  @Override
  public Set<IConnection> unoccupiedConnections() {
    return this.general.unoccupiedConnections();
  }

  @Override
  public List<Color> getDeck() {
    return new ArrayList<>(this.deck);
  }

  @Override
  public Map<Color, Integer> getPlayerHand() {
    return new HashMap<>(this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerHand());
  }

  @Override
  public int getPlayerRails() {
    return this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerRails();
  }

  @Override
  public Map<IPlayer, PlayerInfo> getAllPlayerInfo() {
    return new HashMap<>(this.allPlayerInfo);
  }

  @Override
  public Set<IDestination> getPlayerDestination() {
    return new HashSet<>(this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerDestinations());
  }

  @Override
  public List<IPlayer> getTurnOrder() {
    return new ArrayList<>(this.turnOrder);
  }

  @Override
  public int getCurrentTurn() {
    return this.currentTurn;
  }

  @Override
  public boolean legalAcquisition(IConnection connection) {
    Set<IConnection> available = this.unoccupiedConnections();
    if (!available.contains(connection)) {
      return false;
    }
    Map<Color, Integer> playerHand = this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerHand();
    if (playerHand.get(connection.getColor()) < connection.getLength()) {
      return false;
    }
    int playerRails = this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerRails();
    return playerRails >= connection.getLength();
  }

  @Override
  public IPlayerGameState currentPlayerGameState() {
    return new PlayerGameState(
            this.general,
            this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerHand(),
            this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerRails(),
            this.allPlayerInfo.get(turnOrder.get(currentTurn)).getPlayerDestinations());
  }
  
  @Override
  public IGeneralGameState getGGS() {
    return this.general;
  }
}
