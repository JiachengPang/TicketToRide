package Admin;

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
import Player.Strategy;
import Player.Hold10Strategy;
import Player.BuyNowStrategy;
import Player.CheatingStrategy;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for Manager
 */
public class ManagerTest {
  ICity atlanta = new City("Atlanta", new Coord(50, 100));
  ICity boston = new City("Boston", new Coord(50, 100));
  ICity chicago = new City("Chicago", new Coord(60, 10));
  ICity denver = new City("Denver", new Coord(50, 400));
  ICity sanfran = new City("San Francisco", new Coord(1, 2));
  ICity seattle = new City("Seattle", new Coord(1, 1));
  ICity miami = new City("Miami", new Coord(8, 8));

  IConnection ATLtoBOS = new Connection(atlanta, boston, Color.RED, 4);
  IConnection BOStoCHI = new Connection(boston, chicago, Color.BLUE, 3);
  IConnection BOStoDEN = new Connection(boston, denver, Color.GREEN, 3);
  IConnection CHItoDEN = new Connection(chicago, denver, Color.BLUE, 5);
  IConnection CHItoDEN2 = new Connection(chicago, denver, Color.GREEN, 3);
  IConnection SEAtoSF = new Connection(seattle, sanfran, Color.RED, 5);

  Set<IDestination> destinations = new HashSet<>(Arrays.asList(
      new Destination(atlanta, boston), new Destination(atlanta, denver)));

  List<Color> deck;
  List<Color> whiteDeck;
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
  IManager manager;

  private static class SlowPoke extends Player {

    public SlowPoke(Strategy strategy) {
      super(strategy);
    }

    @Override
    public void setup(TrainMap map, int rails, Map<Color, Integer> cards) {
      try {
        Thread.sleep(40000);
      } catch (InterruptedException ie) {
      }
    }

    @Override
    public TrainMap start() {
      try {
        Thread.sleep(40000);
      } catch (InterruptedException ie) {
      }
      return null;
    }
  }

  private static class BrokenPlayer extends Player {

    public BrokenPlayer(Strategy strategy) {
      super(strategy);
    }

    @Override
    public void setup(TrainMap map, int rails, Map<Color, Integer> cards) {
      throw new IllegalArgumentException("I want different cards");
    }
  }

  private static class RegularCardSelector implements ICardSelector {

    @Override
    public List<Color> shuffle(List<Color> deck) {
      return deck;
    }
  }

  @Before
  public void reset() {
    Set<ICity> citySet = new HashSet<>(Arrays.asList(
        atlanta, boston, chicago, denver, sanfran, seattle, miami));
    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
        ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

    this.map = new map(600, 650, citySet, connectionSet);

    Map<IPlayer, Set<IConnection>> acquired = new HashMap<>();
    p1 = new Player(new BuyNowStrategy());
    p2 = new Player(new BuyNowStrategy());
    turnOrder = new ArrayList<>(Arrays.asList(p1, p2));

    acquired.put(p1, new HashSet<>(Arrays.asList(ATLtoBOS, BOStoCHI)));
    acquired.put(p2, new HashSet<>());

    this.ggs = new GeneralGameState(this.map, acquired);
    Map<Color, Integer> hand = new HashMap<>();
    hand.put(Color.RED, 5);
    hand.put(Color.BLUE, 1);

    this.pgs = new PlayerGameState(this.ggs, hand, 5, destinations);

    deck = new ArrayList<>(Arrays.asList(Color.RED, Color.RED, Color.RED, Color.RED, Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN));
    whiteDeck = new ArrayList<>();
    for (int i = 0; i < 250; i++) {
      whiteDeck.add(Color.WHITE);
    }
    manager = new Manager(turnOrder, deck, new RandomCardSelector(), new RandomDestinationSelector());
  }

