package Utils;

import org.json.*;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.ICity;
import Common.IConnection;
import Common.TrainMap;
import Common.map;

/**
 * Parse well-formed JSON strings into a TrainMap.
 */
public class MapParser {

  // a well-formed JSON string
  private final JSONObject mapO;

  /**
   * Constructor.
   *
   * @param mapO a JSONObject a TrainMap
   */
  public MapParser(JSONObject mapO) {
    this.mapO = mapO;
  }

  /**
   * Parse the given JSONObject into a TrainMap.
   * @return a TrainMap
   */
  public TrainMap toMap() {
    int width = mapO.getInt("width");
    int height = mapO.getInt("height");
    JSONArray citiesA = mapO.getJSONArray("cities");
    JSONObject connectionsO = mapO.getJSONObject("connections");

    Map<String, ICity> cityMap = this.readCities(citiesA);
    Set<IConnection> connections = this.readConnections(connectionsO, cityMap);

    return new map(width, height, new HashSet<>(cityMap.values()), connections);
  }

  /**
   * Read all cities from a JSONArray of cities.
   * @param citiesA a JSONArray of cities
   * @return a map of city names to ICity instances
   */
  private Map<String, ICity> readCities(JSONArray citiesA) {
    Map<String, ICity> res = new HashMap<>();
    for (int i = 0; i < citiesA.length(); i++) {
      JSONArray city = citiesA.getJSONArray(i);
      String name = city.getString(0);
      JSONArray posn = city.getJSONArray(1);
      Coord c = new Coord(posn.getInt(0), posn.getInt(1));
      res.put(name, new City(name, c));
    }
    return res;
  }

  /**
   * Read all connections from a JSONObject.
   * @param connectionsO JSONObject
   * @param cities a map of city names to ICity instances
   * @return a set of IConnections
   */
  private Set<IConnection> readConnections(JSONObject connectionsO, Map<String, ICity> cities) {
    Set<IConnection> res = new HashSet<>();
    for (String city1Str : connectionsO.keySet()) {
      JSONObject targets = connectionsO.getJSONObject(city1Str);
      for (String city2Str : targets.keySet()) {
        JSONObject segments = targets.getJSONObject(city2Str);
        for (String colorStr : segments.keySet()) {
          int segs = segments.getInt(colorStr);
          Color color = this.getColor(colorStr);
          res.add(new Connection(cities.get(city1Str), cities.get(city2Str), color, segs));
        }
      }
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