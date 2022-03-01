package Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import Common.Destination;
import Common.ICity;
import Common.IConnection;
import Common.IDestination;

/**
 * Compares 2 IConnection objects in the following order:
 *  - the ICity pair in lexicographic order
 *  - the length in ascending order
 *  - the color string in lexicographic order
 */
public class ConnectionComparator implements Comparator<IConnection> {
  @Override
  public int compare(IConnection o1, IConnection o2) {
    DestinationComparator cpc = new DestinationComparator();
    List<ICity> c1 = new ArrayList<>(o1.getCities());
    IDestination d1 = new Destination(c1.get(0), c1.get(1));
    List<ICity> c2 = new ArrayList<>(o2.getCities());
    IDestination d2 = new Destination(c2.get(0), c2.get(1));
    if (cpc.compare(d1, d2) != 0) {
      return cpc.compare(d1, d2);
    }
    if (o1.getLength() != o2.getLength()) {
      return o1.getLength() - o2.getLength();
    }
    return this.colorString(o1.getColor()).compareTo(this.colorString(o2.getColor()));
  }

  /**
   * Convert a valid Color object to a string.
   * @param c Color
   * @return String for c
   */
  private String colorString(Color c) {
    if (c == Color.RED) return "RED";
    else if (c == Color.BLUE) return "BLUE";
    else if (c == Color.GREEN) return "GREEN";
    else if (c == Color.WHITE) return "WHITE";
    else throw new IllegalArgumentException("Invalid color.");
  }
}