  @Test
  public void testAllocatePlayers() {
    List<IPlayer> playerList = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      playerList.add(new Player(new Hold10Strategy()));
    }
    manager = new Manager(playerList, deck, new RandomCardSelector(), new RandomDestinationSelector());
    List<List<IPlayer>> allocatedList = manager.allocatePlayers();
    assertEquals(4, allocatedList.size());
    for (int i = 0; i < 4; i++) {
      if (i != 3) {
        assertEquals(8, allocatedList.get(i).size());
        for (int j = 0; j < 8; j++) {
          assertEquals(playerList.get(i * 8 + j), allocatedList.get(i).get(j));
        }
      } else {
        assertEquals(6, allocatedList.get(i).size());
        for (int j = 0; j < 6; j++) {
          assertEquals(playerList.get(i * 8 + j), allocatedList.get(i).get(j));
        }
      }
    }
  }

  @Test
  public void testAllocatePlayersBacktracking() {
    List<IPlayer> playerList = new ArrayList<>();
    for (int i = 0; i < 25; i++) {
      playerList.add(new Player(new Hold10Strategy()));
    }
    manager = new Manager(playerList, deck, new RandomCardSelector(), new RandomDestinationSelector());
    List<List<IPlayer>> allocatedList = manager.allocatePlayers();
    assertEquals(4, allocatedList.size());
    for (int i = 0; i < 4; i++) {
      if (i == 2) {
        assertEquals(7, allocatedList.get(i).size());
        for (int j = 0; j < 7; j++) {
          assertEquals(playerList.get(i * 8 + j), allocatedList.get(i).get(j));
        }
      } else if (i == 3){
        assertEquals(2, allocatedList.get(i).size());
        assertEquals(playerList.get(23), allocatedList.get(i).get(0));
        assertEquals(playerList.get(24), allocatedList.get(i).get(1));
      } else {
        assertEquals(8, allocatedList.get(i).size());
        for (int j = 0; j < 8; j++) {
          assertEquals(playerList.get(i * 8 + j), allocatedList.get(i).get(j));
        }
      }
    }
  }

  @Test
  public void testSetup() {
    TrainMap chosenMap = manager.setup();
    assertEquals(map.getCities(), chosenMap.getCities());
    assertEquals(map.getAllConnections(), chosenMap.getAllConnections());
    List<IPlayer> slowPlayers = new ArrayList<>(Arrays.asList(new SlowPoke(new Hold10Strategy()), new SlowPoke(new Hold10Strategy())));
    manager = new Manager(slowPlayers, deck, new RandomCardSelector(), new RandomDestinationSelector());
    try {
      manager.setup();
      fail();
    } catch (RuntimeException e) {
      assertEquals("All players have malfunctioned", e.getMessage());
    }
  }

  @Test
  public void testStart() {
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, p2)), whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.start();
    List<IPlayer> winners = manager.getWinners();
    List<IPlayer> kicked = manager.getKickedPlayers();
    assertEquals(2, winners.size());
    assertTrue(winners.contains(p1));
    assertTrue(winners.contains(p2));
    assertEquals(0, kicked.size());
  }

  @Test
  public void testStartCheating() {
    IPlayer cheater = new Player(new CheatingStrategy());
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, cheater)), whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.start();
    List<IPlayer> winners = manager.getWinners();
    List<IPlayer> kicked = manager.getKickedPlayers();
    assertEquals(1, winners.size());
    assertTrue(winners.contains(p1));
    assertEquals(1, kicked.size());
    assertTrue(kicked.contains(cheater));
  }

  @Test
  public void testNextRound() {
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, p2)), whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.setup();
    List<IPlayer> survivors = manager.startNextRound();
    assertEquals(2, survivors.size());
    assertTrue(survivors.contains(p1));
    assertTrue(survivors.contains(p2));
  }

  @Test
  public void testNextRoundCheating() {
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, new Player(new CheatingStrategy()))), whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.setup();
    List<IPlayer> survivors = manager.startNextRound();
    assertEquals(1, survivors.size());
    assertTrue(survivors.contains(p1));
  }

  @Test
  public void testNextGame() {
    List<IPlayer> players = new ArrayList<>(Arrays.asList(p1, p2));
    manager = new Manager(players, whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.setup();
    List<IPlayer> survivors = manager.startNextGame(players);
    assertEquals(2, survivors.size());
    assertTrue(survivors.contains(p1));
    assertTrue(survivors.contains(p2));
  }

  @Test
  public void testNextGameCheating() {
    List<IPlayer> players = new ArrayList<>(Arrays.asList(p1, new Player(new CheatingStrategy())));
    manager = new Manager(players, whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.setup();
    List<IPlayer> survivors = manager.startNextGame(players);
    assertEquals(1, survivors.size());
    assertTrue(survivors.contains(p1));
  }

  @Test
  public void testEndTournament() {
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, p2)), whiteDeck, new RandomCardSelector(), new RandomDestinationSelector());
    manager.start();
    assertTrue(p1.wonTournament());
    assertTrue(p2.wonTournament());

    reset();
    for (int i = 0; i < 4; i++) {
      whiteDeck.add(0, Color.red);
    }
    manager = new Manager(new ArrayList<>(Arrays.asList(p1, p2)), whiteDeck, new RegularCardSelector(), new RandomDestinationSelector());
    manager.start();
    assertTrue(p1.wonTournament());
    assertFalse(p2.wonTournament());
  }

}
