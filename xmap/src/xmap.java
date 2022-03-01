import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Scanner;

import Common.TrainMap;
import Utils.MapParser;

/**
 * Testing harness that checks if 2 cities has a path in a TrainMap represented by a JSON string.
 * It builds the TrainMap from a JSONObject and call the havePath method on the given 2 cities.
  * It prints out true if the cities have a path, false otherwise.
*/
public class xmap {

  /**
   * The main method.
   * @param args arguments
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    String city1 = scan.nextLine().strip().replace("\"", "");
    String city2 = scan.nextLine().strip().replace("\"", "");

    while (scan.hasNextLine()) {
      sb.append(scan.nextLine());
      sb.append("\n");
    }
    scan.close();

    JSONTokener jt = new JSONTokener(sb.toString());
    MapParser j = new MapParser((JSONObject) jt.nextValue());
    TrainMap map = j.toMap();
    System.out.print(map.havePath(city1, city2));
  }
}