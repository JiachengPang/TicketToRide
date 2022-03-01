package Common;

import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Player.IPlayer;
import Player.Player;
import Player.Hold10Strategy;
import Player.BuyNowStrategy;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for PlayerGameState.
 */
public class PlayerGameStateTest {
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

  Set<IDestination> destinations = new HashSet<>(Arrays.asList(
          new Destination(atlanta, boston), new Destination(atlanta, denver)));

  TrainMap map;
  IGeneralGameState ggs;
  IPlayerGameState pgs;
  IPlayer p1;
  IPlayer p2;

  private void reset() {

    Set<ICity> citySet = new HashSet<>(Arrays.asList(
            atlanta, boston, chicago, denver, sanfran, seattle, miami));
    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
            ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

    this.map = new map(600, 650, citySet, connectionSet);

    p1 = new Player(new Hold10Strategy());
    p2 = new Player(new BuyNowStrategy());

    Map<IPlayer, Set<IConnection>> acquired = new HashMap<>();
    acquired.put(p1, new HashSet<>(Arrays.asList(ATLtoBOS, BOStoCHI)));
    acquired.put(p2, new HashSet<>());

    this.ggs = new GeneralGameState(this.map, acquired);
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.GREEN, 3);

    this.pgs = new PlayerGameState(this.ggs, hand, 5, destinations);
  }

  @Test
  public void testConstructor() {
     reset();
    try {
      new PlayerGameState(null, null, 1, null);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("All arguments should be non-null.", e.getMessage());
    }
    try {
      new PlayerGameState(this.ggs, new HashMap<>(), -1, new HashSet<>());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding negative number of rails.", e.getMessage());
    }
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.BLACK, 1);
    try {
      new PlayerGameState(this.ggs, hand, 1, new HashSet<>());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding invalid color cards.", e.getMessage());
    }
    Map<Color, Integer> hand2 = new HashMap<>();
    hand2.put(Color.GREEN, -1);
    try {
      new PlayerGameState(this.ggs, hand2, 1, new HashSet<>());
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding negative number of cards of a color.",
              e.getMessage());
    }
    Set<IDestination> destinations = new HashSet<>();
    destinations.add(new Destination(miami, atlanta));
    try {
      new PlayerGameState(this.ggs, new HashMap<>(), 1, destinations);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player has an invalid destination.",
              e.getMessage());
    }
  }

  @Test
  public void testGetHand() {
    reset();
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.BLUE, 0);
    hand.put(Color.GREEN, 3);
    hand.put(Color.WHITE, 0);
    assertEquals(hand, this.pgs.getHand());
  }

  @Test
  public void testGetDestinations() {
    reset();
    assertEquals(this.destinations, this.pgs.getDestinations());
  }

  @Test
  public void testAvailableConnections() {
    reset();
    Set<IConnection> c = new HashSet<>(Arrays.asList(CHItoDEN2, SEAtoSF));
    assertEquals(c, this.pgs.availableConnections());
  }

}
