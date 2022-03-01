package Common;

import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import Player.Hold10Strategy;
import Player.BuyNowStrategy;
import Player.IPlayer;
import Player.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for GeneralGameState
 */
public class GeneralGameStateTest {
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

  TrainMap map;
  IGeneralGameState gs;
  IPlayer p1;
  IPlayer p2;

  private void reset() {

    Set<ICity> citySet = new HashSet<>(Arrays.asList(
            atlanta, boston, chicago, denver, sanfran, seattle, miami));
    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
            ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

    this.map = new map(600, 650, citySet, connectionSet);

    Map<IPlayer, Set<IConnection>> acquired = new HashMap<>();
    p1 = new Player(new Hold10Strategy());
    p2 = new Player(new BuyNowStrategy());
    acquired.put(p1, new HashSet<>(Arrays.asList(ATLtoBOS, BOStoCHI)));
    acquired.put(p2, new HashSet<>());

    this.gs = new GeneralGameState(this.map, acquired);
  }

  @Test
  public void testConstructor() {
    reset();
    try {
      new GeneralGameState(null, new HashMap<>());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("All arguments should be non-null.", e.getMessage());
    }

    Map<IPlayer, Set<IConnection>> acquired2 = new HashMap<>();
    acquired2.put(p1, new HashSet<>(Collections.singletonList(ATLtoBOS)));
    acquired2.put(p2, new HashSet<>(Collections.singletonList(ATLtoBOS)));
    try {
      new GeneralGameState(this.map, acquired2);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("A connection is acquired by more than 1 player.", e.getMessage());
    }
    Map<IPlayer, Set<IConnection>> acquired3 = new HashMap<>();
    acquired2.put(p1, new HashSet<>(
            Collections.singletonList(new Connection(miami, sanfran, Color.RED, 4))));
    try {
      new GeneralGameState(this.map, acquired2);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player acquired a non-existent connection.", e.getMessage());
    }

  }

  @Test
  public void testGetMap() {
    reset();
    TrainMap map2 = this.gs.getMap();
    assertEquals(this.map.getCityNames(), map2.getCityNames());
    assertEquals(this.map.getDimension(), map2.getDimension());
    assertEquals(this.map.getDestinations(), map2.getDestinations());
    assertEquals(this.map.getAllConnections(), map2.getAllConnections());
    for (String name : map2.getCityNames()) {
      assertEquals(this.map.cityCoord(name), map2.cityCoord(name));
    }
  }

  @Test
  public void testUnoccpupiedConnections() {
    reset();
    Set<IConnection> set1 = new HashSet<>(Arrays.asList(
            BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));
    assertEquals(set1, this.gs.unoccupiedConnections());
  }
}
