package Common;

import org.junit.Test;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.ICity;
import Common.IConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for IConnection.
 */
public class ConnectionTest {
  private final ICity atlanta = new City("Atlanta", new Coord(50, 100));
  private final ICity boston = new City("Boston", new Coord(50, 100));
  private final ICity chicago = new City("Chicago", new Coord(60, 10));

  private final IConnection c1 = new Connection(atlanta, boston, Color.RED, 3);
  private final IConnection c2 = new Connection(atlanta, chicago, Color.BLUE, 4);
  private final IConnection c3 = new Connection(boston, chicago, Color.RED, 5);

  @Test
  public void testConstructor() {
    try {
      new Connection(atlanta, atlanta, Color.RED, 4);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("A connection has to connect 2 different cities.", e.getMessage());
    }
    try {
      new Connection(atlanta, boston, Color.BLACK, 4);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Color must be red, blue, green, or white.", e.getMessage());
    }
    try {
      new Connection(atlanta, boston, Color.RED, 2);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("A connection can only have 3, 4, or 5 segments.", e.getMessage());
    }
    try {
      new Connection(atlanta, boston, Color.RED, 6);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("A connection can only have 3, 4, or 5 segments.", e.getMessage());
    }
  }

  @Test
  public void getCities() {
    Set<ICity> s1 = new HashSet<>();
    s1.add(atlanta);
    s1.add(boston);
    assertEquals(s1, c1.getCities());
    Set<ICity> s2 = new HashSet<>();
    s2.add(atlanta);
    s2.add(chicago);
    assertEquals(s2, c2.getCities());
  }

  @Test
  public void getColor() {
    assertEquals(Color.RED, c1.getColor());
    assertEquals(Color.BLUE, c2.getColor());
    assertEquals(Color.RED, c3.getColor());
  }

  @Test
  public void getLength() {
    assertEquals(3, c1.getLength());
    assertEquals(4, c2.getLength());
    assertEquals(5, c3.getLength());
  }

  @Test
  public void connectedTo() {
    assertEquals(atlanta, c1.connectedTo("Boston"));
    assertEquals(boston, c1.connectedTo("Atlanta"));
    assertEquals(chicago, c2.connectedTo("Atlanta"));
    assertEquals(atlanta, c2.connectedTo("Chicago"));
  }

  @Test
  public void contains() {
    assertTrue(c1.contains("Boston"));
    assertTrue(c1.contains("Atlanta"));
    assertFalse(c1.contains("Chicago"));
  }
}
