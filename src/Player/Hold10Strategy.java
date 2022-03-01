package Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.ICity;
import Common.IConnection;
import Common.IDestination;
import Common.IPlayerGameState;


/**
 * Represents a Strategy for a player:
 *  - pick the first 2 destinations in lexicographic order
 *  - each turn, if the player is holding less than or equal to 10 cards, request cards.
 *    Otherwise, buy connection.
 *  - buy the first connection in lexicographic order
 */
public class Hold10Strategy extends AbstractStrategy {

  @Override
  public Set<IDestination> pickDestinations(Set<IDestination> destinations) {
    List<IDestination> sortedDestinations = getSortedDestinations(destinations);
    return new HashSet<>(sortedDestinations.subList(0, 2));
  }

  @Override
  public Action makeMove(IPlayerGameState pgs) {
    Map<Color, Integer> hand = pgs.getHand();
    int numCards = hand.values().stream().mapToInt(Integer::intValue).sum();
    if (numCards <= 10) {
      return new Action(true);
    }

    List<IConnection> connections = getSortedConnections(pgs.availableConnections());
    if (connections.size() == 0) {
      return new Action(true);
    }
    return new Action(connections.get(0));
  }
}
