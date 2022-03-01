package Common;

import Player.IPlayer;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A player game state that contains information visible to a particular player.
 */
public class PlayerGameState implements IPlayerGameState {
  private final IGeneralGameState general;
  private final Map<Color, Integer> hand;
  private final int rails;
  private final Set<IDestination> destinations;

  /**
   * Constructor.
   * Constraints:
   *  - all inputs are non-null
   *  - hand only contains valid colors and non-negative # cards
   *  - # rails is non-negative
   *  - destinations are valid
   *
   * @param general general game state
   * @param hand holding of colored cards
   * @param rails # rails
   * @param destinations destinations
   */
  public PlayerGameState(IGeneralGameState general, Map<Color, Integer> hand, int rails,
                         Set<IDestination> destinations) {

    this.checkInput(general, hand, rails, destinations);

    Set<Color> validColors = new HashSet<>(
            Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));
    Map<Color, Integer> actualHand = new HashMap<>(hand);
    for (Color c : validColors) {
      if (!actualHand.containsKey(c)) {
        actualHand.put(c, 0);
      }
    }
    this.general = general;
    this.hand = actualHand;
    this.rails = rails;
    this.destinations = destinations;
  }

  /**
   * Verify that all inputs are valid, throw IllegalArgumentException otherwise.
   * @param general general game state
   * @param hand holding of colored cards
   * @param rails # rails
   * @param destinations destinations
   */
  private void checkInput(IGeneralGameState general, Map<Color, Integer> hand, int rails,
                          Set<IDestination> destinations) {
    if (general == null || hand == null || destinations == null) {
      throw new IllegalArgumentException("All arguments should be non-null.");
    }

    if (rails < 0) {
      throw new IllegalArgumentException("Player is holding negative number of rails.");
    }

    Set<Color> validColors = new HashSet<>(
            Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));
    if (!validColors.containsAll(hand.keySet())) {
      throw new IllegalArgumentException("Player is holding invalid color cards.");
    }

    for (Color c : hand.keySet()) {
      if (hand.get(c) < 0) {
        throw new IllegalArgumentException(
                "Player is holding negative number of cards of a color.");
      }
    }

    if (!general.getMap().getDestinations().containsAll(destinations)) {
      throw new IllegalArgumentException("Player has an invalid destination.");
    }
  }

  @Override
  public TrainMap getMap() {
    return this.general.getMap();
  }

  @Override
  public Map<IPlayer, Set<IConnection>> getAcquiredConnections() {
    return this.general.getAcquiredConnections();
  }

  @Override
  public Set<IConnection> unoccupiedConnections() {
    return this.general.unoccupiedConnections();
  }

  @Override
  public Map<Color, Integer> getHand() {
    return new HashMap<>(this.hand);
  }

  @Override
  public int getRails() {
    return this.rails;
  }

  @Override
  public Set<IDestination> getDestinations() {
    return new HashSet<>(this.destinations);
  }

  @Override
  public Set<IConnection> availableConnections() {
    Set<IConnection> unoccupied = this.unoccupiedConnections();
    Set<IConnection> available = new HashSet<>();
    for (IConnection c : unoccupied) {
      Color color = c.getColor();
      int segs = c.getLength();
      if (this.hand.get(color) >= segs && this.rails >= segs) {
        available.add(c);
      }
    }
    return available;
  }

  @Override
  public IGeneralGameState getGGS() {
    return this.general;
  }

}
