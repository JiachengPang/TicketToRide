package Admin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * An ICardSelector that shuffles the deck randomly
 */
public class RandomCardSelector implements ICardSelector {
  private Random rand;

  /**
   * Constructor for using default random seed
   */
  public RandomCardSelector() {
    this.rand = new Random();
  }

  /**
   * Constructor for using a given random seed
   * @param seed random seed
   */
  public RandomCardSelector(long seed) {
    this.rand = new Random(seed);
  }

  @Override
  public List<Color> shuffle(List<Color> deck) {
    List<Color> shuffledDeck = new ArrayList<>(deck);
    Collections.shuffle(shuffledDeck, rand);
    return shuffledDeck;
  }
}
