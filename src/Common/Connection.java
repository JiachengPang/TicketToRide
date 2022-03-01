package Common;

import java.awt.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A connection between 2 different cities on the map. A connection has the following attributes:
 *  - A set of cities of length 2: the endpoints of the connection
 *  - A color: can be red, blue, green, or white
 *  - A length: can be 3, 4, or 5
 */
public class Connection implements IConnection {
  private final Set<ICity> cities;
  private final Color color;
  private final int length;

  /**
   * Constructor
   * @param city1 a city
   * @param city2 a city
   * @param color a color(red, blue, green, or white)
   * @param length a length(3, 4, or 5)
   * @throws IllegalArgumentException if input is not valid
   */
  public Connection(ICity city1, ICity city2, Color color, int length) {
    if (city1.equals(city2)) {
      throw new IllegalArgumentException("A connection has to connect 2 different cities.");
    }

    if (length < 3 | length > 5) {
      throw new IllegalArgumentException("A connection can only have 3, 4, or 5 segments.");
    }

    if (!this.validColor(color)) {
      throw new IllegalArgumentException("Color must be red, blue, green, or white.");
    }
    this.cities = new HashSet<>();
    this.cities.add(city1);
    this.cities.add(city2);
    this.color = color;
    this.length = length;
  }

  /**
   * Check if color is valid: red, blue, green, or white.
   * @param color color
   * @return true if color is valid
   */
  private boolean validColor(Color color) {
    return color.equals(Color.RED) | color.equals(Color.GREEN)
            | color.equals(Color.BLUE) | color.equals(Color.WHITE);
  }

  @Override
  public Set<ICity> getCities() {
    return new HashSet<>(this.cities);
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public int getLength() {
    return length;
  }

  @Override
  public ICity connectedTo(String name) {
    if (!this.contains(name)) {
      throw new IllegalArgumentException("City is not incident to this connection.");
    }
    for (ICity c : this.cities) {
      if (!c.getName().equals(name)) {
        return c;
      }
    }
    return null;
  }

  @Override
  public boolean contains(String name) {
    for (ICity c : this.cities) {
      if (c.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Connection that = (Connection) o;
    return length == that.length
            && Objects.equals(cities, that.cities)
            && Objects.equals(color, that.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cities, color, length);
  }

  @Override
  public String toString() {
    return "Connection{" +
            "cities=" + cities +
            ", color=" + color +
            ", length=" + length +
            '}';
  }
}
