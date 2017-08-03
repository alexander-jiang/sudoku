package main.solver;

import main.grid.model.ISquareSudokuGrid;

/**
 * Interface for solvers of square Sudoku variants.
 *
 * <p>The grid must be filled in such that each element of the grid contains
 * exactly one number between 1 and N inclusive. Further, each row, column,
 * and box must contain each of the numbers between 1 and N inclusive exactly
 * once. A grid that is filled in this manner is a "solved" grid.</p>
 */
public interface ISquareSudokuSolver {
  /**
   * Attempts to make a single step towards the solution. If the solver is unable to
   * make progress, it may return the initial grid.
   * @param grid  the initial grid
   * @return  the grid after a single update has been made or the initial grid if no progress could be made
   */
  ISquareSudokuGrid step(ISquareSudokuGrid grid);

  /**
   * Returns whether the grid is solved: every element has been assigned a value, and the
   * constraints are satisfied.
   * @param grid  the grid to check
   * @return  whether the grid is solved
   */
  boolean isSolved(ISquareSudokuGrid grid);

  /**
   * Returns the solved grid, if the solver is able to find a solution from the initial grid.
   * @param grid  the initial grid (with initial values already set)
   * @return  the solved grid, or an incomplete grid if the solver was unable to solve the grid
   */
  ISquareSudokuGrid solve(ISquareSudokuGrid grid);
}
