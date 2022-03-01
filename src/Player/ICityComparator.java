package Player;

import java.util.Comparator;

import Common.ICity;

/**
 * Compares 2 ICity objects in lexicographic order in terms of the city names
 */
public class ICityComparator implements Comparator<ICity> {
  @Override
  public int compare(ICity o1, ICity o2) {
    return o1.getName().compareTo(o2.getName());
  }
}
