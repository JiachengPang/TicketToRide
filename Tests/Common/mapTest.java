package Common;

import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.Destination;
import Common.ICity;
import Common.IConnection;
import Common.IDestination;
import Common.TrainMap;
import Common.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the City class implementation:
 * unit tests to ensure that a City can be
 * created as expected and that its methods behave correctly.
 */
public class mapTest {

  ICity atlanta = new City("Atlanta", new Coord(50, 100));
  ICity boston = new City("Boston", new Coord(50, 100));
  ICity chicago = new City("Chicago", new Coord(60, 10));
  ICity denver = new City("Denver", new Coord(50, 400));
  ICity sanfran = new City("San Francisco", new Coord(1, 2));
  ICity seattle = new City("Seattle", new Coord(1, 1));
  ICity miami = new City("Miami", new Coord(8, 8));

  IConnection ATLtoBOS = new Connection(atlanta, boston, Color.RED, 5);
  IConnection BOStoCHI = new Connection(boston, chicago, Color.BLUE, 3);
  IConnection BOStoDEN = new Connection(boston, denver, Color.GREEN, 4);
  IConnection CHItoDEN = new Connection(chicago, denver, Color.WHITE, 5);
  IConnection CHItoDEN2 = new Connection(chicago, denver, Color.GREEN, 3);
  IConnection SEAtoSF = new Connection(seattle, sanfran, Color.RED, 5);

  Set<ICity> citySet = new HashSet<>(Arrays.asList(atlanta, boston, chicago, denver));
  Set<ICity> citySet2 = new HashSet<>(Arrays.asList(
          atlanta, boston, chicago, denver, sanfran, seattle, miami));
  Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
          ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN));
  Set<IConnection> connectionSet2 = new HashSet<>(Arrays.asList(
          ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

  TrainMap map1 = new map(citySet, connectionSet);
  TrainMap map2 = new map(600, 650, citySet2, connectionSet2);
  TrainMap map3 = new map(new HashSet<>(), new HashSet<>());

  @Test
  public void testGetDimension() {
    // Implicitly testing inferDimension
    assertEquals(new Dimension(100, 400), map1.getDimension());
    assertEquals(new Dimension(600, 650), map2.getDimension());
  }

  @Test
  public void testGetName() {
    assertEquals(new HashSet<>(Arrays.asList("Atlanta", "Boston", "Chicago", "Denver")),
            map1.getCityNames());
    assertEquals(new HashSet<>(Arrays.asList(
            "Atlanta", "Boston", "Chicago", "Denver", "San Francisco", "Seattle", "Miami")),
            map2.getCityNames());
  }

  @Test
  public void testCityCoord() {
    assertEquals(new Coord(50, 100), map1.cityCoord("Atlanta"));
    assertEquals(new Coord(50, 100), map1.cityCoord("Boston"));
    assertEquals(new Coord(60, 10), map1.cityCoord("Chicago"));
    assertEquals(new Coord(50, 400), map1.cityCoord("Denver"));
    assertEquals(new Coord(1, 2), map2.cityCoord("San Francisco"));
    assertEquals(new Coord(1, 1), map2.cityCoord("Seattle"));

    try {
      map1.cityCoord("San Francisco");
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("City does not exist.", e.getMessage());
    }
  }

  @Test
  public void testSegmentsBetween() {
    assertEquals(5, map1.segmentsBetween("Atlanta", "Boston", Color.RED));
    assertEquals(-1, map1.segmentsBetween("Atlanta", "Boston", Color.BLUE));
    assertEquals(3, map1.segmentsBetween("Chicago", "Boston", Color.BLUE));
    assertEquals(4, map1.segmentsBetween("Denver", "Boston", Color.GREEN));
    assertEquals(-1, map1.segmentsBetween("Atlanta", "Denver", Color.RED));
    assertEquals(5, map1.segmentsBetween("Chicago", "Denver", Color.WHITE));
  }

  @Test
  public void testColorsBetween() {
    assertEquals(new HashSet<>(Collections.singletonList(Color.RED)),
            map1.colorsBetween("Atlanta", "Boston"));
    assertEquals(new HashSet<>(Collections.singletonList(Color.BLUE)),
            map1.colorsBetween("Chicago", "Boston"));
    assertEquals(new HashSet<>(Collections.singletonList(Color.GREEN)),
            map1.colorsBetween("Denver", "Boston"));
    assertEquals(new HashSet<>(), map1.colorsBetween("Atlanta", "Denver"));
    assertEquals(new HashSet<>(Arrays.asList(Color.WHITE, Color.GREEN)),
            map2.colorsBetween("Chicago", "Denver"));
    assertEquals(new HashSet<>(Collections.singletonList(Color.RED)),
            map1.colorsBetween("Boston", "Atlanta"));
    assertEquals(new HashSet<>(), map1.colorsBetween("Atlanta", "Denver"));
  }

  @Test
  public void testGetDestinations() {
    IDestination dATLtoBOS = new Destination(atlanta, boston);
    IDestination dBOStoCHI = new Destination(boston, chicago);
    IDestination dBOStoDEN = new Destination(boston, denver);
    IDestination dCHItoDEN = new Destination(chicago, denver);
    IDestination dCHItoATL = new Destination(chicago, atlanta);
    IDestination dATLtoDEN = new Destination(atlanta, denver);

    IDestination dSEAtoSF = new Destination(seattle, sanfran);

    Set<IDestination> destSet1 = new HashSet<>(Arrays.asList(
            dATLtoBOS, dBOStoCHI, dBOStoDEN, dCHItoDEN, dCHItoATL, dATLtoDEN));
    Set<IDestination> destSet2 = new HashSet<>(Arrays.asList(
            dATLtoBOS, dBOStoCHI, dBOStoDEN, dCHItoDEN, dCHItoATL, dATLtoDEN, dSEAtoSF));

    assertEquals(destSet1, map1.getDestinations());
    assertEquals(destSet2, map2.getDestinations());
    assertEquals(new HashSet<>(), map3.getDestinations());
  }

  @Test
  public void testGetNeighbors() {
    Set<String> s1 = new HashSet<>(Arrays.asList("Chicago", "Denver", "Atlanta"));
    assertEquals(s1, this.map1.getNeighbors("Boston"));
    Set<String> s2 = new HashSet<>(Arrays.asList("Chicago", "Boston"));
    assertEquals(s2, this.map1.getNeighbors("Denver"));
    Set<String> s3 = new HashSet<>();
    assertEquals(s3, this.map2.getNeighbors("Miami"));
  }

  @Test
  public void testHavePath() {
    assertTrue(map2.havePath("Atlanta", "Denver"));
    assertTrue(map2.havePath("Atlanta", "Boston"));
    assertFalse(map2.havePath("Chicago", "San Francisco"));
    assertFalse(map2.havePath("Miami", "Chicago"));
  }
}





