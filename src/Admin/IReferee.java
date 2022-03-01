package Admin;

import java.util.List;
import java.util.Map;

import Player.IPlayer;

/**
 * the Referee, where all logic for verifying and executing player moves in a Trains game take place.
 */
public interface IReferee {

  /**
   * starts the game and run all phases of the game. Configure its rankings and the list of kicked
   * out players. To get the result of the game, use rankings() and kickedOut().
   */
  void start();

  /**
   * Set up phase of the game. Sets up all players with the map, initial number of rails, and
   * initial cards.
   */
  void setup();

  /**
   * Turn phase of the game. Turns proceeds until the game is ended. During each turn, A player
   * makes a move and give the Action back to the referee. The referee checks the validity of the
   * action. If the action is valid, the referee updates the game state and proceed, otherwise, the
   * referee kicks the player out of the game. If the player malfunctions, they are also kicked from
   * the game.
   */
  void playTurns();

  /**
   * Plays a single turn of the game
   */
  void playTurn();

  /**
   * End phase of the game. The referee computes a ranking for all players and notify the players
   * with the highest points that they have won the game.
   * @return a map of scores for all non-kicked players
   */
  Map<IPlayer, Scores> scoreGame();

  /**
   * returns the rankings of the players.
   * @return - the ordered list of players ranked by the points they scored
   */
  List<List<IPlayer>> rankings();

  /**
   * returns the player(s) with the highest score
   * @return the winners
   */
  List<IPlayer> getWinners();

  /**
   * returns the list of players that are kicked out of the game.
   * @return - the list of players kicked out of the game
   */
  List<IPlayer> kickedOut();

  /**
   * returns the referee game state of the referee
   * @return the rgs
   */
  IRefereeGameState getRGS();
}
