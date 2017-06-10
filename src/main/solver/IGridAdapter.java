package main.solver;

import java.util.Set;

/**
 * The adapter that allows the solver to update the grid's representation.
 */
public interface IGridAdapter {
  /**
   * Tells the grid to update the value displayed in the given element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param newValue  the value that the element should be updated to
   */
  void updateGridValue(int i, int j, int newValue);

  /**
   * Tells the grid to update the candidate value displayed in the given element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param candidateValue  the candidate value that should be updated
   * @param isCandidate whether the value should be displayed as a candidate or not
   */
  void updateGridCandidateValue(int i, int j, int candidateValue, boolean isCandidate);

  /**
   * Tells the grid to return the candidate values for the given element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   */
  Set<Integer> getCandidateValues(int i, int j);
}
