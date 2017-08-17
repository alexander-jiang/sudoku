package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.util.Pair;

import java.util.ArrayList;
import java.util.Set;

/**
 * A naive, brute-force solver that attempts to place each possible value in each empty
 * square and checks the constraints of the whole grid after each attempted place. If the
 * constraints are invalidated, the last placed value is erased and replaced with another
 * value. If the constraints are not valid, the solve attempts to place a value in the next
 * empty square. This process repeats until all squares are filled and the constraints
 * are met.
 */
public class BruteForceSolver implements ISquareSudokuSolver {

  private ISquareSudokuGrid grid;

  public BruteForceSolver(ISquareSudokuGrid grid) {
    this.grid = grid;
  }

  @Override
  public ISquareSudokuGrid getGrid() {
    return grid;
  }

  @Override
  public ISquareSudokuGrid step() {
    return null;
  }

  @Override
  public ISquareSudokuGrid solve() {
    if (grid.isSolved()) {
      return grid;
    }

    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (!grid.isFixed(r, c)) {
          Set<Integer> candidates = grid.getCandidateValues(r, c);
          if (candidates.size() == 0) {
            return null; // No candidates left, no solutions.
          }
          for (int candidate : candidates) {
            // Try placing each candidate value.
            ISquareSudokuGrid gridCopy = grid.copy();
            System.out.printf("Setting element (%d, %d) to %d%n", r, c, candidate);
            gridCopy.setValue(r, c, candidate);
            System.out.println(gridCopy.gridToString());
            ISquareSudokuSolver newSolver = new BruteForceSolver(gridCopy);
            ISquareSudokuGrid solution = newSolver.solve();
            if (solution != null && solution.isSolved()) {
              return solution;
            }

            // TODO try removing the candidate and seeing if that advances the puzzle
          }
          // Tried all candidates, haven't found a solution.
          return null;
        }
      }
    }
    System.out.println("Reached the end!");
    return null;
  }

  /**
   * Attempts to find multiple solutions.
   * @return  a list containing the different solutions
   */
  private ArrayList<ISquareSudokuGrid> findAllSolutions() {
    return null;
  }
}
