package Admin.Commands;

import Admin.PlayerReturn;
import Common.TrainMap;
import Player.IPlayer;
import java.awt.Color;
import java.util.Map;

/**
 * Command for setting up a player
 */
public class SetupCommand implements PlayerCommand {
  private final IPlayer player;
  private final TrainMap map;
  private final int rails;
  private final Map<Color, Integer> hand;

  /**
   * Constructor
   * @param player the player
   * @param map the map
   * @param rails number of rails
   * @param hand the starting cards
   */
  public SetupCommand(IPlayer player, TrainMap map, int rails, Map<Color, Integer> hand) {
    if (player == null || map == null || rails < 0 || hand == null) {
      throw new IllegalArgumentException("Invalid inputs");
    }
    this.player = player;
    this.map = map;
    this.rails = rails;
    this.hand = hand;
  }

  @Override
  public PlayerReturn execute() {
    player.setup(map, rails, hand);
    return new PlayerReturn(false);
  }
}
