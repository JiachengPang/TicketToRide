package Player;

import org.junit.Test;

import java.awt.*;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.ICity;
import Common.IConnection;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ConnectionComparator
 */
public class ConnectionComparatorTest {
  private final ICity atlanta = new City("Atlanta", new Coord(50, 100));
  private final ICity boston = new City("Boston", new Coord(50, 100));
  private final ICity chicago = new City("Chicago", new Coord(60, 10));

  private final IConnection c1 = new Connection(atlanta, boston, Color.RED, 3);
  private final IConnection c2 = new Connection(atlanta, boston, Color.GREEN, 3);
  private final IConnection c3 = new Connection(atlanta, boston, Color.RED, 4);
  private final IConnection c4 = new Connection(atlanta, chicago, Color.BLUE, 4);
  private final IConnection c5 = new Connection(boston, chicago, Color.RED, 5);

  @Test
  public void testCompare() {
    ConnectionComparator c = new ConnectionComparator();
    assertEquals(-1, c.compare(c1, c4));
    assertEquals(-1, c.compare(c1, c5));
    assertEquals(11, c.compare(c1, c2));
    assertEquals(-1, c.compare(c1, c3));
  }

}
