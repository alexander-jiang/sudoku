package main.grid.view;

import javax.swing.*;

/**
 * The view (GUI) for the Sudoku grid.
 */
public class GridView extends JFrame {

  private IModelAdapter model;

  public GridView(IModelAdapter modelAdpt) {
    this.model = modelAdpt;
    initGUI();
  }

  /**
   * Initialize the GUI components.
   */
  private void initGUI() {

  }

  public void start() {
    setVisible(true);
  }
}
