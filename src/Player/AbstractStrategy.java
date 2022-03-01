package Player;

import Common.IConnection;
import Common.IDestination;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An abstract strategy that implements lexicographic ordering of destinations and connections.
 */
public abstract class AbstractStrategy implements Strategy {

  /**
   * Order the destinations
   * @param destinations destinations
   * @return ordered destinations
   */
  public List<IDestination> getSortedDestinations(Set<IDestination> destinations) {
    if (destinations.size() < 2) {
      throw new IllegalArgumentException("Not enough destinations to pick from.");
    }
    List<IDestination> destinationsList = new ArrayList<>(destinations);
    destinationsList.sort(new DestinationComparator());
    return destinationsList;
  }

  /**
   * Order the connections
   * @param connections connections
   * @return ordered connections
   */
  public List<IConnection> getSortedConnections(Set<IConnection> connections) {
    List<IConnection> connectionsList = new ArrayList<>(connections);
    connectionsList.sort(new ConnectionComparator());
    return connectionsList;
  }
}
