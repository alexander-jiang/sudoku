package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.util.DisplayStrings;
import main.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * A constraint-based solver that attempts to narrow down the candidate values
 * for each empty square based on the constraints of the Sudoku grid. The solver
 * uses logical inferences to determine when candidate values would violate constraints.
 */
public class ConstraintBasedSolver implements ISquareSudokuSolver {

  /**
   * Set up the initial candidate values (as the grid may not have them set properly).
   */
  private void initializeCandidateValues() {
    // TODO need this step to set things up
  }

  @Override
  public ISquareSudokuGrid solve(ISquareSudokuGrid grid) {
    initializeCandidateValues();

    // Check for elements with only one candidate value (naked single). These elements should have
    // their value set to the single candidate value.
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        if (grid.getCandidateValues(r, c).size() == 1) {
          int nakedSingle = (Integer) (grid.getCandidateValues(r, c).toArray()[0]);
          System.out.println("Found naked single in element (" + r + ", " + c + "): " + nakedSingle);
          grid.setValue(r, c, nakedSingle);
        }
      }
    }

    // Check for elements which are the only element in the group (row, column, or box)
    // that contain a certain value as a candidate (hidden single).
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        // Check against the row.
        // TODO use enum to distinguish between "failed, no constraint violated", "failed, constraint violated" and "success"
        checkForHiddenSingle(grid, r, c, grid.getRowElements(r, c));

        // Check against the column.
        checkForHiddenSingle(grid, r, c, grid.getColumnElements(r, c));

        // Check against the box.
        checkForHiddenSingle(grid, r, c, grid.getBoxElements(r, c));
      }
    }

    // Check for a set of m elements in a group that are the only m elements in the group
    // that contain a set of m values as candidates. In those elements, any value that
    // isn't one of the m shared values is not a candidate (as then there would be at most m-1
    // elements that remain to store the m shared values).

    // TODO is the idea to make the Sudoku grid functional by having the solver return a grid??
    // TODO And then this solve method could be recursive?
    return grid;
  }

  private boolean checkForHiddenSingle(ISquareSudokuGrid grid, int r, int c, List<Pair<Integer, Integer>> groupCoordinates) {
    Set<Integer> candidates = grid.getCandidateValues(r, c);
    for (Pair<Integer, Integer> coords : groupCoordinates) {
      // Don't compare with self.
      if (!(coords.first() == r && coords.second() == c) && !candidates.isEmpty()) {
        candidates.removeAll(grid.getCandidateValues(coords.first(), coords.second()));
      }
    }
    if (candidates.size() > 1) {
      System.out.println("Constraint violated! These values appear only once in element (" + r + ", " + c + "): " +
          DisplayStrings.setToString(candidates));
      return false;
    } else if (candidates.size() == 1) {
      int hiddenSingle = (Integer) (grid.getCandidateValues(r, c).toArray()[0]);
      System.out.println("Found hidden single in element (" + r + ", " + c + "): " + hiddenSingle);
      grid.setValue(r, c, hiddenSingle);
      return true;
    } else {
      return false;
    }
  }

  public static void main(String[] args) {
    ISquareSudokuGrid partiallyFilledGrid = new StandardSudokuGrid(new int[][] {
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
    ISquareSudokuSolver solver = new ConstraintBasedSolver();
    solver.solve(partiallyFilledGrid);
  }
}
