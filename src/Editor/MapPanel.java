package Editor;

import Common.Coord;
import Common.TrainMap;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

/**
 * A JPanel where the map is drawn. It represents cities as orange ellipses with the names on top
 * and connections as segmented lines representing their colors and numbers of segments.
 */
public class MapPanel extends JPanel {
  private final TrainMap map;
  //private final int MARGIN = 20;
  // line thickness
  private final int STROKE = 4;
  // width of city ellipses
  private final int OVAL_WIDTH = 40;
  // height of city ellipses
  private final int OVAL_HEIGHT = 20;
  // font size of city names
  private final int FONT_SIZE = 20;

  /**
   * Constructor.
   * @param map a TrainMap
   */
  public MapPanel(TrainMap map) {
    this.map = map;
    setSize(new Dimension(map.getDimension()));
    setBackground(Color.BLACK);
  }

  @Override
  public void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    super.paintComponent(g2d);
    this.drawConnections(g2d);
    this.drawCities(g2d);
    g2d.dispose();
  }

  /**
   * Draw all cities on the map.
   * @param g Graphics2D
   */
  private void drawCities(Graphics2D g) {
    g.setFont(new Font("TimesRoman", Font.PLAIN, FONT_SIZE));
    g.setStroke(new BasicStroke(STROKE));
    FontMetrics metrics = g.getFontMetrics();
    for (String cityName : map.getCityNames()) {
      Coord pos = map.cityCoord(cityName);

      int strX = pos.x - metrics.stringWidth(cityName) / 2;
      int strY = pos.y - metrics.getHeight() / 2;

      g.setColor(Color.WHITE);
      g.drawString(cityName, strX, strY);

      g.setColor(Color.ORANGE);
      g.drawOval(pos.x - OVAL_WIDTH / 2, pos.y - OVAL_HEIGHT / 2, OVAL_WIDTH, OVAL_HEIGHT);
    }
  }

  /**
   * Draw all connections on the map.
   * @param g Graphics2D
   */
  private void drawConnections(Graphics2D g) {
    Set<String> visited = new HashSet<>();

    g.setStroke(new BasicStroke(STROKE));
    for (String cityName : map.getCityNames()) {
      Coord coord1 = map.cityCoord(cityName);
      for (String neighbor : map.getNeighbors(cityName)) {
        if (visited.contains(neighbor)) {
          continue;
        }
        Coord coord2 = map.cityCoord(neighbor);
        int num = 0;
        int shift = 10;
        int gapDiameter = 10;
        //int direction = -1;
        double ratio =
                (double)Math.abs(coord1.y - coord2.y) / (double)Math.abs(coord2.x - coord1.x);
        for(Color color : map.colorsBetween(cityName, neighbor)) {
          g.setColor(color);

          int shiftValue = num * shift;
          if (ratio >= 1) {
            g.drawLine(coord1.x + shiftValue, coord1.y,
                    coord2.x + shiftValue, coord2.y);
          } else {
            g.drawLine(coord1.x, coord1.y + shiftValue,
                    coord2.x, coord2.y + shiftValue);
          }

          int w = (coord2.x - coord1.x) / map.segmentsBetween(cityName, neighbor, color);
          int h = (coord2.y - coord1.y) / map.segmentsBetween(cityName, neighbor, color);

          g.setColor(Color.BLACK);
          for (int i = 1; i < map.segmentsBetween(cityName, neighbor, color); i++) {
            int gapX, gapY;
            if (ratio >= 1) {
              gapX = coord1.x + w * i + shiftValue - gapDiameter / 2;
              gapY = coord1.y + h * i - gapDiameter / 2;
            } else {
              gapX = coord1.x + w * i - gapDiameter / 2;
              gapY = coord1.y + h * i + shiftValue - gapDiameter / 2;
            }

            g.fillOval(gapX, gapY, gapDiameter, gapDiameter);
          }

          num++;
        }
      }

      visited.add(cityName);
    }
  }
}
