package Player;

import Common.IDestination;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Common.ICity;

/**
 * Compares 2 ICity pairs in lexicographic order in terms of city names.
 * Compare the first value, if same, compare the second value.
 */
public class DestinationComparator implements Comparator<IDestination> {
  @Override
  public int compare(IDestination o1, IDestination o2) {
    List<ICity> cityPair1 = new ArrayList<>(o1.getCities());
    List<ICity> cityPair2 = new ArrayList<>(o2.getCities());
    cityPair1.sort(new ICityComparator());
    cityPair2.sort(new ICityComparator());
    if (cityPair1.get(0).getName().compareTo(cityPair2.get(0).getName()) == 0) {
      return cityPair1.get(1).getName().compareTo(cityPair2.get(1).getName());
    } else {
      return cityPair1.get(0).getName().compareTo(cityPair2.get(0).getName());
    }
  }
}
