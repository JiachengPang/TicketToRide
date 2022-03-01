package Admin.Commands;

import Admin.PlayerReturn;
import Common.IDestination;
import Player.IPlayer;
import java.util.Set;

/**
 * Command for a player picking destinations
 */
public class PickCommand implements PlayerCommand {
  private final IPlayer player;
  private final Set<IDestination> destinations;

  /**
   * Constructor.
   * @param player the player
   * @param destinations destinations to choose from
   */
  public PickCommand(IPlayer player, Set<IDestination> destinations) {
    if (player == null || destinations == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.player = player;
    this.destinations = destinations;
  }

  @Override
  public PlayerReturn execute() {
    return new PlayerReturn(player.pick(destinations));
  }
}
