package Admin.Commands;

import Admin.PlayerReturn;
import Player.IPlayer;

/**
 * Command for notifying a player has won the game
 */
public class WinCommand implements PlayerCommand {
  private final IPlayer player;
  private final boolean win;

  /**
   * Constructor
   * @param player the player
   * @param win if the player has won the game
   */
  public WinCommand(IPlayer player, boolean win) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    this.player = player;
    this.win = win;
  }

  @Override
  public PlayerReturn execute() {
    player.win(win);
    return new PlayerReturn(false);
  }
}
