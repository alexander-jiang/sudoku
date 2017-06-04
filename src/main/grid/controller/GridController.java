package main.grid.controller;

import main.grid.model.GridModel;
import main.grid.model.ISquareSudokuGrid;
import main.grid.model.IViewAdapter;
import main.grid.model.StandardSudokuGrid;
import main.grid.view.GridView;
import main.grid.view.IModelAdapter;

import java.awt.*;
import java.util.Set;

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
    // TODO write code to load initial grid from file on disk
    ISquareSudokuGrid grid = new StandardSudokuGrid(new int[][] {
        {0, 0, 0, 0, 0, 5, 0, 0, 7},
        {7, 0, 0, 0, 8, 9, 1, 0, 3},
        {0, 9, 2, 7, 0, 0, 0, 0, 5},
        {0, 0, 0, 2, 4, 7, 0, 0, 1},
        {0, 0, 5, 0, 0, 0, 9, 0, 0},
        {0, 2, 8, 9, 0, 0, 6, 0, 0},
        {2, 0, 9, 1, 0, 0, 3, 0, 0},
        {3, 4, 6, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    });

    model = new GridModel(grid, new IViewAdapter() {
      @Override
      public void updateDisplayGrid() {
        System.out.println(model.getGrid().gridToString());
      }

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
        model.getGrid().setValue(i, j, newValue);
        model.getViewAdapter().updateDisplayGrid();
      }

      @Override
      public void updateGridCandidateValue(int i, int j, int candidateValue, boolean isCandidate) {
        model.getGrid().setCandidate(i, j, candidateValue, isCandidate);
      }

      @Override
      public Set<Integer> getCandidateValues(int i, int j) {
        return model.getGrid().getCandidateValues(i, j);
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
    view.waitForInput();
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
