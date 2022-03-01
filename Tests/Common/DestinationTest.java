package Common;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import Common.City;
import Common.Coord;
import Common.Destination;
import Common.ICity;
import Common.IDestination;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests for IDestination
 */
public class DestinationTest {
  private final ICity atlanta = new City("Atlanta", new Coord(50, 100));
  private final ICity boston = new City("Boston", new Coord(50, 100));
  private final ICity chicago = new City("Chicago", new Coord(60, 10));

  private final IDestination d1 = new Destination(atlanta, boston);
  private final IDestination d2 = new Destination(boston, atlanta);
  private final IDestination d3 = new Destination(atlanta, chicago);

  @Test
  public void testGetCities() {
    Set<ICity> s1 = new HashSet<>();
    s1.add(atlanta);
    s1.add(boston);
    assertEquals(s1, d1.getCities());
    assertEquals(s1, d2.getCities());
    Set<ICity> s2 = new HashSet<>();
    s2.add(chicago);
    s2.add(atlanta);
    assertEquals(s2, d3.getCities());
  }

  @Test
  public void testEquals() {
    assertEquals(d1, d2);
    assertNotEquals(d2, d3);
    assertEquals(d1, d1);
  }

  @Test
  public void testHashCode() {
    assertEquals(d1.hashCode(), d2.hashCode());
    assertNotEquals(d2.hashCode(), d3.hashCode());
    assertEquals(d1.hashCode(), d1.hashCode());
  }

}
