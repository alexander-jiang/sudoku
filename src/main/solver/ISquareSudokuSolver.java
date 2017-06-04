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
   * Returns the solved grid, if the solver is able to find a solution from the initial grid.
   * @param grid  the initial grid (with initial values already set)
   * @return  the solved grid, or an incomplete grid if the solver was unable to solve the grid
   */
  ISquareSudokuGrid solve(ISquareSudokuGrid grid);
}
