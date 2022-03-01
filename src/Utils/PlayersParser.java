package Utils;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import Player.BuyNowStrategy;
import Player.CheatingStrategy;
import Player.Hold10Strategy;
import Player.IPlayer;
import Player.Player;
import Player.Strategy;

public class PlayersParser {
  private final JSONArray ja;

  public PlayersParser(JSONArray ja) {
    this.ja = ja;
  }

  public Map<IPlayer, String> toPlayers() {
    Map<IPlayer, String> players = new HashMap<>();
    for (int i = 0; i < ja.length(); i++) {
      JSONArray player = ja.getJSONArray(i);
      players.put(new Player(this.toStrategy(player.getString(1))), player.getString(0));
    }
    return players;
  }

  private Strategy toStrategy(String str) {
    switch (str) {
      case "Hold-10": return new Hold10Strategy();
      case "Buy-Now": return new BuyNowStrategy();
      case "Cheat": return new CheatingStrategy();
      default: return null;
    }
  }
}
