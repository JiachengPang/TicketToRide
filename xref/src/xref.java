import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import Admin.IReferee;
import Admin.RandomCardSelector;
import Admin.RandomDestinationSelector;
import Admin.Referee;
import Common.TrainMap;
import Player.IPlayer;
import Utils.MapParser;
import Utils.PlayersParser;

public class xref {

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
    Map<IPlayer, String> players = new PlayersParser((JSONArray) jt.nextValue()).toPlayers();
    System.out.println(players);
    List<Color> deck = new ArrayList<>();
    JSONArray colors = (JSONArray) jt.nextValue();
    for (int i = 0; i < colors.length(); i++) {
      deck.add(getColor(colors.getString(i)));
    }

    IReferee ref = null;
    try {
      ref = new Referee(map, new ArrayList<>(players.keySet()), deck, new RandomCardSelector(), new RandomDestinationSelector());
    } catch (IllegalArgumentException e) {
      if (e.getMessage().equals("The map does not have enough potential destinations to "
              + "start a game.")) {
        System.out.print("error: not enough destinations");
        return;
      }
    }

    ref.start();
    List<List<IPlayer>> rankings = ref.rankings();
    List<IPlayer> kicked = ref.kickedOut();
    JSONArray res = new JSONArray();
    JSONArray ranks = new JSONArray();
    for (List<IPlayer> place : rankings) {
      List<String> tiedPlayers = new ArrayList<>();
      for (IPlayer player : place) {
        tiedPlayers.add(players.get(player));
      }
      tiedPlayers.sort(String::compareTo);
      ranks.put(new JSONArray(tiedPlayers));
    }
    res.put(ranks);
    List<String> kickedOut = new ArrayList<>();
    for (IPlayer player : kicked) {
      kickedOut.add(players.get(player));
    }
    kickedOut.sort(String::compareTo);
    res.put(kickedOut);
    System.out.print(res.toString());
    System.exit(1);
  }

  /**
   * Get the corresponding color string into java awt color object.
   * @param color color string
   * @return Color
   * @throws IllegalArgumentException if color is not supported
   */
  private static Color getColor(String color) {
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
