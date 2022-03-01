import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Scanner;
import java.util.Set;

import Common.IConnection;
import Common.IPlayerGameState;
import Common.TrainMap;
import Utils.ConnectionParser;
import Utils.MapParser;
import Utils.PGSParser;

/**
 * Takes in a series of well-formed JSON Strings representing in order:
 *  - the map
 *  - the player game state
 *  - the connection the player wishes to acquire
 * Outputs whether the action is legal.
 */
public class xlegal {

  /**
   * The main method.
   * @param args arguments
   */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    while (scan.hasNextLine()) {
      sb.append(scan.nextLine());
      sb.append("\n");
    }
    scan.close();

    JSONTokener jt = new JSONTokener(sb.toString());
    TrainMap map = new MapParser((JSONObject) jt.nextValue()).toMap();
    IPlayerGameState pgs = new PGSParser((JSONObject) jt.nextValue(), map).toPGS();
    IConnection c = new ConnectionParser((JSONArray) jt.nextValue(), map).toConnection();
    Set<IConnection> available = pgs.availableConnections();
    System.out.print(available.contains(c));
  }
}
