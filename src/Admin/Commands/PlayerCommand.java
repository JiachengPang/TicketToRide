package Admin.Commands;

import Admin.PlayerReturn;

/**
 * Represents a communication from the referee to the player.
 */
public interface PlayerCommand {

  /**
   * Execute the communication by invoking a method in the player.
   * @return a PlayerReturn object that stores the return value of the method call
   */
  PlayerReturn execute();

}
