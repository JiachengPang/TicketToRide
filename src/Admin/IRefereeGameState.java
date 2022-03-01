package Admin;

import Player.IPlayer;
import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.List;

import Common.IConnection;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.IPlayerGameState;

/**
 * Represents information about the state of a game that is visible to a referee.
 * Contents include all from IGeneralGameState plus:
 *  - the deck
 *  - hand of every player
 *  - # rails of every player
 *  - destinations of every player
 */
public interface IRefereeGameState extends IGeneralGameState {

  /**
   * Get the deck, which is a list of colors representing the ordered list of colored cards.
   * @return a list of colors
   */
  List<Color> getDeck();

  /**
   * Get the hand of current player.
   * @return a map of color -> int
   */
  Map<Color, Integer> getPlayerHand();

  /**
   * Get the # of current player's rails
   * @return # rails
   */
  int getPlayerRails();

  /**
   * get the player information for all players.
   * @return a map of players to their information
   */
  Map<IPlayer, PlayerInfo> getAllPlayerInfo();

  /**
   * Get the destinations of current player.
   * @return set of IDestination
   */
  Set<IDestination> getPlayerDestination();

  /**
   * Get the turn order.
   * @return a list of player players
   */
  List<IPlayer> getTurnOrder();

  /**
   * Get the current turn.
   * @return player id
   */
  int getCurrentTurn();

  /**
   * Check if it is legal for the current player to acquire a connection.
   * The action is illegal if:
   *  - the connection does not exist on the map
   *  - the connection is already acquired
   * @param connection IConnection
   * @return true if the action is legal, false otherwise
   */
  boolean legalAcquisition(IConnection connection);

  /**
   * Generate an IPlayerGameState for the current player.
   * @return IPlayerGameState
   */
  IPlayerGameState currentPlayerGameState();

  /**
   * Get the IGeneralGameState.
   * @return IGeneralGameState
   */
  IGeneralGameState getGGS();
}
