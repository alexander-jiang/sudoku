package grid;

import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * A 9x9 grid for sudoku puzzles. Each element holds a number between 0 and 9 inclusive (0 represents no value).
 */
public class Grid {
  private final int[][] grid = new int[9][9];
  private List<Set<Integer>> domainGrid = new ArrayList<>(Collections.nCopies(9 * 9, new TreeSet<>()));

  /**
   * Creates an empty sudoku grid.
   */
  public Grid() {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        grid[r][c] = 0;
        domainGrid.set(r * 9 + c, new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
      }
    }
  }

  /**
   * Creates a sudoku grid with the initial values given.
   * @param initialGrid the initial values for the sudoku grid
   */
  public Grid(int[][] initialGrid) {
    if (!verifyGrid(initialGrid)) {
      System.out.println("Provided grid is invalid! (must be a 9x9 grid of integers between 0 and 9 inclusive)");
    }

    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        grid[r][c] = initialGrid[r][c];
        if (initialGrid[r][c] == 0) {
          domainGrid.set(r * 9 + c, new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        } else {
          domainGrid.set(r * 9 + c, new TreeSet<>(Collections.singletonList(initialGrid[r][c])));
        }
      }
    }

    if (!checkConstraints()) {
      System.out.println("Initial grid doesn't satisfy constraints!");
    }
  }

  public void solve() {
    scan();
  }

  private void scan() {
//    boolean flag = true; // need a flag that checks whether constrain(r, c) has already been called (a new 2d array of booleans??)
//    while (flag) {
//      flag = false;
      // Scan each element.
      for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
          if (domainGrid.get(r * 9 + c).size() == 1) {
            // The element is fixed to a single value.
            Integer[] dummyArray = new Integer[] {};
            grid[r][c] = domainGrid.get(r * 9 + c).toArray(dummyArray)[0];
            System.out.println("grid[" + r + "][" + c + "] is constrained to one value: " + grid[r][c]);
            System.out.println("Update to grid:");
            System.out.println(printGrid());
            constrain(r, c);
//            flag = true;
          }
        }
      }
//    }
    printDomains();
    resolve();
  }

  private void printDomains() {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        StringBuilder sb = new StringBuilder("grid[" + r + "][" + c + "] is constrained to the value(s): ");
        Object[] values = domainGrid.get(r * 9 + c).toArray();
        for (int i = 0; i < values.length; i++) {
          sb.append(values[i].toString());
          if (i != values.length - 1) {
            sb.append(", ");
          }
        }
        System.out.println(sb.toString());
      }
    }
  }

  /**
   * Propagates the constrained element to constrain other elements.
   * @param row the row index of the constrained element
   * @param col the column index of the constrained element
   */
  private void constrain(int row, int col) {
    if (grid[row][col] < 1 || grid[row][col] > 9) {
      System.out.println("Invalid value! (must be between 1 and 9)");
      return;
    } else if (domainGrid.get(row * 9 + col).size() != 1 || !domainGrid.get(row * 9 + col).contains(grid[row][col])) {
      System.out.println("Invalid element! (must be constrained to only the value)");
      return;
    }

    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        // Skip if this element is the element that we're propagating from,
        // otherwise if this element is in the same row, column, or 3x3 box, constrain the element.
        if (!(r == row && c == col) && (r == row || c == col || (row / 3 == r / 3 && col / 3 == c / 3))) {
          domainGrid.get(r * 9 + c).remove(grid[row][col]);
        }
      }
    }
  }

  // looks in each row, col, box and tries to place the number (e.g. if 1 only appears in a single variable domain in the
  // row/col/box, then it places the 1 there)
  private void resolve() {

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
   * Checks the constraints on the sudoku grid (i.e. each column, row, and 3x3 box contains at most one of each
   * of the numbers between 1 and 9 inclusive).
   * @return  whether the sudoku grid satisfies the constraints
   */
  boolean checkConstraints() {
    return checkRowConstraints() && checkColConstraints() && checkBoxConstraints();
  }

  private boolean checkRowConstraints() {
    for (int r = 0; r < 9; r++) {
      Set<Integer> rowSet = new TreeSet<>();
      for (int c = 0; c < 9; c++) {
        if (grid[r][c] != 0 && rowSet.contains(grid[r][c])) {
          return false;
        } else {
          rowSet.add(grid[r][c]);
        }
      }
    }
    return true;
  }

  private boolean checkColConstraints() {
    for (int c = 0; c < 9; c++) {
      Set<Integer> colSet = new TreeSet<>();
      for (int r = 0; r < 9; r++) {
        if (grid[r][c] != 0 && colSet.contains(grid[r][c])) {
          return false;
        } else {
          colSet.add(grid[r][c]);
        }
      }
    }
    return true;
  }

  private boolean checkBoxConstraints() {
    for (int startR = 0; startR < 9; startR += 3) {
      for (int startC = 0; startC < 9; startC += 3) {
        Set<Integer> boxSet = new TreeSet<>();
        for (int r = startR; r < 3; r++) {
          for (int c = startC; c < 3; c++) {
            if (grid[r][c] != 0 && boxSet.contains(grid[r][c])) {
              return false;
            } else {
              boxSet.add(grid[r][c]);
            }
          }
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
    System.out.println("checkConstraints() = " + g2.checkConstraints());

    int[][] filledGrid = {
        {3, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    };
    Grid g3 = new Grid(filledGrid);
    System.out.println(g3.printGrid());
    System.out.println("checkConstraints() = " + g3.checkConstraints());

    int[][] partiallyFilledGrid = {
        {0, 0, 0, 2, 0, 5, 0, 0, 0},
        {0, 9, 0, 0, 0, 0, 7, 3, 0},
        {0, 0, 2, 0, 0, 9, 0, 6, 0},
        {2, 0, 0, 0, 0, 0, 4, 0, 9},
        {0, 0, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 9, 0, 0, 0, 0, 0, 1},
        {0, 8, 0, 4, 0, 0, 1, 0, 0},
        {0, 6, 3, 0, 0, 0, 0, 8, 0},
        {0, 0, 0, 6, 0, 8, 0, 0, 0}
    };
    Grid g4 = new Grid(partiallyFilledGrid);
    System.out.println(g4.printGrid());
    System.out.println("checkConstraints() = " + g4.checkConstraints());
    g4.solve();
  }
}
