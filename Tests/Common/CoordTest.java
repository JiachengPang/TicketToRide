package Common;

import org.junit.Test;

import Common.Coord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Tests for Coord.
 */
public class CoordTest {
  private final Coord c1 = new Coord(0, 0);
  private final Coord c2 = new Coord(0, 0);
  private final Coord c3 = new Coord(1, 0);
  private final Coord c4 = new Coord(100, 200);

  @Test
  public void testConstructor() {
    try {
      new Coord(-1, -1);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Coordinates must be non-negative.", e.getMessage());
    }
    try {
      new Coord(1, -1);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Coordinates must be non-negative.", e.getMessage());
    }
    try {
      new Coord(-1, 1);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Coordinates must be non-negative.", e.getMessage());
    }
  }

  @Test
  public void testEquals() {
    assertEquals(c1, c2);
    assertEquals(c1, c1);
    assertNotEquals(c1, c3);
    assertNotEquals(c1, c4);
    assertNotEquals(c3, c4);
  }

}
