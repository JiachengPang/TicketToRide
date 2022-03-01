package Admin;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Common.IDestination;

/**
 * Represents a player's assets in the game, including:
 *  - the hand
 *  - number of rails
 *  - destinations
 */
public class PlayerInfo {
  private final Map<Color, Integer> playerHand;
  private final int playerRails;
  private final Set<IDestination> playerDestinations;

  /**
   * Constructor.
   * @param playerHands map of color -> int
   * @param playerRails int
   * @param playerDestinations set of IDestination
   */
  public PlayerInfo(
          Map<Color, Integer> playerHands, int playerRails, Set<IDestination> playerDestinations) {
    this.playerHand = playerHands;
    this.playerRails = playerRails;
    this.playerDestinations = playerDestinations;
  }

  /**
   * Constructor. All values are set to default.
   */
  public PlayerInfo() {
    this.playerHand = new HashMap<>();
    this.playerRails = 0;
    this.playerDestinations = new HashSet<>();
  }

  public Map<Color, Integer> getPlayerHand() {
    return new HashMap<>(this.playerHand);
  }

  public int getPlayerRails() {
    return this.playerRails;
  }

  public Set<IDestination> getPlayerDestinations() {
    return new HashSet<>(this.playerDestinations);
  }
}
