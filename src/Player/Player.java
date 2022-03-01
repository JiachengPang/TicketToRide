package Player;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.ICity;
import Common.IConnection;
import Common.map;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Common.GeneralGameState;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.IPlayerGameState;
import Common.PlayerGameState;
import Common.TrainMap;

/**
 * A player implementation that keeps track of:
 *  - the map
 *  - the state of the game, including its assets: cards, rails, destinations
 *  - the strategy
 *  - whether it has won the game
 */
public class Player implements IPlayer {
  private Strategy strategy;
  private IPlayerGameState pgs;
  private boolean wonGame;
  private boolean wonTournament;

  public Player(String strategyPath) {
    try {
      ClassLoader cl = ClassLoader.getSystemClassLoader();
      Class<?> strategyClass = cl.loadClass(strategyPath);
      this.strategy = (Strategy) strategyClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to load strategy: " + e.getMessage());
    }
  }

  public Player(Strategy strategy) {
    this.strategy = strategy;
  }

  @Override
  public void setup(TrainMap map, int rails, Map<Color, Integer> cards) {
    if (map == null || cards == null) {
      throw new IllegalArgumentException("Inputs cannot be null.");
    }
    IGeneralGameState ggs = new GeneralGameState(map, new HashMap<>());
    this.pgs = new PlayerGameState(ggs, cards, rails, new HashSet<>());
    this.wonGame = false;
  }

  @Override
  public Set<IDestination> pick(Set<IDestination> destinations) {
    Set<IDestination> options = new HashSet<>(destinations);
    Set<IDestination> picked = this.strategy.pickDestinations(options);
    options.removeAll(picked);
    this.pgs = new PlayerGameState(pgs.getGGS(), pgs.getHand(), pgs.getRails(), picked);
    return options;
  }

  @Override
  public Action play(IPlayerGameState pgs) {
    return this.strategy.makeMove(pgs);
  }

  @Override
  public void receiveCards(Map<Color, Integer> cards) {
    Map<Color, Integer> hand = this.pgs.getHand();
    for (Color c : cards.keySet()) {
      hand.put(c, hand.get(c) + cards.get(c));
    }
    this.pgs = new PlayerGameState(this.pgs.getGGS(),
            hand, this.pgs.getRails(), this.pgs.getDestinations());
  }

  @Override
  public void win(boolean w) {
    this.wonGame = w;
  }

  @Override
  public TrainMap start() {
    ICity atlanta = new City("Atlanta", new Coord(50, 100));
    ICity boston = new City("Boston", new Coord(50, 100));
    ICity chicago = new City("Chicago", new Coord(60, 10));
    ICity denver = new City("Denver", new Coord(50, 400));
    ICity sanfran = new City("San Francisco", new Coord(1, 2));
    ICity seattle = new City("Seattle", new Coord(1, 1));
    ICity miami = new City("Miami", new Coord(8, 8));
    Set<ICity> citySet = new HashSet<>(Arrays.asList(
        atlanta, boston, chicago, denver, sanfran, seattle, miami));

    IConnection ATLtoBOS = new Connection(atlanta, boston, Color.RED, 4);
    IConnection BOStoCHI = new Connection(boston, chicago, Color.BLUE, 3);
    IConnection BOStoDEN = new Connection(boston, denver, Color.GREEN, 3);
    IConnection CHItoDEN = new Connection(chicago, denver, Color.BLUE, 5);
    IConnection CHItoDEN2 = new Connection(chicago, denver, Color.GREEN, 3);
    IConnection SEAtoSF = new Connection(seattle, sanfran, Color.RED, 5);
    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
        ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));
    return new map(500, 500, citySet, connectionSet);
  }

  @Override
  public void end(boolean w) {
    this.wonTournament = w;
  }

  @Override
  public boolean wonGame() {
    return this.wonGame;
  }

  @Override
  public boolean wonTournament() {
    return this.wonTournament;
  }
}
