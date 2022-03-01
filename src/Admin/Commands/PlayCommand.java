package Admin.Commands;

import Admin.PlayerReturn;
import Common.IPlayerGameState;
import Player.IPlayer;

/**
 * Command for a player playing a turn
 */
public class PlayCommand implements PlayerCommand {
  private final IPlayer player;
  private final IPlayerGameState pgs;

  /**
   * Constructor
   * @param player the player
   * @param pgs the IPlayerGameState the player receives
   */
  public PlayCommand(IPlayer player, IPlayerGameState pgs) {
    if (player == null || pgs == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.player = player;
    this.pgs = pgs;
  }

  @Override
  public PlayerReturn execute() {
    return new PlayerReturn(player.play(pgs));
  }
}
