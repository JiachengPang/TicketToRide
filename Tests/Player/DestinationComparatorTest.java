package Player;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Common.City;
import Common.Coord;
import Common.Destination;
import Common.ICity;
import Common.IDestination;

import static org.junit.Assert.assertEquals;

/**
 * Tests for DestinationComparator
 */
public class DestinationComparatorTest {
  ICity atlanta1 = new City("Atlanta", new Coord(50, 100));
  ICity atlanta2 = new City("Atlanta", new Coord(1, 1));

  ICity boston1 = new City("Boston", new Coord(50, 100));

  ICity chicago = new City("Chicago", new Coord(60, 10));
  ICity denver = new City("Denver", new Coord(50, 400));

  IDestination d1 = new Destination(atlanta1, boston1);
  IDestination d2 = new Destination(boston1, atlanta1);
  IDestination d3 = new Destination(atlanta1, boston1);
  IDestination d4 = new Destination(atlanta1, denver);

  @Test
  public void testCompare() {
    DestinationComparator c = new DestinationComparator();
    assertEquals(0, c.compare(d1, d2));
    assertEquals(-2, c.compare(d1, d4));
    assertEquals(0, c.compare(d1, d3));
  }
}
