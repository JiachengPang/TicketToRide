package Admin;

import Common.IDestination;
import Common.TrainMap;
import Player.Action;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the return value from a player when the referee invokes a method in a player object.
 *  - action: the action the player chooses for a turn
 *  - destinations: the set of destinations the player returns to the referee
 *  - hasReturn: false if the function invoked is void
 *  - hasMalfunctions: the player's method malfunctions or times out
 */
public class PlayerReturn {
  private Action action;
  private Set<IDestination> destinations;
  private TrainMap map;
  private boolean hasReturn;
  private boolean hasMalfunctioned;

  /**
   * Action return value
   * @param action action
   */
  public PlayerReturn(Action action) {
    if (action == null) {
      throw new IllegalArgumentException("Action cannot be null");
    }
    this.action = action;
  }

  /**
   * Destinations return value
   * @param destinations set of IDestination
   */
  public PlayerReturn(Set<IDestination> destinations) {
    if (destinations == null) {
      throw new IllegalArgumentException("Destinations cannot be null");
    }
    this.destinations = destinations;
  }

  /**
   * TrainMap return value
   * @param map map of game
   */
  public PlayerReturn(TrainMap map) {
    if (map == null) {
      throw new IllegalArgumentException("Map cannot be null");
    }
    this.map = map;
  }

  /**
   * No return value
   * @param hasReturn no return
   */
  public PlayerReturn(boolean hasReturn) {
    if (hasReturn) {
      throw new IllegalArgumentException("Has return cannot be true when there is no return");
    }
    this.hasReturn = hasReturn;
  }

  /**
   * Get the action value
   * @return action value
   */
  public Action getAction() {
    return action;
  }

  /**
   * Get the destination value
   * @return destination value
   */
  public Set<IDestination> getDestinations() {
    return new HashSet<>(destinations);
  }

  /**
   * Get the map from the player
   * @return the map
   */
  public TrainMap getMap() {
    return map;
  }

  /**
   * Get the hasReturn value
   * @return hasReturn value
   */
  public boolean getHasReturn() {
    return hasReturn;
  }

  /**
   * Set the hasMalfunctioned value
   * @param hasMalfunctioned hasMalfunctioned value
   */
  public void setHasMalfunctioned(boolean hasMalfunctioned) {
    this.hasMalfunctioned = hasMalfunctioned;
  }

  /**
   * Get the hasMalfunctioned value
   * @return hasMalfunctioned value
   */
  public boolean hasMalfunctioned() {
    return this.hasMalfunctioned;
  }
}
