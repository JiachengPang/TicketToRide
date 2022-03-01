package Editor;

/**
 * A visualizer of the TrainMap. It should consume a TrainMap and produce a visualization of the map
 * with all cities and all connections.
 */
public interface IMapView {

  /**
   * Render the map.
   */
  void render();

  /**
   * Close the view.
   */
  void closeView();
}
