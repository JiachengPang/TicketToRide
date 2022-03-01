package Player;

import java.util.Objects;

import Common.IConnection;

/**
 * A return value for a Strategy's makeMove method.
 * This method holds 2 values:
 *  - boolean requestCards: whether the player should request cards
 *  - IConnection connection: if the player should buy a connection, which connection to buy
 */
public class Action {
  public final boolean requestCards;
  public final IConnection connection;

  /**
   * Constructor for requesting more cards.
   * @param requestCards boolean
   */
  public Action(boolean requestCards) {
    if (!requestCards) {
      throw new IllegalArgumentException("Invalid action.");
    }
    this.requestCards = requestCards;
    this.connection = null;
  }

  /**
   * Constructor for acquiring a connection.
   * @param connection IConnection
   */
  public Action(IConnection connection) {
    if (connection == null) {
      throw new IllegalArgumentException("Invalid action.");
    }
    this.requestCards = false;
    this.connection = connection;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Action that = (Action) o;
    return requestCards == that.requestCards && Objects.equals(connection, that.connection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestCards, connection);
  }
}
