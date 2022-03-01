package Admin.Commands;

import Admin.PlayerReturn;
import Player.IPlayer;

/**
 * Command for a player ending the tournament, informing the player whether they won the tournament
 */
public class EndCommand implements PlayerCommand {
  private final IPlayer player;
  private final boolean wonTournament;

  /**
   * Constructor.
   * @param player the player
   */
  public EndCommand(IPlayer player, boolean wonTournament) {
    if (player == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.player = player;
    this.wonTournament = wonTournament;
  }

  @Override
  public PlayerReturn execute() {
    player.end(wonTournament);
    return new PlayerReturn(false);
  }
}
