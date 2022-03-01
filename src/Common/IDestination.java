package Common;

import java.util.Set;

/**
 * An unordered pair of ICity that has a path between them.
 */
public interface IDestination {

  /**
   * Get the pair of cities.
   * @return a Set of ICity of length 2
   */
  Set<ICity> getCities();
}
