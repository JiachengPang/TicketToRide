package Admin;

import java.awt.Color;
import java.util.List;

/**
 * Function object to decide the sequence of a given deck.
 */
public interface ICardSelector {

  /**
   * shuffle the deck
   * @param deck deck
   * @return shuffled deck
   */
  public List<Color> shuffle(List<Color> deck);

}
