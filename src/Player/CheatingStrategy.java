package Player;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Common.Connection;
import Common.ICity;
import Common.IConnection;
import Common.IDestination;
import Common.IPlayerGameState;
import Common.TrainMap;
import Player.Action;
import Player.Strategy;

public class CheatingStrategy extends AbstractStrategy {

  @Override
  public Set<IDestination> pickDestinations(Set<IDestination> destinations) {
    List<IDestination> sortedDestinations = getSortedDestinations(destinations);
    return new HashSet<>(sortedDestinations.subList(destinations.size() - 2, destinations.size()));
  }

  @Override
  public Action makeMove(IPlayerGameState pgs) {
    TrainMap map = pgs.getMap();
    for (ICity city1 : map.getCities()) {
      for (ICity city2 : map.getCities()) {
        if (city1.equals(city2)) {
          continue;
        }
        if (map.colorsBetween(city1.getName(), city2.getName()).isEmpty()) {
          IConnection c = new Connection(city1, city2, Color.RED, 5);
          return new Action(c);
        }
      }
    }
    return null;
  }
}
