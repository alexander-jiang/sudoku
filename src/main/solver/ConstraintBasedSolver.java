package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.util.DisplayStrings;
import main.util.Pair;

import java.util.*;

/**
 * A constraint-based solver that attempts to narrow down the candidate values
 * for each empty square based on the constraints of the Sudoku grid. The solver
 * uses logical inferences to determine when candidate values would violate constraints.
 */
public class ConstraintBasedSolver implements ISquareSudokuSolver {

  /**
   * Set up the initial candidate values (as the grid may not have them set properly).
   */
  public static ISquareSudokuGrid initializeCandidateValues(ISquareSudokuGrid grid) {
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        for (int value = 1; value <= grid.getDimension(); value++) {
          grid.setCandidate(r, c, value, true);
        }
      }
    }

    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        int gridValue = grid.getValue(r, c);
        if (gridValue != 0) {
          for (Pair<Integer, Integer> sameRowCoord : grid.getRowElements(r, c)) {
            grid.setCandidate(sameRowCoord.first(), sameRowCoord.second(), gridValue, false);
          }

          for (Pair<Integer, Integer> sameColCoord : grid.getColumnElements(r, c)) {
            grid.setCandidate(sameColCoord.first(), sameColCoord.second(), gridValue, false);
          }

          for (Pair<Integer, Integer> sameBoxCoord : grid.getBoxElements(r, c)) {
            grid.setCandidate(sameBoxCoord.first(), sameBoxCoord.second(), gridValue, false);
          }
        }
      }
    }
    return grid;
  }

  /**
   * Assigns the value to the given element in the grid, and returns the resulting grid.
   * @param i the row coordinate of the element to update
   * @param j the column coordinate of the element to update
   * @param value the value to update the element to
   * @return  the updated grid
   */
  private ISquareSudokuGrid constrain(ISquareSudokuGrid grid, int i, int j, int value) {
    if (grid.isFixed(i, j)) {
      System.out.println("Attempted to update a fixed value! Aborting");
      return grid;
    }

    grid.setValue(i, j, value);

    // Update constraints for elements in the same row, column, and box.
    for (Pair<Integer, Integer> sameRowCoord : grid.getRowElements(i, j)) {
      grid.setCandidate(sameRowCoord.first(), sameRowCoord.second(), value, false);
    }

    for (Pair<Integer, Integer> sameColCoord : grid.getColumnElements(i, j)) {
      grid.setCandidate(sameColCoord.first(), sameColCoord.second(), value, false);
    }

    for (Pair<Integer, Integer> sameBoxCoord : grid.getBoxElements(i, j)) {
      grid.setCandidate(sameBoxCoord.first(), sameBoxCoord.second(), value, false);
    }
    System.out.println(grid.gridToString());
    return grid;
  }

  @Override
  public ISquareSudokuGrid solve(ISquareSudokuGrid grid) {
    // Check for elements with only one candidate value (naked single). These elements should have
    // their value set to the single candidate value.
    boolean updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (grid.getCandidateValues(r, c).size() == 1 && !grid.isFixed(r, c)) {
          int nakedSingle = (Integer) (grid.getCandidateValues(r, c).toArray()[0]);
          System.out.println("Found naked single in element (" + r + ", " + c + "): " + nakedSingle);
          grid = constrain(grid, r, c, nakedSingle);
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(grid); // Restart to scan from the beginning
    }

    // Check for elements which are the only element in the group (row, column, or box)
    // that contain a certain value as a candidate (hidden single).
    updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (!grid.isFixed(r, c)) {
          // TODO use enum to distinguish between "failed, no constraint violated", "failed, constraint violated" and "success"
          // Check against the row, column, and box (stop as soon as one of them returns true).
          boolean elementUpdated = checkForHiddenSingle(grid, r, c, grid.getRowElements(r, c)) ||
              checkForHiddenSingle(grid, r, c, grid.getColumnElements(r, c)) ||
              checkForHiddenSingle(grid, r, c, grid.getBoxElements(r, c));

          updated = updated || elementUpdated;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(grid); // Restart
    }

    // Check if the candidates for a value in a box are restricted to a specific column or row.
    // If so, that value can't be a candidate anywhere else in that column or row.
    updated = false;
    for (int r = 0; r < grid.getDimension(); r += Math.sqrt(grid.getDimension())) {
      for (int c = 0; c < grid.getDimension(); c += Math.sqrt(grid.getDimension())) {
        for (int value = 1; value <= grid.getDimension(); value++) {
          // Separate the checks to avoid the short-circuit caused by using the || operator.
          if (checkForRowLockedCandidate(grid, value, grid.getBoxElements(r, c))) {
            updated = true;
          }

          if (checkForColumnLockedCandidate(grid, value, grid.getBoxElements(r, c))) {
            updated = true;
          }
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(grid); // Restart
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
      if (!(coords.first() == r && coords.second() == c)) {
        candidates.removeAll(grid.getCandidateValues(coords.first(), coords.second()));
      }

      // Early exit: each of this element's candidate values can be found in some other element in the group
      if (candidates.isEmpty()) {
        return false;
      }
    }
    if (candidates.size() > 1) {
      System.out.println("Constraint violated! These values appear only once in element (" + r + ", " + c + "): " +
          DisplayStrings.setToString(candidates));
      return false;
    } else if (candidates.size() == 1) {
      int hiddenSingle = (Integer) (candidates.toArray()[0]);
      System.out.println("Found hidden single in element (" + r + ", " + c + "): " + hiddenSingle);
//      System.out.println("Group elements and their candidate values:");
//      for (Pair<Integer, Integer> coords : groupCoordinates) {
//        System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//            DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//      }
      constrain(grid, r, c, hiddenSingle);
      return true;
    } else {
      return false;
    }
  }

  private boolean checkForRowLockedCandidate(ISquareSudokuGrid grid,
                                             int value,
                                             List<Pair<Integer, Integer>> boxElements) {
    boolean inARow = false;
    int lockedRow = -1;
    Pair<Integer, Integer> boxCoordinates = new Pair<>(-1, -1);
    for (Pair<Integer, Integer> coord : boxElements) {
      // If this value is already fixed in this box, this check is invalid.
      if (grid.isFixed(coord.first(), coord.second()) && grid.getValue(coord.first(), coord.second()) == value) {
        return false;
      }

      // Check if the value is a candidate in this row (ignoring multiple occurrences in the same row).
      if (grid.isACandidate(coord.first(), coord.second(), value) && lockedRow != coord.first()) {
        if (!inARow) {
          inARow = true;
          lockedRow = coord.first();
        } else {
          return false; // Found in more than one row.
        }
      }
      boxCoordinates = grid.getBoxCoordinates(coord.first(), coord.second());
    }

    // If control reaches here, means that this value is restricted to a single row in this box.
    System.out.println("Found locked candidate in box (" + boxCoordinates.first() + ", " + boxCoordinates.second() +
        "), row = " + lockedRow + ": " + value);
//    System.out.println("Box elements and their candidate values:");
//    for (Pair<Integer, Integer> coords : boxElements) {
//      System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//          DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//    }
    boolean updated = false;
    for (int c = 0; c < grid.getDimension(); c++) {
      // Don't constrain the elements in the same box.
      if (!grid.getBoxCoordinates(lockedRow, c).equals(boxCoordinates)) {
        if (grid.isACandidate(lockedRow, c, value)) {
          grid.setCandidate(lockedRow, c, value, false);
          updated = true;
          System.out.println("Removed " + value + " as a candidate from element (" + lockedRow + ", " + c + ")");
        }
      }
    }
    return updated;
  }

  private boolean checkForColumnLockedCandidate(ISquareSudokuGrid grid,
                                                int value,
                                                List<Pair<Integer, Integer>> boxElements) {
    boolean inAColumn = false;
    int lockedColumn = -1;
    Pair<Integer, Integer> boxCoordinates = new Pair<>(-1, -1);
    for (Pair<Integer, Integer> coord : boxElements) {
      // If this value is already fixed in this box, this check is invalid.
      if (grid.isFixed(coord.first(), coord.second()) && grid.getValue(coord.first(), coord.second()) == value) {
        return false;
      }

      // Check if the value is a candidate in this row (ignoring multiple occurrences in the same row).
      if (grid.isACandidate(coord.first(), coord.second(), value) && lockedColumn != coord.second()) {
        if (!inAColumn) {
          inAColumn = true;
          lockedColumn = coord.second();
        } else {
          return false; // Found in more than one column.
        }
      }
      boxCoordinates = grid.getBoxCoordinates(coord.first(), coord.second());
    }

    // If control reaches here, means that this value is restricted to a single row in this box.
    System.out.println("Found locked candidate in box (" + boxCoordinates.first() + ", " + boxCoordinates.second() +
        "), column = " + lockedColumn + ": " + value);
//    System.out.println("Box elements and their candidate values:");
//    for (Pair<Integer, Integer> coords : boxElements) {
//      System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//          DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//    }
    boolean updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      // Don't constrain the elements in the same box.
      if (!grid.getBoxCoordinates(r, lockedColumn).equals(boxCoordinates)) {
        if (grid.isACandidate(r, lockedColumn, value)) {
          grid.setCandidate(r, lockedColumn, value, false);
          updated = true;
          System.out.println("Removed " + value + " as a candidate from element (" + r + ", " + lockedColumn + ")");
        }
      }
    }
    return updated;
  }

  public static void main(String[] args) {
    ISquareSudokuGrid partiallyFilledGrid = new StandardSudokuGrid(new int[][] {
        {0, 0, 0, 1, 5, 0, 0, 0, 3},
        {7, 0, 4, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 2, 7, 6, 0},
        {0, 7, 0, 9, 0, 0, 0, 2, 0},
        {0, 0, 0, 8, 0, 6, 0, 0, 0},
        {0, 9, 0, 0, 0, 5, 0, 4, 0},
        {0, 3, 5, 2, 0, 0, 0, 0, 0},
        {0, 0, 7, 0, 0, 0, 2, 0, 1},
        {2, 0, 0, 0, 9, 1, 0, 0, 0}
    });
    ISquareSudokuSolver solver = new ConstraintBasedSolver();
    System.out.println(partiallyFilledGrid.gridToString());
    solver.solve(initializeCandidateValues(partiallyFilledGrid));
  }
}
