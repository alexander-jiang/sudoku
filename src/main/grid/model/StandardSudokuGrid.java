package main.grid.model;

import main.util.Pair;

import java.util.*;

/**
 * Representation of a standard, 9x9 Sudoku grid.
 */
public class StandardSudokuGrid implements ISquareSudokuGrid {

  private static final int N = 9;

  private int[][] values = new int[N][N];

  private int[][] candidateSets = new int[N][N];

  /**
   * Creates an empty Sudoku grid.
   */
  public StandardSudokuGrid() {
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        values[r][c] = 0;
        for (int value = 1; value <= N; value++) {
          setCandidate(r, c, value, true);
        }
      }
    }
  }

  /**
   * Creates a Sudoku grid with the given initial values.
   * @param compactValues a String containing the initial values
   */
  public StandardSudokuGrid(String compactValues) {
    // TODO consistency checking? i.e. same value doesn't appear twice in the same column, row, box (typos?)
    this();
    int length = compactValues.length();
    if (length != 81) {
      System.out.println("Invalid string: length is not 81!");
    }
    for (int i = 0; i < length; i++) {
      if (compactValues.charAt(i) == '.') {
        values[i / N][i % N] = 0;
      } else if (compactValues.charAt(i) >= '1' && compactValues.charAt(i) <= '9') {
        setValue(i / N, i % N, Character.getNumericValue(compactValues.charAt(i)));
      } else {
        System.out.println("Invalid string: invalid character: " + compactValues.charAt(i));
      }
    }
  }

  /**
   * Constructs a Sudoku grid with the given initial values already
   * assigned.
   * @param initialValues a two-dimensional (N by N) array containing the initial values
   */
  public StandardSudokuGrid(int[][] initialValues) {
    this();
    for (int r = 0; r < N; r++) {
      for (int c = 0; c < N; c++) {
        if (initialValues[r][c] != 0) {
          setValue(r, c, initialValues[r][c]);
        }
      }
    }
  }

  /**
   * Constructs a Sudoku grid with the given initial values and candidate values already
   * assigned.
   * @param initialValues a two-dimensional (N by N) array containing the initial values
   * @param candidateSets a two-dimensional (N by N) array containing integers (bit strings)
   *                      that store which values are candidates
   */
  public StandardSudokuGrid(int[][] initialValues, int[][] candidateSets) {
    this.values = getGridCopy(initialValues);
    this.candidateSets = getGridCopy(candidateSets);
  }

  @Override
  public StandardSudokuGrid copy() {
    return new StandardSudokuGrid(values, candidateSets);
  }

  private int[][] getGridCopy(int[][] grid) {
    int[][] copy = new int[grid.length][grid[0].length];
    // Manual array copy required to keep the arrays separate in memory.
    for (int i = 0; i < N; i++) {
      System.arraycopy(grid[i], 0, copy[i], 0, N);
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
  public void setValue(int i, int j, int newValue) {
    if (isFixed(i, j)) {
      System.out.println("Attempted to update a fixed value! Aborting");
      return;
    }

    values[i][j] = newValue;

    // Update constraints for elements in the same row, column, and box.
    for (Pair<Integer, Integer> sameRowCoord : getRowElements(i, j)) {
      setCandidate(sameRowCoord.first(), sameRowCoord.second(), newValue, false);
    }

    for (Pair<Integer, Integer> sameColCoord : getColumnElements(i, j)) {
      setCandidate(sameColCoord.first(), sameColCoord.second(), newValue, false);
    }

    for (Pair<Integer, Integer> sameBoxCoord : getBoxElements(i, j)) {
      setCandidate(sameBoxCoord.first(), sameBoxCoord.second(), newValue, false);
    }
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
    for (int value = 1; value <= 9; value++) { // TODO should depend on dimension
      if (isACandidate(i, j, value)) {
        candidateValues.add(value);
      }
    }
    return candidateValues;
  }

  @Override
  public void setCandidate(int i, int j, int value, boolean isCandidate) {
    if (isFixed(i, j)) {
      candidateSets[i][j] = 0;
    } else {
      int mask = 0x0001 << value; // the 2^value bit is set to 1, all other bits are set to 0.
      if (isCandidate) {
        candidateSets[i][j] = candidateSets[i][j] | mask;
      } else {
        candidateSets[i][j] = candidateSets[i][j] & ~mask;
      }
    }
  }

  @Override
  public boolean isFixed(int i, int j) {
    return values[i][j] != 0;
  }

  @Override
  public boolean checkBasicConstraints() {
    // check that each row has no repeated values
    for (int r = 0; r < 9; r++) {
      List<Pair<Integer, Integer>> rowElements = getRowElements(r, 0);
      Set<Integer> rowValues = new HashSet<>();
      for (Pair<Integer, Integer> coord : rowElements) {
        if (isFixed(coord.first(), coord.second())) {
          int cellValue = getValue(coord.first(), coord.second());
          if (rowValues.contains(cellValue)) {
            return false;
          }
          rowValues.add(cellValue);
        }
      }
    }

    // check that each column has no repeated values
    for (int c = 0; c < 9; c++) {
      List<Pair<Integer, Integer>> columnElements = getColumnElements(0, c);
      Set<Integer> columnValues = new HashSet<>();
      for (Pair<Integer, Integer> coord : columnElements) {
        if (isFixed(coord.first(), coord.second())) {
          int cellValue = getValue(coord.first(), coord.second());
          if (columnValues.contains(cellValue)) {
            return false;
          }
          columnValues.add(cellValue);
        }
      }
    }

    // check that each box has no repeated values
    for (int r = 0; r < 9; r += 3) {
      for (int c = 0; c < 9; c += 3) {
        List<Pair<Integer, Integer>> boxElements = getBoxElements(r, c);
        Set<Integer> boxValues = new HashSet<>();
        for (Pair<Integer, Integer> coord : boxElements) {
          if (isFixed(coord.first(), coord.second())) {
            int cellValue = getValue(coord.first(), coord.second());
            if (boxValues.contains(cellValue)) {
              return false;
            }
            boxValues.add(cellValue);
          }
        }
      }
    }

    return true;
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

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StandardSudokuGrid)) {
      return false;
    }
    StandardSudokuGrid otherGrid = (StandardSudokuGrid) o;
    if (this.getDimension() != otherGrid.getDimension()) {
      return false;
    }
    for (int r = 0; r < this.getDimension(); r++) {
      for (int c = 0; c < this.getDimension(); c++) {
        if (this.getValue(r, c) != otherGrid.getValue(r, c) &&
            !this.getCandidateValues(r, c).equals(otherGrid.getCandidateValues(r, c))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public boolean valuesEqual(ISquareSudokuGrid otherGrid) {
    if (this.getDimension() != otherGrid.getDimension()) {
      return false;
    }
    for (int r = 0; r < this.getDimension(); r++) {
      for (int c = 0; c < this.getDimension(); c++) {
        if (this.getValue(r, c) != otherGrid.getValue(r, c)) {
          return false;
        }
      }
    }
    return true;
  }

}
