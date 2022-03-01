package Utils;

import org.json.JSONArray;

import java.awt.*;

import Common.City;
import Common.Connection;
import Common.IConnection;
import Common.TrainMap;

/**
 * Parse an JSONArray into an IConnection.
 */
public class ConnectionParser {

  private final JSONArray connO;
  private final TrainMap map;

  public ConnectionParser(JSONArray connO, TrainMap map) {
    this.connO = connO;
    this.map = map;
  }

  /**
   * Parse the JSONArray into an IConnection.
   * The JSONArray is the third JSON string in input.
   * @return IConnection
   */
  public IConnection toConnection() {
    String name1 = connO.getString(0);
    String name2 = connO.getString(1);
    return new Connection(
            new City(name1, map.cityCoord(name1)),
            new City(name2, map.cityCoord(name2)),
            this.getColor(connO.getString(2)),
            connO.getInt(3));
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
