package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.util.Pair;

import java.util.*;

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
//            System.out.println(gridCopy.gridToString());
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

  public ISquareSudokuGrid solveIterative() {
    if (grid.isSolved()) {
      return grid;
    }

    ISquareSudokuGrid gridCopy = grid.copy();
    int r = 0;
    int c = 0;
    while (r < 9 && c < 9) {
      // Ignore the given clues in the original grid
      if (grid.isFixed(r, c)) {
        int c_new = (c + 1) % 9;
        if (c_new == 0) {
          r++;
        }
        c = c_new;
        continue;
      }

      // Start trying candidate values
      int nextCandidate = 0;
      if (gridCopy.isFixed(r, c)) {
        nextCandidate = gridCopy.getValue(r, c);
      }

      boolean placed = false;
      for (int candidate = nextCandidate + 1; candidate <= 9; candidate++) {
        // clear the cell value
        gridCopy.clearValue(r, c);

        // can the candidate value be placed in gridCopy without violating constraints?
        if (gridCopy.peekConstraintsOnPlace(r, c, candidate)) {
          gridCopy.setValue(r, c, candidate);
          System.out.printf("Trying candidate %d at (%d, %d)%n", candidate, r, c);
          placed = true;
          break;
        }
      }
      if (placed) {
        int c_new = (c + 1) % 9;
        if (c_new == 0) {
          r++;
        }
        c = c_new;
        continue;
      }

      // no more candidates can be tried, clear this cell and go back to the previous
      // non-given cell and try the next value
      gridCopy.clearValue(r, c);
      do {
        if (c > 0) {
          c -= 1;
        } else if (r > 0) {
          c = 8;
          r -= 1;
        } else {
          // We've failed to find a solution!
          System.out.println("backtracked to the end with no solution!!");
          return null;
        }
      } while (grid.isFixed(r, c));
      System.out.printf("Exhausted candidates, backtracking to cell (%d, %d)%n", r, c);
    }

    if (gridCopy.isSolved()) {
      return gridCopy;
    } else {
      System.out.println("grid copy is not a solution!");
      return null;
    }
  }

  @Override
  public List<ISquareSudokuGrid> findAllSolutions() {
    List<ISquareSudokuGrid> solutions = new ArrayList<>();
    if (grid.isSolved()) {
      solutions.add(grid);
      return solutions;
    }

    int[][] nextCandidates = new int[9][9];
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        if (grid.isFixed(r, c)) {
          nextCandidates[r][c] = 10;
        } else {
          nextCandidates[r][c] = 1;
        }
      }
    }

    ISquareSudokuGrid gridCopy = grid.copy();
    int r = 0;
    int c = 0;
    while (true) {
      if (gridCopy.isSolved()) {
        ISquareSudokuGrid solution = gridCopy.copy();
        solutions.add(solution);
      }

      boolean exhaustedAllCandidates = true;
      for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
          if (nextCandidates[i][j] <= 9) {
            exhaustedAllCandidates = false;
            break;
          }
        }
      }
      if (exhaustedAllCandidates) {
        break;
      }

      // Ignore the given clues in the original grid
      if (grid.isFixed(r, c)) {
        if (r != 8 || c != 8) {
          int c_new = (c + 1) % 9;
          if (c_new == 0) {
            r++;
          }
          c = c_new;
        }
        continue;
      }

      // Start trying candidate values
      int nextCandidate = nextCandidates[r][c];

      boolean placed = false;
      for (int candidate = nextCandidate; candidate <= 9; candidate++) {
        // clear the cell value
        gridCopy.clearValue(r, c);

        // update the next candidate value for future iterations
        nextCandidates[r][c]++;

        // can the candidate value be placed in gridCopy without violating constraints?
        if (gridCopy.peekConstraintsOnPlace(r, c, candidate)) {
          gridCopy.setValue(r, c, candidate);
          System.out.printf("Trying candidate %d at (%d, %d)%n", candidate, r, c);
          placed = true;
          break;
        }
      }
      if (placed) {
        if (r != 8 || c != 8) {
          int c_new = (c + 1) % 9;
          if (c_new == 0) {
            r++;
          }
          c = c_new;
        }
        continue;
      }

      // no more candidates can be tried, clear this cell and go back to the previous
      // non-given cell and try the next value
      gridCopy.clearValue(r, c);
      nextCandidates[r][c] = 1;
      do {
        if (c > 0) {
          c -= 1;
        } else if (r > 0) {
          c = 8;
          r -= 1;
        } else {
          System.out.println("backtracked to the end");
          return solutions;
        }
      } while (grid.isFixed(r, c));
      System.out.printf("Exhausted candidates, backtracking to cell (%d, %d)%n", r, c);
    }

    return solutions;
  }
}
