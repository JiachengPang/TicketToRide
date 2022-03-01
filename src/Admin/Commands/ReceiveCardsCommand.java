package Admin.Commands;

import Admin.PlayerReturn;
import Player.IPlayer;
import java.awt.Color;
import java.util.Map;

/**
 * Command for a player receiving cards from the referee
 */
public class ReceiveCardsCommand implements PlayerCommand {
  private final IPlayer player;
  private final Map<Color, Integer> cards;

  /**
   * Constructor
   * @param player the player
   * @param cards the cards to give to the player
   */
  public ReceiveCardsCommand(IPlayer player, Map<Color, Integer> cards) {
    if (player == null || cards == null) {
      throw new IllegalArgumentException("Inputs cannot be null");
    }
    this.player = player;
    this.cards = cards;
  }

  @Override
  public PlayerReturn execute() {
    player.receiveCards(cards);
    return new PlayerReturn(false);
  }
}
