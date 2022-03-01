package Admin;

import static org.junit.Assert.assertEquals;

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
import Player.Hold10Strategy;
import Player.BuyNowStrategy;
import Player.Strategy;
import Utils.DeckGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Referee functionality
 */
public class RefereeTest {
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
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetupTimeout() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    players.add(new SlowPoke(new Hold10Strategy()));
    Referee ref = new Referee(map, players, deck, new RandomCardSelector(), new RandomDestinationSelector());
    ref.setup();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetupBroken() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    players.add(new BrokenPlayer(new Hold10Strategy()));
    Referee ref = new Referee(map, players, deck, new RandomCardSelector(), new RandomDestinationSelector());
    ref.setup();
  }

  @Test
  public void testPlayTurns() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    Referee ref = new Referee(map, players, deck, new RegularCardSelector(), new RandomDestinationSelector());
    ref.start();
    List<List<IPlayer>> rankings = new ArrayList<>(Arrays.asList(
            new ArrayList<>(Collections.singletonList(p1)), new ArrayList<>(Collections.singletonList(p2))));
    assertEquals(rankings, ref.rankings());
  }

  @Test
  public void testAdvanceTurn() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    Referee ref = new Referee(map, players, deck, new RegularCardSelector(), new RandomDestinationSelector());
    ref.setup();
    assertEquals(0, ref.getRGS().getCurrentTurn());
    ref.playTurn();
    assertEquals(1, ref.getRGS().getCurrentTurn());
    ref.playTurn();
    assertEquals(0, ref.getRGS().getCurrentTurn());
  }

  @Test
  public void testKickedOut() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    IPlayer p2 = new SlowPoke(new BuyNowStrategy());
    players.add(p2);
    Referee ref = new Referee(map, players, deck, new RegularCardSelector(), new RandomDestinationSelector());
    ref.start();
    List<IPlayer> kickedOut = new ArrayList<>(Arrays.asList(p2));
    assertEquals(kickedOut, ref.kickedOut());
  }

  @Test
  public void testScoreSegments() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    Referee ref = new Referee(map, players, deck, new RegularCardSelector(), new RandomDestinationSelector());
    ref.setup();
    ref.playTurns();
    Map<IPlayer, Scores> scores = ref.scoreGame();
    Map<IPlayer, Set<IConnection>> acquired = ref.getRGS().getAcquiredConnections();
    int p1Score = 0;
    for (IConnection c : acquired.get(p1)) {
      p1Score += c.getLength();
    }
    assertEquals(p1Score, scores.get(p1).segScore);

    int p2Score = 0;
    for (IConnection c : acquired.get(p2)) {
      p2Score += c.getLength();
    }
    assertEquals(p2Score, scores.get(p2).segScore);
  }

  @Test
  public void testScoreDestinations() {
    List<Color> actualDeck = new DeckGenerator().generate();
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    Referee ref = new Referee(map, players, actualDeck, new RandomCardSelector(), new RandomDestinationSelector());
    ref.setup();
    ref.playTurns();
    Map<IPlayer, Scores> scores = ref.scoreGame();
    Map<IPlayer, Set<IConnection>> acquired = ref.getRGS().getAcquiredConnections();

    TrainMap p1Map = new map(800, 800, map.getCities(), acquired.get(p1));
    Set<IDestination> p1Dests = ref.getRGS().getAllPlayerInfo().get(p1).getPlayerDestinations();
    int p1DestScore = 0;
    for (IDestination dest : p1Dests) {
      List<ICity> pair = new ArrayList<>(dest.getCities());
      if (p1Map.havePath(pair.get(0).getName(), pair.get(1).getName())) {
        p1DestScore += 10;
      } else {
        p1DestScore -= 10;
      }
    }
    assertEquals(p1DestScore, scores.get(p1).destScore);

    TrainMap p2Map = new map(800, 800, map.getCities(), acquired.get(p2));
    Set<IDestination> p2Dests = ref.getRGS().getAllPlayerInfo().get(p2).getPlayerDestinations();
    int p2DestScore = 0;
    for (IDestination dest : p2Dests) {
      List<ICity> pair = new ArrayList<>(dest.getCities());
      if (p2Map.havePath(pair.get(0).getName(), pair.get(1).getName())) {
        p2DestScore += 10;
      } else {
        p2DestScore -= 10;
      }
    }
    assertEquals(p2DestScore, scores.get(p2).destScore);
  }

  @Test
  public void testScoreLongestPath() {
    List<IPlayer> players = new ArrayList<>();
    players.add(p1);
    players.add(p2);
    Referee ref = new Referee(map, players, deck, new RegularCardSelector(), new RandomDestinationSelector());
    ref.setup();
    ref.playTurns();
    Map<IPlayer, Scores> scores = ref.scoreGame();
    assertEquals(20, scores.get(p1).longestPathScore);
    assertEquals(0, scores.get(p2).longestPathScore);
  }
}