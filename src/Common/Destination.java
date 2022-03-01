package Common;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An unordered pair of cities that has a path between them.
 * Enforced by the TrainMap it belongs to.
 */
public class Destination implements IDestination {
  private final Set<ICity> set;

  /**
   * Constructor.
   * @param first a city
   * @param second a city
   */
  public Destination(ICity first, ICity second) {
    this.set = new HashSet<>();
    this.set.add(first);
    this.set.add(second);
  }

  @Override
  public Set<ICity> getCities() {
    return new HashSet<>(this.set);
  }

  /**
   * 2 Destinations are the same if they contain the same cities, regardless of their order.
   * @param o Object
   * @return true if 2 destinations contain the same cities, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Destination that = (Destination) o;
    return Objects.equals(set, that.set);
  }

  @Override
  public int hashCode() {
    return Objects.hash(set);
  }
}
