package main.grid.model;

import main.util.Pair;

import java.util.List;
import java.util.Set;

/**
 * Interface for grids of square Sudoku variants.
 *
 * <p>In other words, the grid has N rows and N columns and is further
 * subdivided into N square sub-regions ("boxes"). Note that this means N
 * (the "dimension" of the grid) must be a positive perfect square.</p>
 */
public interface ISquareSudokuGrid {
  /**
   * Returns the value N, the number of elements in each group (i.e. row, column, and box).
   * Note that N must be a positive perfect square.
   * @return  the dimension of the grid
   */
  int getDimension();

  /**
   * Returns a copy of the current state of the grid.
   * @return  the copy of the grid
   */
  ISquareSudokuGrid copy();

  /**
   * Returns the value in the element in the i-th row and j-th column of the grid. If no value has
   * been assigned yet, returns 0. Otherwise, it must return a value between 1 and N inclusive.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  the given element's value, or 0 if the element hasn't been assigned a value
   */
  int getValue(int i, int j);

  /**
   * Updates the grid with the element at (i, j) updated to a value of newValue. Also updates
   * elements in the same row, column, or box as (i, j) to not have a candidate value of newValue.
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
   * Returns the row-major coordinates of the N elements in the box at box coordinates (i, j)
   * in the grid.
   * @param i the row coordinate of the box in the grid
   * @param j the column coordinate of the box in the grid
   * @return  a list of coordinates of all elements in the box
   */
  List<Pair<Integer, Integer>> getBoxElementsByCoordinates(Pair<Integer, Integer> boxCoordinates);

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
   * Returns a set of candidate values for the element at (i, j) in the grid.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @return  a set containing all candidate values for the given element
   */
  Set<Integer> getCandidateValues(int i, int j);

  /**
   * Updates the grid with the element at (i, j) set to either have value as a candidate
   * or not have value as a candidate, depending on if isCandidate is true or false, respectively.
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

  /**
   * Returns whether the grid is solved: every element has been assigned a value, and the
   * constraints are satisfied.
   * @return  whether the grid is solved
   */
  boolean isSolved();

  boolean checkBasicConstraints();

  /**
   * Returns a string representation of the Sudoku grid.
   * @return  a human-readable representation of the grid.
   */
  String gridToString();

  /**
   * Returns a compact string representation of the Sudoku grid (values only).
   * @return  a compact representation of the grid's values
   */
  String compactString();

  /**
   * Checks for equality by only comparing the values (not the candidates).
   * @param otherGrid the other grid
   * @return  whether the other grid has the same values as the current grid
   */
  boolean valuesEqual(ISquareSudokuGrid otherGrid);
}