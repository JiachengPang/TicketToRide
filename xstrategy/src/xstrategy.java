import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Common.ICity;
import Common.IConnection;
import Common.IPlayerGameState;
import Common.TrainMap;
import Player.Action;
import Player.Hold10Strategy;
import Player.ICityComparator;
import Player.Strategy;
import Utils.MapParser;
import Utils.PGSParser;

/**
 * Test for Hold10Strategy. Inputs should be a series of well-formed JSON strings representing
 * the map of the game and the player game state. The output is an action that the current
 * player should take according to the Hold10Strategy
 *
 * An action is one of:
 *  - "more cards"
 *  - [Name, Name, Color, Length]
 */
public class xstrategy {

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
    Strategy hold10 = new Hold10Strategy();
    Action a = hold10.makeMove(pgs);
    if (a.requestCards) {
      System.out.print("more cards");
    } else {
      IConnection connection = a.connection;
      JSONArray arr = new JSONArray();
      List<ICity> cities = new ArrayList<>(connection.getCities());
      cities.sort(new ICityComparator());
      arr.put(cities.get(0).getName());
      arr.put(cities.get(1).getName());
      Color c = connection.getColor();
      String colorStr = "";
      if (c.equals(Color.RED)) {
        colorStr += "red";
      } else if (c.equals(Color.BLUE)) {
        colorStr += "blue";
      } else if (c.equals(Color.WHITE)) {
        colorStr += "white";
      } else if (c.equals(Color.GREEN)) {
        colorStr += "green";
      }
      arr.put(colorStr);
      arr.put(connection.getLength());
      System.out.print(arr.toString());
    }
  }
}
