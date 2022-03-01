package Admin;

import Common.IDestination;
import java.util.List;
import java.util.Set;

/**
 * Function object to decide the sequence of a set of IDestinations
 */
public interface IDestinationSelector {

  /**
   * Shuffle the set of destinations
   * @param destinations destinations
   * @return shuffled destinations
   */
  public List<IDestination> shuffle(Set<IDestination> destinations);

}
