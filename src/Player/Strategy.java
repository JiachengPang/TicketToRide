package Player;

import java.util.List;
import java.util.Set;

import Common.IDestination;
import Common.IPlayerGameState;

/**
 * Represents a strategy for a player to play the game. The strategy makes the following decisions:
 *  - which destination cards to get
 *  - what to do in a turn
 */
public interface Strategy {

  /**
   * Pick 2 destinations from the given destinations
   * @param destinations a list of IDestinations
   * @return a set of chosen IDestinations
   */
  Set<IDestination> pickDestinations(Set<IDestination> destinations);

  /**
   * Choose a move, request more cards, or buy a connection.
   * @param pgs IPlayerGameState
   * @return StrategyReturn object that indicates what move to make
   */
  Action makeMove(IPlayerGameState pgs);
}
