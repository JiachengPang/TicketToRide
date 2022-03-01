package Common;

import java.awt.*;
import java.util.Set;

/**
 * Represent a Map of train routes between cities.
 * A Map should know:
 *  - its size
 *  - all cities on the map, their names, and their positions
 *  - all connections between cities, the endpoints, color, and length of each connection
 */
public interface TrainMap {

  /**
   * Get the dimension of the map.
   * @return a 2D dimension
   */
  Dimension getDimension();

  /**
   * Get the names of all cities.
   * @return a set of string representing city names (no duplicates)
   */
  Set<String> getCityNames();

  /**
   * Gets all the cities on the map
   * @return the cities
   */
  Set<ICity> getCities();

  /**
   * Get the coordinate of a city.
   * @param city the city name
   * @return the coordinate of the city
   */
  Coord cityCoord(String city);

  /**
   * Get the number of segments between 2 cities on the connection with the give color.
   * @param city1 a city name
   * @param city2 a city name
   * @param color the color
   * @return number of segments, -1 if these cities are not connected with a rail of the given color
   */
  int segmentsBetween(String city1, String city2, Color color);

  /**
   * Get the set of colors of all connections between 2 cities.
   * @param city1 a city name
   * @param city2 a city name
   * @return the set of colors
   */
  Set<Color> colorsBetween(String city1, String city2);

  /**
   * Get all feasible destinations. A destination is a pair of cities that has a path between them.
   * @return a set of IDestination
   */
  Set<IDestination> getDestinations();

  /**
   * Get all connections on the map.
   * @return a set of IConnection
   */
  Set<IConnection> getAllConnections();

  /**
   * Get all neighbors of a city.
   * @param city a city name
   * @return a set of city names the given city is adjacent to
   */
  Set<String> getNeighbors(String city);

  /**
   * Is there a path between city1 and city2.
   * @param city1 a city name
   * @param city2 a city name
   * @return true if there exists a path between city1 and city2
   */
  boolean havePath(String city1, String city2);
}
