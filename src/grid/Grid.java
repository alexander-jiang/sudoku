package grid;

import org.jetbrains.annotations.Contract;

import java.util.*;

/**
 * A 9x9 grid for sudoku puzzles. Each element holds a number between 0 and 9 inclusive (0 represents no value).
 */
public class Grid {
  /**
   * Stores the values in each square of the grid as a number between 0 and 9 inclusive (0 represents an unknown value).
   */
  private final int[][] grid = new int[9][9];

  private boolean[][] fixedGrid = new boolean[9][9];

  /**
   * Stores the fixed values in each column of the sudoku grid (where the first element in the list
   * corresponds to the top row and the last element corresponds to the bottom row).
   */
  private List<Set<Integer>> rowConstraints = new ArrayList<>(Arrays.asList(new TreeSet<>(), new TreeSet<>(),
      new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>()));

  /**
   * Stores the fixed values in each column of the sudoku grid (where the first element in the list
   * corresponds to the left column and the last element corresponds to the right column).
   */
  private List<Set<Integer>> colConstraints = new ArrayList<>(Arrays.asList(new TreeSet<>(), new TreeSet<>(),
      new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>()));

  /**
   * Stores the fixed values in each 3 by 3 box in the sudoku grid (where the first element in the list corresponds
   * to the top-left box, the second element corresponds to the top-center box, and so on).
   */
  private List<Set<Integer>> boxConstraints = new ArrayList<>(Arrays.asList(new TreeSet<>(), new TreeSet<>(),
      new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>(), new TreeSet<>()));

//  private List<Set<Integer>> domainGrid = new ArrayList<>(Collections.nCopies(9 * 9, new TreeSet<>()));

