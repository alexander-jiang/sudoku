package grid.model;

import util.Pair;

import java.util.List;

/**
 * Representation of a standard, 9x9 Sudoku grid.
 */
public class StandardSudokuGrid implements ISquareSudokuGrid {

  /**
   * Creates an empty Sudoku grid.
   */
  public StandardSudokuGrid() {

  }

  @Override
  public int getDimension() {
    return 9;
  }


}
