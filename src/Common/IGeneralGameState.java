package Common;

import Player.IPlayer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents information about the state of a game that is visible to all parties, including all
 * players and the referee. Contents are:
 *  - the board
 *  - acquired connections with player id
 */
public interface IGeneralGameState {

  /**
   * Get the game board.
   * @return TrainMap
   */
  TrainMap getMap();

  /**
   * Get the map of acquired connections for all players.
   * @return map of IPlayer to Set of IConnections they have acquired
   */
  Map<IPlayer, Set<IConnection>> getAcquiredConnections();

  /**
   * Get all unoccupied connections.
   * @return set of IConnection
   */
  Set<IConnection> unoccupiedConnections();
}
