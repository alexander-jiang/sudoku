package grid.model;

import util.Pair;

import java.util.List;

/**
 * Interface for grids of square Sudoku variants.
 *
 * <p>In other words, the grid has N rows and N columns and is further
 * subdivided into N square sub-regions ("boxes"). Note that this means N
 * (the "dimension" of the grid) must be a positive perfect square.</p>
 *
 * <p>The grid must be filled in such that each element of the grid contains
 * exactly one number between 1 and N inclusive. Further, each row, column,
 * and box must contain each of the numbers between 1 and N inclusive exactly
 * once. A grid that is filled in this manner is a "solved" grid.</p>
 */
public interface ISquareSudokuGrid {
  /**
   * Returns the value N, the number of elements in each group (i.e. row, column, and box).
   * Note that N must be a positive perfect square.
   * @return  the dimension of the grid
   */
  int getDimension();

  /**
   * Returns the value in the element in the i-th row and j-th column of the grid. If no value has
   * been assigned yet, returns 0. Otherwise, it must return a value between 1 and N inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  the given element's value, or 0 if the element hasn't been assigned a value
   */
  int getValue(int i, int j);

  /**
   * Updates the grid so that the element at (i, j) in the grid now has a value of newValue.
   * newValue must be between 1 and N inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param newValue  the element's new value
   */
  void setValue(int i, int j, int newValue);

  /**
   * Returns the row-major coordinates of the box that contains the element at (i, j) in the grid.
   * The two integers in the ordered pair must be between 0 and sqrt(N) - 1 inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  the coordinates of the box that contains the given element
   */
  Pair<Integer, Integer> getBoxCoordinates(int i, int j);

  /**
   * Returns the row-major coordinates of the N elements in the same row as the element at (i, j)
   * in the grid (including the element at (i, j)).
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  a list of coordinates of all elements in the same row as the given element
   */
  List<Pair<Integer, Integer>> getRowElements(int i, int j);

  /**
   * Returns the row-major coordinates of the N elements in the same column as the element at (i, j)
   * in the grid (including the element at (i, j)).
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  a list of coordinates of all elements in the same column as the given element
   */
  List<Pair<Integer, Integer>> getColumnElements(int i, int j);

  /**
   * Returns the row-major coordinates of the N elements in the same box as the element at (i, j)
   * in the grid (including the element at (i, j)).
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  a list of coordinates of all elements in the same box as the given element
   */
  List<Pair<Integer, Integer>> getBoxElements(int i, int j);

  /**
   * Returns whether the value is marked as a candidate for the element at (i, j). value must
   * be between 1 and N inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param value the value to check for candidacy
   * @return  whether the given value is a candidate in the given element of the grid
   */
  boolean isACandidate(int i, int j, int value);

  /**
   * Updates the grid so that the element at (i, j) in the grid either has value as a candidate
   * or does not have value as a candidate, depending on if isCandidate is true or false, respectively.
   * value must be between 1 and N inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param value the value whose candidacy will be set
   * @param isCandidate the new candidacy
   */
  void setCandidate(int i, int j, int value, boolean isCandidate);

  /**
   * Returns whether the element has been assigned a value, meaning that only one candidate
   * value remains.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  whether the element has been assigned a value
   */
  boolean isFixed(int i, int j);
}
