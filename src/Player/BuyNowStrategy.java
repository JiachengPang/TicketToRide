package Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Common.IConnection;
import Common.IDestination;
import Common.IPlayerGameState;

/**
 * Represents a Strategy for a player:
 *  - pick the last 2 destinations in lexicographic order
 *  - each turn, buy a connection if possible, otherwise, request cards.
 *  - buy the first connection in lexicographic order
 */
public class BuyNowStrategy extends AbstractStrategy {

  @Override
  public Set<IDestination> pickDestinations(Set<IDestination> destinations) {
    List<IDestination> sortedDestinations = getSortedDestinations(destinations);
    return new HashSet<>(sortedDestinations.subList(destinations.size() - 2, destinations.size()));
  }

  @Override
  public Action makeMove(IPlayerGameState pgs) {
    List<IConnection> connections = getSortedConnections(pgs.availableConnections());
    if (connections.size() == 0) {
      return new Action(true);
    }
    return new Action(connections.get(0));
  }
}
