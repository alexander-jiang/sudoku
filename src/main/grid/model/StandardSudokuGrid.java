package main.grid.model;

import main.util.Pair;

import java.util.*;

/**
 * Representation of a standard, 9x9 Sudoku main.grid.
 */
public class StandardSudokuGrid implements ISquareSudokuGrid {

  private static final int N = 9;

  private int[][] values = new int[N][N];

  private int[][] candidateSets = new int[N][N];

  /**
   * Creates an empty Sudoku main.grid.
   */
  public StandardSudokuGrid() {

  }

  /**
   * Constructs a Sudoku main.grid with the given initial values already
   * assigned.
   * @param initialValues a two-dimensional (N by N) array containing the initial values
   */
  public StandardSudokuGrid(int[][] initialValues) {
    this.values = getGridCopy(initialValues);
  }

  /**
   * Constructs a Sudoku main.grid with the given initial values and candidate values already
   * assigned.
   * @param initialValues a two-dimensional (N by N) array containing the initial values
   * @param candidateSets a two-dimensional (N by N) array containing integers (bit strings) that store which values are candidates
   */
  public StandardSudokuGrid(int[][] initialValues, int[][] candidateSets) {
    this.values = getGridCopy(initialValues);
    this.candidateSets = getGridCopy(candidateSets);
  }

  private int[][] getGridCopy(int[][] grid) {
    int[][] copy = new int[grid.length][grid[0].length];
    // Manual array copy required to keep the arrays separate in memory.
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        copy[i][j] = grid[i][j];
      }
    }
    return copy;
  }

  @Override
  public int getDimension() {
    return N;
  }

  @Override
  public int getValue(int i, int j) {
    return values[i][j];
  }

  @Override
  public ISquareSudokuGrid setValue(int i, int j, int newValue) {
    int[][] valuesCopy = getGridCopy(values);
    valuesCopy[i][j] = newValue;
    return new StandardSudokuGrid(valuesCopy, candidateSets); // assert newValue is between 1 and 9? what if you set to 0?
  }

  @Override
  public Pair<Integer, Integer> getBoxCoordinates(int i, int j) {
    return new Pair<>(i / 3, j / 3); // should depend on N
  }

  @Override
  public List<Pair<Integer, Integer>> getRowElements(int i, int j) {
    ArrayList<Pair<Integer, Integer>> elements = new ArrayList<>(9);
    for (int c = 0; c < N; c++) {
      elements.add(new Pair<>(i, c));
    }
    return elements;
  }

  @Override
  public List<Pair<Integer, Integer>> getColumnElements(int i, int j) {
    ArrayList<Pair<Integer, Integer>> elements = new ArrayList<>(9);
    for (int r = 0; r < N; r++) {
      elements.add(new Pair<>(r, j));
    }
    return elements;
  }

  @Override
  public List<Pair<Integer, Integer>> getBoxElements(int i, int j) {
    ArrayList<Pair<Integer, Integer>> elements = new ArrayList<>(9);
    Pair<Integer, Integer> boxCoordinates = getBoxCoordinates(i, j);
    for (int r = 0; r < 3; r++) { // should depend on N
      for (int c = 0; c < 3; c++) {
        elements.add(new Pair<>(boxCoordinates.first() * 3 + r, boxCoordinates.second() * 3 + c)); // should depend on N
      }
    }
    return elements;
  }

  @Override
  public List<Pair<Integer, Integer>> getBoxElementsByCoordinates(Pair<Integer, Integer> boxCoordinates) {
    return getBoxElements(boxCoordinates.first() * 3, boxCoordinates.second() * 3); // should depend on N
  }

  @Override
  public boolean isACandidate(int i, int j, int value) {
    // A fixed element should have only one candidate value: the element's value in the grid.
    if (isFixed(i, j)) {
      return value == getValue(i, j);
    }

    int mask = 0x0001 << value; // the 2^value bit is set to 1, all other bits are set to 0.
    return (candidateSets[i][j] & mask) != 0;
  }

  @Override
  public Set<Integer> getCandidateValues(int i, int j) {
    // A fixed element should have only one candidate value: the element's value in the grid.
    if (isFixed(i, j)) {
      return new TreeSet<>(Collections.singleton(getValue(i, j)));
    }

    Set<Integer> candidateValues = new TreeSet<>();
    for (int value = 1; value <= 9; value++) {
      if (isACandidate(i, j, value)) {
        candidateValues.add(value);
      }
    }
    return candidateValues;
  }

  @Override
  public ISquareSudokuGrid setCandidate(int i, int j, int value, boolean isCandidate) {
    int[][] copyCandidateSets = getGridCopy(candidateSets);
    if (isFixed(i, j)) {
      copyCandidateSets[i][j] = 0;
    } else {
      int mask = 0x0001 << value; // the 2^value bit is set to 1, all other bits are set to 0.
      if (isCandidate) {
        copyCandidateSets[i][j] = copyCandidateSets[i][j] | mask;
      } else {
        copyCandidateSets[i][j] = copyCandidateSets[i][j] & ~mask;
      }
    }
    return new StandardSudokuGrid(values, copyCandidateSets);
  }

  @Override
  public boolean isFixed(int i, int j) {
    return values[i][j] != 0;
  }

  @Override
  public boolean isSolved() {
    // Check that all elements are assigned a value.
    for (int r = 0; r < this.getDimension(); r++) {
      for (int c = 0; c < this.getDimension(); c++) {
        if (!this.isFixed(r, c)) {
          return false;
        }
      }
    }

    // Check each row, column, and box (every number must appear exactly once).
    for (int r = 0; r < this.getDimension(); r++) {
      Set<Integer> values = new TreeSet<>();
      List<Pair<Integer, Integer>> elements = this.getRowElements(r, 0);
      for (Pair<Integer, Integer> coords : elements) {
        if (values.contains(this.getValue(coords.first(), coords.second()))) {
          return false; // Same number appears twice in the row
        } else {
          values.add(this.getValue(coords.first(), coords.second()));
        }
      }
    }

    for (int c = 0; c < this.getDimension(); c++) {
      Set<Integer> values = new TreeSet<>();
      List<Pair<Integer, Integer>> elements = this.getColumnElements(0, c);
      for (Pair<Integer, Integer> coords : elements) {
        if (values.contains(this.getValue(coords.first(), coords.second()))) {
          return false; // Same number appears twice in the column
        } else {
          values.add(this.getValue(coords.first(), coords.second()));
        }
      }
    }

    for (int r = 0; r < this.getDimension(); r += Math.sqrt(this.getDimension())) {
      for (int c = 0; c < this.getDimension(); c += Math.sqrt(this.getDimension())) {
        Set<Integer> values = new TreeSet<>();
        List<Pair<Integer, Integer>> elements = this.getBoxElements(r, c);
        for (Pair<Integer, Integer> coords : elements) {
          if (values.contains(this.getValue(coords.first(), coords.second()))) {
            return false; // Same number appears twice in the box
          } else {
            values.add(this.getValue(coords.first(), coords.second()));
          }
        }
      }
    }

    return true;
  }

  @Override
  public String gridToString() {
    StringBuilder output = new StringBuilder();
    for (int r = 0; r < 9; r++) {
      for (int c = 0; c < 9; c++) {
        if (values[r][c] == 0) {
          output.append(' ');
        } else {
          output.append(values[r][c]);
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
}
