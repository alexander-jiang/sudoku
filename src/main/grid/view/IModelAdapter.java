package main.grid.view;

import java.util.Set;

/**
 * The adapter that allows the view to talk to the model.
 */
public interface IModelAdapter {

  /**
   * Tells the model to update the value in the given grid element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param newValue  the value that the element should be updated to
   */
  void updateGridValue(int i, int j, int newValue);

  /**
   * Tells the model to update the candidate value in the given grid element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param candidateValue  the candidate value that should be updated
   * @param isCandidate whether the value should be displayed as a candidate or not
   */
  void updateGridCandidateValue(int i, int j, int candidateValue, boolean isCandidate);

  /**
   * Tells the model to return the candidate values for the given grid element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   */
  Set<Integer> getCandidateValues(int i, int j);
}
