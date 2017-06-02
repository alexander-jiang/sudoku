package main.grid.controller;

import main.grid.model.GridModel;
import main.grid.model.IViewAdapter;
import main.grid.model.StandardSudokuGrid;
import main.grid.view.GridView;
import main.grid.view.IModelAdapter;

import java.awt.*;

/**
 * The MVC controller for the Sudoku grid.
 */
public class GridController {
  private GridModel model;

  private GridView view;

  /**
   * Constructs a new GridController.
   */
  public GridController() {
    model = new GridModel(new StandardSudokuGrid(), new IViewAdapter() {
      @Override
      public void updateDisplayValue(int i, int j, int newValue) {
        System.out.println("Not implemented!");
      }

      @Override
      public void updateDisplayCandidateValue(int i, int j, int candidateValue, boolean isCandidate) {
        System.out.println("Not implemented!");
      }
    });

    view = new GridView(new IModelAdapter() {
      @Override
      public void updateGridValue(int i, int j, int newValue) {
        System.out.println("Not implemented!");
      }

      @Override
      public void updateGridCandidateValue(int i, int j, int candidateValue, boolean isCandidate) {
        System.out.println("Not implemented!");
      }
    });
  }

  /**
   * Starts the view then the model.  The view needs to be started first so that it can display
   * the model status updates as it starts.
   */
  private void start() {
    view.start();
    model.start();
  }

  /**
   * Launches the application.
   * @param args  the command-line arguments
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        (new GridController()).start();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

}
