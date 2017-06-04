package main.grid.model;

/**
 * The MVC model for the Sudoku grid.
 */
public class GridModel {
  private ISquareSudokuGrid grid;

  private IViewAdapter view;

  public GridModel(ISquareSudokuGrid grid, IViewAdapter viewAdpt) {
    this.grid = grid;
    this.view = viewAdpt;
  }

  public void start() {
    view.updateDisplayGrid();
  }

  public ISquareSudokuGrid getGrid() {
    return grid;
  }

  public IViewAdapter getViewAdapter() {
    return view;
  }
}
