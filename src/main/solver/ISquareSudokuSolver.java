package main.solver;

import main.grid.model.ISquareSudokuGrid;

import java.util.List;

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
   * Gets the current state of the grid (that the solver sees).
   * @return  the grid
   */
  ISquareSudokuGrid getGrid();

  /**
   * Attempts to make a single step towards the solution. If the solver is unable to
   * make progress, it may return the initial grid.
   * @return  the grid after a single update has been made or the initial grid if no progress could be made
   */
  ISquareSudokuGrid step();

  /**
   * Returns the solved grid, if the solver is able to find a solution from the initial grid.
   * @return  the solved grid, or an incomplete grid if the solver was unable to solve the grid
   */
  ISquareSudokuGrid solve();

  /**
   * Attempts to find multiple solutions.
   * @return  a list containing the different solutions
   */
  List<ISquareSudokuGrid> findAllSolutions();
}
