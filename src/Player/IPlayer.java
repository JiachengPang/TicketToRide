package Player;

import java.awt.*;
import java.util.Map;
import java.util.List;
import java.util.Set;

import Common.IConnection;
import Common.IDestination;
import Common.IPlayerGameState;
import Common.TrainMap;

/**
 * Represents a player in the game. The player should know the game state and make decisions
 * based on its strategy to the game.
 */
public interface IPlayer {

  /**
   * setup the game.
   * @param map - the map
   * @param rails - the rails of the player
   * @param cards - the cards for the player
   */
  void setup(TrainMap map, int rails, Map<Color, Integer> cards);

  /**
   * picks destinations.
   * @param destinations - the choices
   * @return The remaining set of IDestination
   */
  Set<IDestination> pick(Set<IDestination> destinations);

  /**
   * play the turn and return an Action Object that represents the player's choice.
   * @param pgs - the game state
   * @return the Action
   */
  Action play(IPlayerGameState pgs);

  /**
   * Receive cards from the referee. Update the player game state.
   * @param cards cards from the referee
   */
  void receiveCards(Map<Color, Integer> cards);

  /**
   * Receive a notification from the referee whether this player has won the game.
   * @param w win or not
   */
  void win(boolean w);

  /**
   * Submit a map to the tournament manager
   * @return TrainMap
   */
  TrainMap start();

  /**
   * Inform this player whether they have won the tournament
   * @param w result
   */
  void end(boolean w);

  /**
   * Has this player won the last game
   * @return true if the player has won the last game, false otherwise
   */
  boolean wonGame();

  /**
   * Has this player won the tournament
   * @return true if the player has won the tournament, false otherwise
   */
  boolean wonTournament();
}
