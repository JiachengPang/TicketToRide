package Admin;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import Player.IPlayer;
import Player.Player;
import Player.BuyNowStrategy;
import Player.Hold10Strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for RefereeGameState
 */
public class RefereeGameStateTest {

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

  List<Color> deck;
  Map<IPlayer, Map<Color, Integer>> playerHands;
  Map<IPlayer, Integer> playerRails;
  Map<IPlayer, Set<IDestination>> playerDestinations;
  Map<IPlayer, PlayerInfo> allPlayerInfo;
  List<IPlayer> turnOrder;
  TrainMap map;
  IGeneralGameState ggs;
  IPlayerGameState pgs;
  IRefereeGameState rgs;
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
    turnOrder = new ArrayList<>(Arrays.asList(p2, p1));
    Map<IPlayer, Set<IConnection>> acquired = new HashMap<>();
    acquired.put(p1, new HashSet<>(Arrays.asList(ATLtoBOS, BOStoCHI)));
    acquired.put(p2, new HashSet<>());

    this.ggs = new GeneralGameState(this.map, acquired);
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.BLUE, 1);

    this.pgs = new PlayerGameState(this.ggs, hand, 5, destinations);

    deck = new ArrayList<>(Arrays.asList(Color.RED, Color.WHITE, Color.WHITE));

    playerHands = new HashMap<>();
    playerHands.put(p2, hand);

    playerRails = new HashMap<>();
    playerRails.put(p2, 5);

    playerDestinations = new HashMap<>();
    playerDestinations.put(p2, destinations);
    allPlayerInfo = new HashMap<>();
    allPlayerInfo.put(p2, new PlayerInfo(hand, 5, destinations));

    this.rgs = new RefereeGameState(ggs, deck, allPlayerInfo, turnOrder, 0);

  }

  @Test
  public void testConstructor() {
    reset();
    try {
      new RefereeGameState(null, deck, allPlayerInfo, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("All arguments should be non-null.", e.getMessage());
    }
    List<Color> deck2 = new ArrayList<>();
    deck2.add(Color.BLACK);
    try {
      new RefereeGameState(this.ggs, deck2, allPlayerInfo, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("The deck contains invalid color cards.", e.getMessage());
    }
    Map<IPlayer, PlayerInfo> wrongInfo = new HashMap<>();
    wrongInfo.put(new Player(new Hold10Strategy()), new PlayerInfo());
    try {
      new RefereeGameState(this.ggs, deck, wrongInfo, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player information does not match player id.", e.getMessage());
    }
    Map<IPlayer, PlayerInfo> wrongInfo2 = new HashMap<>();
    Map<Color, Integer> hands2 = new HashMap<>();
    hands2.put(Color.BLACK, 1);
    wrongInfo2.put(p1, new PlayerInfo(hands2, 5, new HashSet<>()));
    try {
      new RefereeGameState(this.ggs, deck, wrongInfo2, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding an invalid color card.", e.getMessage());
    }
    Map<IPlayer, PlayerInfo> wrongInfo3 = new HashMap<>();
    Map<Color, Integer> hands3 = new HashMap<>();
    hands3.put(Color.GREEN, -1);
    wrongInfo3.put(p1, new PlayerInfo(hands3, 5, new HashSet<>()));
    try {
      new RefereeGameState(this.ggs, deck, wrongInfo3, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding a negative number of cards of a color.",
              e.getMessage());
    }
    Map<IPlayer, PlayerInfo> wrongInfo4 = new HashMap<>();
    wrongInfo4.put(p1, new PlayerInfo(new HashMap<>(), -1, new HashSet<>()));
    try {
      new RefereeGameState(this.ggs, deck, wrongInfo4, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player is holding negative number of rails.", e.getMessage());
    }
    Map<IPlayer, PlayerInfo> wrongInfo5 = new HashMap<>();
    Set<IDestination> wrongDestination =
            new HashSet<>(Collections.singletonList(new Destination(miami, atlanta)));
    wrongInfo5.put(p1, new PlayerInfo(new HashMap<>(), 1, wrongDestination));
    try {
      new RefereeGameState(this.ggs, deck, wrongInfo5, turnOrder, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Player has an invalid destination.", e.getMessage());
    }
    List<IPlayer> wrongTurns = new ArrayList<>(Arrays.asList(p1, p2, p1));
    try {
      new RefereeGameState(this.ggs, deck, allPlayerInfo, wrongTurns, 0);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid turn order: duplicate turns.", e.getMessage());
    }
    try {
      new RefereeGameState(this.ggs, deck, allPlayerInfo, turnOrder, 2);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("Current turn is not recognized.", e.getMessage());
    }
  }

  @Test
  public void testGetDeck() {
    reset();
    assertEquals(deck, this.rgs.getDeck());
  }

  @Test
  public void testGetPlayerHand() {
    reset();
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.BLUE, 1);
    hand.put(Color.GREEN, 0);
    hand.put(Color.WHITE, 0);
    assertEquals(hand, this.rgs.getPlayerHand());
  }

  @Test
  public void testGetPlayerRails() {
    reset();
    assertEquals(5, this.rgs.getPlayerRails());
  }

  @Test
  public void testGetPlayerDestinations() {
    reset();
    assertEquals(destinations, this.rgs.getPlayerDestination());
  }

  @Test
  public void testLegalAcquisition() {
    reset();
    assertTrue(this.rgs.legalAcquisition(SEAtoSF));
    assertFalse(this.rgs.legalAcquisition(ATLtoBOS));
    assertFalse(this.rgs.legalAcquisition(BOStoDEN));
    IConnection invalid = new Connection(miami, atlanta, Color.RED, 3);
    assertFalse(this.rgs.legalAcquisition(invalid));
  }

  @Test
  public void testCurrentPlayerGameState() {
    reset();
    IPlayerGameState pgs2 = this.rgs.currentPlayerGameState();
    assertEquals(this.pgs.getDestinations(), pgs2.getDestinations());
    assertEquals(this.pgs.getHand(), pgs2.getHand());
    assertEquals(this.pgs.getRails(), pgs2.getRails());
  }


}