  /**
   * Creates an empty sudoku grid.
   */
  public Grid() {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        grid[r][c] = 0;
        fixedGrid[r][c] = false;
//        domainGrid.set(r * 9 + c, new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
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
          fixedGrid[r][c] = false;
//          domainGrid.set(r * 9 + c, new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        } else {
          rowConstraints.get(r).add(grid[r][c]);
          colConstraints.get(c).add(grid[r][c]);
          boxConstraints.get(r / 3 * 3 + c / 3).add(grid[r][c]);
          fixedGrid[r][c] = true;
//          domainGrid.set(r * 9 + c, new TreeSet<>(Collections.singletonList(initialGrid[r][c])));
        }
      }
    }

    if (!checkConstraints()) {
      System.out.println("Initial grid doesn't satisfy constraints!");
    }
  }

  public void solve() {
    boolean flag = true; // whether a value was fixed in this pass (if not, terminate)
    while (flag) {
      flag = false;
      // Resolve "each element in the same row, column, and box must be different" constraint by removing values from
      // variable domains (e.g. if a fixed value of 1 appears, remove 1 from all domains in the same column, row, and box).
      for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
          Set<Integer> values = domain(r, c);
          // Ignore previously fixed values.
          if (!fixedGrid[r][c] && values.size() == 1) {
            // The element is fixed to a single value.
            Integer[] dummyArray = new Integer[]{};
            grid[r][c] = values.toArray(dummyArray)[0];
            System.out.println("grid[" + r + "][" + c + "] is constrained to one value: " + grid[r][c]);
            System.out.println("Update to grid:");
            System.out.println(printGrid());
            constrain(r, c);
            flag = true;
          }
        }
      }

      // Resolve "each value must appear at least once in each row, column, and box" constraint by removing values from
      // variable domains (e.g. if 1 appears in only one of the domains in a row, the domain can only contain the value 1).
      for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
          // Ignore previously fixed values.
          if (!fixedGrid[r][c]) {
            Set<Integer> values = domain(r, c);
            // Does the value occur in another domain in the same row, column, or box?
            for (int value : values) {
              // Check this row.
              boolean isUniqueInRow = true;
              int c2 = 0;
              while (c2 < 9 && isUniqueInRow) {
                if (c2 != c && domain(r, c2).contains(value)) {
                  isUniqueInRow = false;
                }
                c2++;
              }

              // Check this column.
              boolean isUniqueInCol = true;
              int r2 = 0;
              while (r2 < 9 && isUniqueInCol) {
                if (r2 != r && domain(r2, c).contains(value)) {
                  isUniqueInCol = false;
                }
                r2++;
              }

              // Check this box.
              boolean isUniqueInBox = true;
              int r0 = r / 3 * 3;
              int c0 = c / 3 * 3;
              while (r0 < (r / 3 * 3) + 3 && isUniqueInBox) {
                while (c0 < (c / 3 * 3) + 3 && isUniqueInBox) {
                  if (!(r0 == r && c0 == c) && domain(r0, c0).contains(value)) {
                    isUniqueInBox = false;
                  }
                  c0++;
                }
                r0++;
                c0 = c / 3 * 3;
              }

              // If this value can't appear anywhere else in the same column, row, or box, fix this domain only contain
              // that value.
              if (isUniqueInRow || isUniqueInCol || isUniqueInBox) {
                grid[r][c] = value;
                constrain(r, c);
                flag = true;
                System.out.println("grid[" + r + "][" + c + "] must take the value: " + grid[r][c]);
                System.out.println("Update to grid:");
                System.out.println(printGrid());
              }
            }
          }
        }
      }
    }
    printDomains();
  }

  private void printDomains() {
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        StringBuilder sb = new StringBuilder("grid[" + r + "][" + c + "] is constrained to the value(s): ");
//        Object[] values = domainGrid.get(r * 9 + c).toArray();
        Object[] values = domain(r, c).toArray();
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

  private void printConstraints() {
    StringBuilder sb = new StringBuilder("");
    for (int r = 0; r < 9; r++) {
      sb.append("Row ");
      sb.append(r);
      sb.append(" constraints: ");
      Object[] values = rowConstraints.get(r).toArray();
      for (int i = 0; i < values.length; i++) {
        sb.append(values[i].toString());
        if (i != values.length - 1) {
          sb.append(", ");
        }
      }
      sb.append('\n');
    }

    for (int c = 0; c < 9; c++) {
      sb.append("Column ");
      sb.append(c);
      sb.append(" constraints: ");
      Object[] values = colConstraints.get(c).toArray();
      for (int i = 0; i < values.length; i++) {
        sb.append(values[i].toString());
        if (i != values.length - 1) {
          sb.append(", ");
        }
      }
      sb.append('\n');
    }

    for (int b = 0; b < 9; b++) {
      sb.append("Box ");
      sb.append(b);
      sb.append(" constraints: ");
      Object[] values = boxConstraints.get(b).toArray();
      for (int i = 0; i < values.length; i++) {
        sb.append(values[i].toString());
        if (i != values.length - 1) {
          sb.append(", ");
        }
      }
      sb.append('\n');
    }
    System.out.println(sb.toString());
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
    } else if (fixedGrid[row][col]) {
      System.out.println("Invalid element! (must not be a fixed element)");
    } else {
      Set<Integer> values = domain(row, col);
      if (!values.contains(grid[row][col])) {
        System.out.println("Invalid element! (its assigned value must be an unconstrained value)");
        return;
      }
    }

    rowConstraints.get(row).add(grid[row][col]);
    colConstraints.get(col).add(grid[row][col]);
    boxConstraints.get(row / 3 * 3 + col / 3).add(grid[row][col]);
    fixedGrid[row][col] = true; // Prevents the scan from trying to constrain this value again.

//    for (int r = 0; r < 9; r++) {
//      for (int c = 0; c < 9; c++) {
//        // Skip if this element is the element that we're propagating from,
//        // otherwise if this element is in the same row, column, or 3x3 box, constrain the element.
//        if (!(r == row && c == col) && (r == row || c == col || (row / 3 == r / 3 && col / 3 == c / 3))) {
//          domainGrid.get(r * 9 + c).remove(grid[row][col]);
//        }
//      }
//    }
  }

  // looks in each row, col, box and tries to place the number (e.g. if 1 only appears in a single variable domain in the
  // row/col/box, then it places the 1 there)
  private void resolve() {

  }

  // calculates the variable domain of the square at (i, j) using the saved row, column, and box constraints
  private Set<Integer> domain(int r, int c) {
    if (fixedGrid[r][c]) {
      return new TreeSet<>(Collections.singletonList(grid[r][c])); // a fixed element's domain only contains the assigned value
    }
    Set<Integer> values = new TreeSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    values.removeAll(rowConstraints.get(r));
    values.removeAll(colConstraints.get(c));
    values.removeAll(boxConstraints.get(r / 3 * 3 + c / 3));
    return values;
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
  private boolean checkConstraints() {
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

    int[][] sudokuEx1 = {
        {0, 0, 0, 0, 0, 5, 0, 0, 7},
        {7, 0, 0, 0, 8, 9, 1, 0, 3},
        {0, 9, 2, 7, 0, 0, 0, 0, 5},
        {0, 0, 0, 2, 4, 7, 0, 0, 1},
        {0, 0, 5, 0, 0, 0, 9, 0, 0},
        {0, 2, 8, 9, 0, 0, 6, 0, 0},
        {2, 0, 9, 1, 0, 0, 3, 0, 0},
        {3, 4, 6, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    Grid g4 = new Grid(sudokuEx1);
    System.out.println(g4.printGrid());
    System.out.println("checkConstraints() = " + g4.checkConstraints());
    g4.printConstraints();
    g4.solve();

    int[][] sudokuEx2 = {
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
    Grid g5 = new Grid(sudokuEx2);
    System.out.println(g5.printGrid());
    System.out.println("checkConstraints() = " + g5.checkConstraints());
    g5.printConstraints();
    g5.solve();
    g5.printGrid();

    int[][] sudokuEx3 = {
        {0, 0, 0, 1, 5, 0, 0, 0, 3},
        {7, 0, 4, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 2, 7, 6, 0},
        {0, 7, 0, 9, 0, 0, 0, 2, 0},
        {0, 0, 0, 8, 0, 6, 0, 0, 0},
        {0, 9, 0, 0, 0, 5, 0, 4, 0},
        {0, 3, 5, 2, 0, 0, 0, 0, 0},
        {0, 0, 7, 0, 0, 0, 2, 0, 1},
        {2, 0, 0, 0, 9, 1, 0, 0, 0}
    };
    Grid g6 = new Grid(sudokuEx3);
    System.out.println(g6.printGrid());
    System.out.println("checkConstraints() = " + g6.checkConstraints());
    g6.printConstraints();
    g6.solve();
    g6.printGrid();
  }
}
