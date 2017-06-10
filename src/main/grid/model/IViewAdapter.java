package main.grid.model;

/**
 * The adapter that allows the model to talk to the view.
 */
public interface IViewAdapter {
  /**
   * Tells the view to update the displayed grid.
   */
  void updateDisplayGrid();
}
