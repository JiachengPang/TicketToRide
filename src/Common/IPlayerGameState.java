package Common;

import java.awt.*;
import java.util.Map;
import java.util.Set;

/**
 * Represents information about the state of a game that is visible to a particular player.
 * Contents include all from IGeneralGameState plus:
 *  - the colored cards in the player's hand, hidden from other players
 *  - number of rails left
 *  - destinations, hidden from other players
 */
public interface IPlayerGameState extends IGeneralGameState {

    /**
     * The holding of colored cards, which is a map of color -> int representing # cards for each
     * color
     * @return a map of color -> int
     * is holding
     */
    Map<Color, Integer> getHand();

    /**
     * The number of rails left.
     * @return # rails
     */
    int getRails();

    /**
     * Destinations for a player.
     * @return set of IDestination
     */
    Set<IDestination> getDestinations();

    /**
     * Connections available to acquire.
     * @return set of available connections
     */
    Set<IConnection> availableConnections();

    /**
     * Get the IGeneralGameState.
     * @return IGeneralGameState
     */
    IGeneralGameState getGGS();
}
