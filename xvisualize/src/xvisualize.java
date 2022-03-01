import org.json.JSONObject;
import org.json.JSONTokener;

import Utils.MapParser;
import Editor.IMapView;
import Editor.MapView;

import java.util.Date;
import java.util.Scanner;

import Common.TrainMap;

/**
 * Visualization harness that displays a visualization of the given map in a pop up window
 * It builds the MapView from a JSONObject (consumed from STDIN) that describes the map
 */
public class xvisualize {

  /**
   * The main method.
   * @param args arguments
   */
  public static void main(String[] args) {
    final int SECONDS_TO_WAIT = 10;

    Scanner scan = new Scanner(System.in);
    StringBuilder sb = new StringBuilder();
    while (scan.hasNextLine()) {
      sb.append(scan.nextLine());
      sb.append("\n");
    }
    scan.close();

    JSONTokener jt = new JSONTokener(sb.toString());
    MapParser j = new MapParser((JSONObject) jt.nextValue());
    TrainMap map = j.toMap();

    IMapView view = new MapView(map);

    Date start = new Date();
    Date end = new Date();

    view.render();

    // wait for the specified number of seconds
    while ((int)((end.getTime() - start.getTime()) / 1000) < SECONDS_TO_WAIT) {
      end = new Date();
    }

    view.closeView();
  }
}