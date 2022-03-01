package Utils;

import org.json.JSONArray;
import org.json.JSONObject;

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
import Common.Destination;
import Common.GeneralGameState;
import Common.IConnection;
import Common.IDestination;
import Common.IGeneralGameState;
import Common.IPlayerGameState;
import Common.PlayerGameState;
import Common.TrainMap;
import Player.IPlayer;
import Player.Player;
import Player.Hold10Strategy;

/**
 * Parse a JSONObject into an IPlayerGameState.
 */
public class PGSParser {
  
  private final JSONObject gameState;
  private final TrainMap map;

  public PGSParser(JSONObject gameState, TrainMap map) {
    this.gameState = gameState;
    this.map = map;
  }

  /**
   * Parse the JSONObject into an IPlayerGameState.
   * The JSONObject is the second JSON string in input.
   * @return IPlayerGameState
   */
  public IPlayerGameState toPGS() {
    JSONObject pgsO = gameState.getJSONObject("this");
    Set<IDestination> destinations = this.readDestinations(pgsO, map);
    int rails = pgsO.getInt("rails");
    Map<Color, Integer> hand = this.readCards(pgsO);
    Set<IConnection> acquired = this.readAcquired(pgsO.getJSONArray("acquired"), map);
    Map<IPlayer, Set<IConnection>> acquiredConnections = new HashMap<>();
    acquiredConnections.put(new Player(new Hold10Strategy()), acquired);

    JSONArray otherAcquired = gameState.getJSONArray("acquired");
    for (int i = 0; i < otherAcquired.length(); i++) {
      Set<IConnection> connections = this.readAcquired(otherAcquired.getJSONArray(i), map);
      acquiredConnections.put(new Player(new Hold10Strategy()), connections);
    }

    IGeneralGameState ggs = new GeneralGameState(map, acquiredConnections);
    return new PlayerGameState(ggs, hand, rails, destinations);
  }

  /**
   * Read the destinations of a player.
   * @param pgsO JSONObject
   * @param map the map
   * @return
   */
  private Set<IDestination> readDestinations(JSONObject pgsO, TrainMap map) {
    Set<IDestination> destinations = new HashSet<>();
    for (int i = 1; i < 3; i++) {
      JSONArray destination = pgsO.getJSONArray("destination" + i);
      String name1 = (String) destination.get(0);
      String name2 = (String) destination.get(1);
      destinations.add(new Destination(new City(name1, map.cityCoord(name1)),
              new City(name2, map.cityCoord(name2))));
    }
    return destinations;
  }

  /**
   * Read the hand of a player
   * @param pgsO JSONObject
   * @return map of color -> int
   */
  private Map<Color, Integer> readCards(JSONObject pgsO) {
    Map<Color, Integer> hand = new HashMap<>();
    JSONObject cards = pgsO.getJSONObject("cards");
    for (String c : cards.keySet()) {
      Color color = this.getColor(c);
      hand.put(color, cards.getInt(c));
    }

    Set<Color> validColors = new HashSet<>(
            Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.WHITE));;
    for (Color c : validColors) {
      if (!hand.containsKey(c)) {
        hand.put(c, 0);
      }
    }
    return hand;
  }

  /**
   * Read the acquired connections
   * @param arr JSONArray
   * @param map the map
   * @return set of IConnection
   */
  private Set<IConnection> readAcquired(JSONArray arr, TrainMap map) {
    Set<IConnection> res = new HashSet<>();
    for (int i = 0; i < arr.length(); i++) {
      ConnectionParser cp = new ConnectionParser(arr.getJSONArray(i), map);
      res.add(cp.toConnection());
    }
    return res;
  }

  /**
   * Get the corresponding color string into java awt color object.
   * @param color color string
   * @return Color
   * @throws IllegalArgumentException if color is not supported
   */
  private Color getColor(String color) {
    if (color.equalsIgnoreCase("red")) {
      return Color.RED;
    } else if (color.equalsIgnoreCase("blue")) {
      return Color.BLUE;
    } else if (color.equalsIgnoreCase("green")) {
      return Color.GREEN;
    } else if (color.equalsIgnoreCase("white")) {
      return Color.WHITE;
    } else {
      throw new IllegalArgumentException("Invalid color.");
    }
  }
}
