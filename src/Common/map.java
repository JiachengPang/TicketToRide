package Common;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * A map of the game. The map has:
 *  - a dimension: the size of the board
 *  - a map of string -> ICity: all cities on the map
 *  - a map of string -> set of IConnection: all connections that a city is incident to.
 */
public class map implements TrainMap {
  private final Dimension d;
  private final Map<String, ICity> cities;
  private final Set<IConnection> connectionSet;
  private final Map<String, Map<String, Set<IConnection>>> connections;

  /**
   * Constructor.
   * @param w width of the board
   * @param h height of the board
   * @param cities a set of all cities
   * @param connections a set of all connections
   * @throws IllegalArgumentException if width or height is not positive
   *                                  or not all cities are on the board
   */
  public map(int w, int h, Set<ICity> cities, Set<IConnection> connections) {
    if (w <= 0 | h <= 0) {
      throw new IllegalArgumentException("Height and width must be positive.");
    }
    if (!this.allOnBoard(w, h, cities)) {
      throw new IllegalArgumentException("Not all cities are within the map.");
    }
    this.d = new Dimension(w, h);
    this.cities = new HashMap<>();
    for (ICity c : cities) {
      this.cities.put(c.getName(), c);
    }
    this.connections = new HashMap<>();
    for (ICity c : cities) {
      this.connections.put(c.getName(), new HashMap<>());
      for (ICity other : cities) {
        if (!c.equals(other)) {
          this.connections.get(c.getName()).put(other.getName(), new HashSet<>());
        }
      }
    }
    for (IConnection c: connections) {
      this.addConnection(c);
    }
    this.connectionSet = connections;
  }

  /**
   * Constructor where the size of the board is 500 x 500
   * @param cities a set of all cities
   * @param connections a set of all connections
   */
  public map(Set<ICity> cities, Set<IConnection> connections) {
    this(inferDimension(cities).width, inferDimension(cities).height, cities, connections);
  }

  /**
   * Infer the size of the board with all cities. The board should be big enough to contain all
   * cities. By default, the minimum dimension is 100 x 100 pixels
   * @param cities all cities
   * @return a dimension
   */
  private static Dimension inferDimension(Set<ICity> cities) {
    Dimension res = new Dimension(100, 100);
    for (ICity c : cities) {
      Coord coord = c.getCoord();
      if (coord.x > res.width) {
        res.width = coord.x;
      }
      if (coord.y > res.height) {
        res.height = coord.y;
      }
    }
    return res;
  }

  /**
   * Check if all cities are within the dimension of the map.
   * @param width width of the map
   * @param height height of the map
   * @param cities all cities
   * @return true if all cities are within the map, false otherwise
   */
  private boolean allOnBoard(int width, int height, Collection<ICity> cities) {
    for (ICity c : cities) {
      if (c.getCoord().x > width | c.getCoord().y > height) {
        return false;
      }
    }
    return true;
  }

  /**
   * Called by a constructor to transform add an IConnection to
   * the map of string -> set of IConnection, for easier access to the neighbors of a city.
   * An IConnection is stored both ways.
   * e.g. A is connected to B, then both A and B are mapped to this connection.
   * @param connection IConnection
   */
  private void addConnection(IConnection connection) {
    for (ICity city : connection.getCities()) {
      if (!this.cities.containsKey(city.getName())) {
        throw new IllegalArgumentException("City " + city.getName() + " does not exist.");
      }

      for (IConnection conn : this.connections.get(city.getName())
              .get(connection.connectedTo(city.getName()).getName())) {
        if (connection.getColor().equals(conn.getColor())) {
          throw new IllegalArgumentException(
              "2 cities cannot be connected by more than 1 rails of the same color.");
        }
      }
    }

    for (ICity city : connection.getCities()) {
      this.connections.get(city.getName()).get(connection.connectedTo(city.getName())
              .getName()).add(connection);
    }
  }

  @Override
  public Dimension getDimension() {
    return new Dimension(d.width, d.height);
  }

  @Override
  public Set<String> getCityNames() {
    Set<String> res = new HashSet<>();
    for (ICity c : this.cities.values()) {
      res.add(c.getName());
    }
    return res;
  }

  @Override
  public Set<ICity> getCities() {
    return new HashSet<>(cities.values());
  }

  @Override
  public Coord cityCoord(String city) {
    if (this.cities.containsKey(city)) {
      return this.cities.get(city).getCoord();
    }
    throw new IllegalArgumentException("City does not exist.");
  }

  @Override
  public int segmentsBetween(String city1, String city2, Color color) {
    for (IConnection conn : this.connections.get(city1).get(city2)) {
      if (conn.getColor().equals(color)) {
        return conn.getLength();
      }
    }
    return -1;
  }

  @Override
  public Set<Color> colorsBetween(String city1, String city2) {
    Set<Color> res = new HashSet<>();

    for (IConnection conn : this.connections.get(city1).get(city2)) {
      res.add(conn.getColor());
    }

    return res;
  }

  @Override
  public Set<IDestination> getDestinations() {
    Set<IDestination> res = new HashSet<>();
    for (ICity city1 : this.cities.values()) {
      Set<ICity> reacheable = this.getReachableCities(city1);
      for (ICity city2 : reacheable) {
        res.add(new Destination(city1, city2));
      }
    }
    return res;
  }

  @Override
  public Set<IConnection> getAllConnections() {
    return new HashSet<>(this.connectionSet);
  }

  @Override
  public Set<String> getNeighbors(String city) {
    Set<String> res = new HashSet<>();
    for (String key : this.connections.get(city).keySet()) {
      if (!this.connections.get(city).get(key).isEmpty()) {
        res.add(key);
      }
    }
    return res;
  }

  @Override
  public boolean havePath(String city1, String city2) {
    return this.getReachableCities(this.cities.get(city1)).contains(this.cities.get(city2));
  }

  /**
   * Get all cities the given city has a path to.
   * @param from a city
   * @return a set of cities this city can reach
   */
  private Set<ICity> getReachableCities(ICity from) {
    Set<ICity> res = new HashSet<>();
    Set<ICity> visited = new HashSet<>();
    visited.add(from);
    Stack<ICity> next = new Stack<>();
    for (String s : this.connections.get(from.getName()).keySet()) {
      for (IConnection conn : this.connections.get(from.getName()).get(s)) {
        next.push(conn.connectedTo(from.getName()));
      }
    }

    while (!next.empty()) {
      ICity current = next.pop();
      if (visited.contains(current)) {
        continue;
      }

      res.add(current);
      visited.add(current);

      for (String s : this.connections.get(current.getName()).keySet()) {
        for (IConnection conn : this.connections.get(current.getName()).get(s)) {
          if (!visited.contains(conn.connectedTo(current.getName()))) {
            next.push(conn.connectedTo(current.getName()));
          }
        }
      }
    }
    return res;
  }
}
