package grid;

import org.jetbrains.annotations.Contract;

/**
 * A 9x9 grid for sudoku puzzles. Each element holds a number between 0 and 9 inclusive (0 represents no value).
 */
public class Grid {
  private final int[][] grid = new int[9][9];

  /**
   * Creates an empty sudoku grid.
   */
  public Grid() {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        grid[r][c] = 0;
      }
    }
  }

  /**
   * Creates a sudoku grid with the initial values given.
   * @param initialGrid the initial values for the sudoku grid
   */
  public Grid(int[][] initialGrid) {
    if (!verifyGrid(initialGrid)) {
      throw new AssertionError();
    }

    System.arraycopy(initialGrid, 0, grid, 0, 9);
  }

  /**
   * Verifies that the two dimensional array is a valid sudoku grid (i.e. the dimensions are 9x9 and
   * each element is a number between 0 and 9). Note: This method does NOT enforce the constraints of a sudoku
   * grid.
   * @param someGrid  the grid to verify
   * @return  whether the grid is valid
   */
  @Contract(pure = true)
  private boolean verifyGrid(int[][] someGrid) {
    if (someGrid.length != 9) {
      return false;
    }
    for (int r = 0; r < 9; r++) {
      if (someGrid[r].length != 9) {
        return false;
      }
      for (int c = 0; c < 9; c++) {
        if (someGrid[r][c] < 0 || someGrid[r][c] > 9) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Returns a human-readable string representation of the sudoku grid.
   * @return  the sudoku grid string representation
   */
  public String printGrid() {
    StringBuilder output = new StringBuilder();
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        if (grid[r][c] == 0) {
          output.append(' ');
        } else {
          output.append(grid[r][c]);
        }

        if (c == 2 || c == 5) {
          output.append('|');
        }
        if (c == 8) {
          output.append('\n');
        }
      }
      if (r == 2 || r == 5) {
        output.append("---+---+---\n");
      }
    }
    return output.toString();
  }

  public static void main(String[] args) {
    Grid g = new Grid();
    System.out.println(g.printGrid());

    int[][] nums = {
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9},
        {1, 2, 3, 4, 5, 6, 7, 8, 9}
    };
    Grid g2 = new Grid(nums);
    System.out.println(g2.printGrid());
  }
}
