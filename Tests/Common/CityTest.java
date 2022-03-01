package Common;

import org.junit.Test;

import Common.City;
import Common.Coord;
import Common.ICity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for the City class implementation:
 * unit tests to ensure that a City can be
 * created as expected and that its methods behave correctly.
 */
public class CityTest {

  private final ICity atlanta1 = new City("Atlanta", new Coord(50, 100));
  private final ICity atlanta2 = new City("Atlanta", new Coord(1, 1));

  private final ICity boston1 = new City("Boston", new Coord(50, 100));
  private final ICity boston2 = new City("Boston", new Coord(1, 1));
  private final ICity boston3 = new City("Boston", new Coord(80, 20));

  private final ICity chicago = new City("Chicago", new Coord(60, 10));
  private final ICity denver = new City("Denver", new Coord(50, 400));
  private final ICity sanfran = new City("San Francisco", new Coord(1, 2));

  @Test
  public void testGetCoord() {
    assertEquals(new Coord(50, 100), atlanta1.getCoord());
    assertEquals(new Coord(1, 2), sanfran.getCoord());
    assertEquals(new Coord(1, 1), boston2.getCoord());
    assertNotEquals(new Coord(2, 2), boston2.getCoord());
  }

  @Test
  public void testGetName() {
    assertEquals("Atlanta", atlanta1.getName());
    assertEquals("San Francisco", sanfran.getName());
    assertEquals("Boston", boston2.getName());
  }

  @Test
  public void testEquals() {
    assertEquals(atlanta1, atlanta1);
    assertEquals(atlanta1, atlanta2);
    assertEquals(boston1, boston3);
    assertEquals(boston1, boston1);

    assertNotEquals(atlanta1, boston1);
    assertNotEquals(atlanta1, chicago);
    assertNotEquals(atlanta1, sanfran);
    assertNotEquals(sanfran, boston1);
    assertNotEquals(sanfran, denver);
  }

  @Test
  public void testHashCode() {
    assertEquals(atlanta1.hashCode(), atlanta1.hashCode());
    assertEquals(atlanta1.hashCode(), atlanta2.hashCode());
    assertEquals(boston1.hashCode(), boston3.hashCode());
    assertNotEquals(boston1.hashCode(), atlanta1.hashCode());
    assertNotEquals(boston1.hashCode(), sanfran.hashCode());
  }
}





