package Common;

import java.util.Objects;

/**
 * A city on the map. A city has a name and a coordinate
 * that represents where the city is on the map.
 */
public class City implements ICity {
  private final String name;
  private final Coord coord;

  /**
   * Constructor.
   * @param name name of the city
   * @param coord coordinates of the city
   */
  public City(String name, Coord coord) {
    this.name = name;
    this.coord = coord;
  }

  @Override
  public Coord getCoord() {
    return coord;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    City city = (City) o;
    return Objects.equals(this.name, city.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }

  @Override
  public String toString() {
    return "City{" +
            "name='" + name + '\'' +
            ", coord=" + coord +
            '}';
  }
}
