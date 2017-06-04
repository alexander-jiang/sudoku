package main.grid.model;

/**
 * The adapter that allows the model to talk to the view.
 */
public interface IViewAdapter {
  /**
   * Tells the view to update the displayed grid.
   */
  void updateDisplayGrid();

  // These two methods should be for the solver's view adapter, not the grid's view adapter.
  /**
   * Tells the view to update the value displayed in the given grid element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param newValue  the value that the element should be updated to
   */
  void updateDisplayValue(int i, int j, int newValue);

  /**
   * Tells the view to update the candidate value displayed in the given grid element.
   * @param i the row coordinate of the element in the grid
   * @param j the column coordinate of the element in the grid
   * @param candidateValue  the candidate value that should be updated
   * @param isCandidate whether the value should be displayed as a candidate or not
   */
  void updateDisplayCandidateValue(int i, int j, int candidateValue, boolean isCandidate);
}
