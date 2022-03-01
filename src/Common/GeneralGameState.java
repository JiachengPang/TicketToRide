package Common;

import Player.IPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A general game state that contains information visible to all players and the referee. It keeps
 * track of:
 *  - the map
 *  - all acquired connections with player id
 *  - deck size
 *  - all player ids
 *  - turn order
 *  - current turn
 */
public class GeneralGameState implements IGeneralGameState{
  private final TrainMap map;
  private final Map<IPlayer, Set<IConnection>> acquiredConnections;

  /**
   * Constructor.
   *
   * @param map the board
   * @param acquiredConnections map of all acquired connections with player id
   */
  public GeneralGameState(TrainMap map, Map<IPlayer, Set<IConnection>> acquiredConnections) {
    this.checkInput(map, acquiredConnections);
    this.map = map;
    this.acquiredConnections = acquiredConnections;
  }

  /**
   * Verify that all inputs are valid, throw IllegalArgumentException otherwise.
   * @param map the board
   * @param acquiredConnections map of all acquired connections with player id
   */
  private void checkInput(TrainMap map, Map<IPlayer, Set<IConnection>> acquiredConnections) {
    if (map == null || acquiredConnections == null) {
      throw new IllegalArgumentException("All arguments should be non-null.");
    }

    Set<IConnection> allConnections = map.getAllConnections();
    for (IPlayer player : acquiredConnections.keySet()) {
      if (!allConnections.containsAll(acquiredConnections.get(player))) {
        throw new IllegalArgumentException("Player acquired a non-existent connection.");
      }
    }

    Set<IConnection> allAcquiredConnections = new HashSet<>();
    int count = 0;
    for (IPlayer player : acquiredConnections.keySet()) {
      allAcquiredConnections.addAll(acquiredConnections.get(player));
      count += acquiredConnections.get(player).size();
    }
    if (count != allAcquiredConnections.size()) {
      throw new IllegalArgumentException("A connection is acquired by more than 1 player.");
    }
  }

  @Override
  public TrainMap getMap() {
    return this.map;
  }

  @Override
  public Map<IPlayer, Set<IConnection>> getAcquiredConnections() {
    return new HashMap<>(this.acquiredConnections);
  }

  @Override
  public Set<IConnection> unoccupiedConnections() {
    Set<IConnection> unoccupied = this.map.getAllConnections();
    for (IPlayer player : this.acquiredConnections.keySet()) {
      Set<IConnection> occupied = this.acquiredConnections.get(player);
      unoccupied.removeAll(occupied);
    }
    return unoccupied;
  }
}
