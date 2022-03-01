package Admin.Commands;

import Admin.PlayerReturn;
import Common.IDestination;
import Player.IPlayer;
import java.util.Set;

/**
 * Command for a player starting the tournament
 */
public class StartCommand implements PlayerCommand {
  private final IPlayer player;

  /**
   * Constructor.
   * @param player the player
   */
  public StartCommand(IPlayer player) {
    if (player == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.player = player;
  }

  @Override
  public PlayerReturn execute() {
    return new PlayerReturn(player.start());
  }
}
