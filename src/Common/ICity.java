package Common;

/**
 * A city on the map. A city should know its name and where it is on the map.
 */
public interface ICity {

  /**
   * Get the name for the city.
   * @return the name of the city
   */
  String getName();

  /**
   * Get the coordinate for the city.
   * @return coordinate
   */
  Coord getCoord();
}
