package Editor;

import Common.TrainMap;
import javax.swing.JFrame;

/**
 * A visualization of the map using Java Swing. It consumes a map and paints the map on a JPanel.
 * This class extends JFrame to hold the JPanel.
 */
public class MapView extends JFrame implements IMapView  {
  private final MapPanel mp;

  /**
   * Constructor.
   * @param map a TrainMap
   */
  public MapView(TrainMap map) {
    if (map == null) {
      throw new IllegalArgumentException("Map is null.");
    }
    // panel for the map
    this.mp = new MapPanel(map);
    this.setTitle("Trains.com");
    this.add(mp);
    this.setSize(map.getDimension());
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  @Override
  public void render() {
    this.repaint();
    this.setVisible(true);
  }

  @Override
  public void closeView() {
    this.setVisible(false);
    this.dispose();
  }
}
