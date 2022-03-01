package Admin;

import Common.IDestination;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * An IDestinationSelector that shuffles the given destinations randomly
 */
public class RandomDestinationSelector implements IDestinationSelector{
  private Random rand;

  /**
   * Constructor for using the default random seed
   */
  public RandomDestinationSelector() {
    this.rand = new Random();
  }

  /**
   * Constructor for using the given random seed
   * @param seed random seed
   */
  public RandomDestinationSelector(long seed) {
    this.rand = new Random(seed);
  }

  @Override
  public List<IDestination> shuffle(Set<IDestination> destinations) {
    List<IDestination> shuffledDestinations = new ArrayList<>(destinations);
    Collections.shuffle(shuffledDestinations, rand);
    return shuffledDestinations;
  }
}
