package Admin;

import Common.TrainMap;
import Player.IPlayer;
import java.util.List;

/**
 * Representation for the tournament manager for Trains.com
 * The manager is responsible for:
 *  - collecting a map from each player and select 1 map for the entire tournament
 *  - run the tournament until it finishes
 *  - inform the non-kicked players whether they won the tournament
 */
public interface IManager {

  /**
   * start the tournament, run it, and inform players of the outcome.
   */
  void start();

  /**
   * setup the tournament by asking each player to submit a map and choosing a map for the entire
   * tournament.
   * @return the map for the tournament
   */
  TrainMap setup();

  /**
   * run the tournament until it finishes
   */
  void run();

  /**
   * start the next round of the tournament
   * @return standing players after the next round
   */
  List<IPlayer> startNextRound();

  /**
   * allocate players into their games for the next round
   * each game should have the maximum number of players, if there are too few players to start
   * the last game, the manager backtracks to the previous game and move the youngest player to
   * the last game.
   * @return the players for each game for the next round
   */
  List<List<IPlayer>> allocatePlayers();

  /**
   * start the next game with the list of players
   * this method spawns a referee to run the game, the referee delivers the winning players and
   * kicked players during the game
   * @param players participants
   * @return standing players after next game
   */
  List<IPlayer> startNextGame(List<IPlayer> players);

  /**
   * end the tournament by informing players that still stand as winners and other non-kicked
   * players as losers
   */
  void endTournament();

  /**
   * get the winners of the tournament
   * @return winners
   */
  List<IPlayer> getWinners();

  /**
   * get the kicked players
   * @return kicked players
   */
  List<IPlayer> getKickedPlayers();
}
