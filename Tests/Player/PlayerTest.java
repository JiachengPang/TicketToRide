package Player;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.Destination;
import Common.GeneralGameState;
import Common.ICity;
import Common.IConnection;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.IPlayerGameState;
import Common.PlayerGameState;
import Common.TrainMap;
import Common.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for Player
 */
public class PlayerTest {

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
          new Destination(atlanta, boston), new Destination(atlanta, denver),
          new Destination(chicago, boston), new Destination(chicago,atlanta),
          new Destination(boston, denver)));

  TrainMap map;
  IGeneralGameState ggs;
  IPlayerGameState pgs;
  Strategy hold10 = new Hold10Strategy();
  Strategy buyNow = new BuyNowStrategy();
  IPlayer hold10Player;
  IPlayer buyNowPlayer;

  private void reset() {

    Set<ICity> citySet = new HashSet<>(Arrays.asList(
            atlanta, boston, chicago, denver, sanfran, seattle, miami));
    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
            ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

    this.map = new map(600, 650, citySet, connectionSet);

    hold10Player = new Player(hold10);
    hold10Player.setup(map, 45, new HashMap<>());
    buyNowPlayer = new Player(buyNow);
    buyNowPlayer.setup(map, 45, new HashMap<>());

    Map<IPlayer, Set<IConnection>> acquired = new HashMap<>();
    acquired.put(hold10Player, new HashSet<>(Arrays.asList(ATLtoBOS, BOStoCHI)));
    acquired.put(buyNowPlayer, new HashSet<>());

    this.ggs = new GeneralGameState(this.map, acquired);
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.GREEN, 3);

    this.pgs = new PlayerGameState(this.ggs, hand, 5, new HashSet<>());
  }

  @Test
  public void testPickDestinations() {
    reset();
    Set<IDestination> picked1 = new HashSet<>(Arrays.asList(
            new Destination(atlanta, boston), new Destination(atlanta, chicago)));
    Set<IDestination> returned1 = new HashSet<>(destinations);
    returned1.removeAll(picked1);
    assertEquals(returned1, this.hold10Player.pick(this.destinations));

    Set<IDestination> picked2 = new HashSet<>(Arrays.asList(
            new Destination(boston, denver), new Destination(chicago, boston)));
    Set<IDestination> returned2 = new HashSet<>(destinations);
    returned2.removeAll(picked2);
    assertEquals(returned2, this.buyNowPlayer.pick(this.destinations));
    try {
      assertEquals(new HashSet<>(), this.hold10Player.pick(new HashSet<>()));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Not enough destinations to pick from.", e.getMessage());
    }
    try {
      assertEquals(new HashSet<>(), this.buyNowPlayer.pick(new HashSet<>()));
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Not enough destinations to pick from.", e.getMessage());
    }
  }

  @Test
  public void testHold10MakeMove() {
    reset();
    assertEquals(new Action(true), this.hold10Player.play(this.pgs));
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.GREEN, 3);
    hand.put(Color.BLUE, 5);
    this.pgs = new PlayerGameState(this.ggs, hand, 5, new HashSet<>());
    assertEquals(new Action(CHItoDEN2), this.hold10Player.play(this.pgs));
    this.pgs = new PlayerGameState(this.ggs, hand, 1, new HashSet<>());
    assertEquals(new Action(true), this.hold10Player.play(this.pgs));
  }

  @Test
  public void testBuyNowMakeMove() {
    reset();
    assertEquals(new Action(CHItoDEN2), this.buyNowPlayer.play(this.pgs));
    this.pgs = new PlayerGameState(this.ggs, new HashMap<>(), 5, new HashSet<>());
    assertEquals(new Action(true), this.buyNowPlayer.play(this.pgs));
  }
}
