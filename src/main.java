import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import Common.City;
import Common.Connection;
import Common.Coord;
import Common.ICity;
import Common.IConnection;
import Common.TrainMap;
import Common.map;
import Editor.IMapView;
import Editor.MapView;

/**
 * A main class for manually testing the map visualization.
 */
public class main {

  /**
   * Main method for testing.
   * @param args arguments
   */
  public static void main(String[] args) {
    ICity atlanta = new City("Atlanta", new Coord(400, 100));
    ICity boston = new City("Boston", new Coord(200, 100));
    ICity chicago = new City("Chicago", new Coord(100, 40));
    ICity denver = new City("Denver", new Coord(50, 400));
    ICity sanfran = new City("San Francisco", new Coord(600, 600));
    ICity seattle = new City("Seattle", new Coord(550, 40));
    ICity miami = new City("Miami", new Coord(250, 300));

    IConnection ATLtoBOS = new Connection(atlanta, boston, Color.RED, 5);
    IConnection BOStoCHI = new Connection(boston, chicago, Color.BLUE, 3);
    IConnection BOStoDEN = new Connection(boston, denver, Color.GREEN, 4);
    IConnection CHItoDEN = new Connection(chicago, denver, Color.WHITE, 5);
    IConnection CHItoDEN2 = new Connection(chicago, denver, Color.GREEN, 3);
    IConnection SEAtoSF = new Connection(seattle, sanfran, Color.RED, 5);


    Set<ICity> citySet = new HashSet<>(Arrays.asList(
            atlanta, boston, chicago, denver, sanfran, seattle, miami));

    Set<IConnection> connectionSet = new HashSet<>(Arrays.asList(
            ATLtoBOS, BOStoCHI, BOStoDEN, CHItoDEN, CHItoDEN2, SEAtoSF));

    TrainMap map = new map(800, 800, citySet, connectionSet);
    IMapView view = new MapView(map);
    view.render();
  }
}
