package Common;

import java.awt.*;
import java.util.Set;


/**
 * A connection between 2 different cities on the map. A connection should know its endpoint cities,
 * its color, and its length.
 */
public interface IConnection {

  /**
   * Get the endpoint cities of this connection.
   * @return a set of ICity
   */
  Set<ICity> getCities();

  /**
   * Get the color of this connection.
   * @return color
   */
  Color getColor();

  /**
   * Get the length of this connection.
   * @return length
   */
  int getLength();

  /**
   * Where is a city connected to via this connection?
   * @param name a city
   * @return the city it is connected to
   * @throws IllegalArgumentException is the given city is not incident to this connection
   */
  ICity connectedTo(String name);

  /**
   * Is this connection incident to the given city
   * @param name the given city
   * @return true if the given city is one of the endpoints of this connection, false otherwise
   */
  boolean contains(String name);
}
