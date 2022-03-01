package Player;

import org.junit.Test;

import Common.City;
import Common.Coord;
import Common.ICity;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ICItyComparator
 */
public class ICityComparatorTest {
  private final ICity atlanta1 = new City("Atlanta", new Coord(50, 100));
  private final ICity atlanta2 = new City("Atlanta", new Coord(1, 1));

  private final ICity boston1 = new City("Boston", new Coord(50, 100));

  private final ICity chicago = new City("Chicago", new Coord(60, 10));
  private final ICity denver = new City("Denver", new Coord(50, 400));

  @Test
  public void testCompare() {
    ICityComparator c = new ICityComparator();
    assertEquals(0, c.compare(atlanta1, atlanta2));
    assertEquals(-1, c.compare(atlanta1, boston1));
    assertEquals(-2, c.compare(atlanta1, chicago));
    assertEquals(-3, c.compare(atlanta1, denver));
    assertEquals(2, c.compare(denver, boston1));

  }
}
