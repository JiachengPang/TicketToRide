package Admin;


/**
 * Scores for a player by different criteria
 */
public class Scores implements Comparable<Scores> {
  public final int segScore;
  public final int destScore;
  public final int longestPathScore;

  /**
   * Constructor
   * @param segScore scores earned by number of segments
   * @param destScore scores earned by destination completion
   * @param longestPathScore scores earned by having the longest path
   */
  public Scores(int segScore, int destScore, int longestPathScore) {
    this.segScore = segScore;
    this.destScore = destScore;
    this.longestPathScore = longestPathScore;
  }

  /**
   * the total score
   * @return the total score
   */
  public int total() {
    return segScore + destScore + longestPathScore;
  }

  @Override
  public int compareTo(Scores o) {
    return this.total() - o.total();
  }
}
