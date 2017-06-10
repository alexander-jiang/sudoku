package main.solver;

import main.grid.model.ISquareSudokuGrid;

/**
 * A naive, brute-force solver that attempts to place each possible value in each empty
 * square and checks the constraints of the whole grid after each attempted place. If the
 * constraints are invalidated, the last placed value is erased and replaced with another
 * value. If the constraints are not valid, the solve attempts to place a value in the next
 * empty square. This process repeats until all squares are filled and the constraints
 * are met.
 */
public class BruteForceSolver implements ISquareSudokuSolver {
  @Override
  public ISquareSudokuGrid solve(ISquareSudokuGrid grid) {
    return null;
  }
}
