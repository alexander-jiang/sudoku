package main.grid.model;

import main.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < N; j++) {
        setValue(i, j, initialValues[i][j]);
      }
    }
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
    values[i][j] = newValue; // assert newValue is between 1 and 9? what if you set to 0?
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
  public boolean isACandidate(int i, int j, int value) {
    int mask = 0x0001 << value; // the 2^value bit is set to 1, all other bits are set to 0.
    return (candidateSets[i][j] & mask) != 0;
  }

  @Override
  public Set<Integer> getCandidateValues(int i, int j) {
    Set<Integer> candidateValues = new TreeSet<>();
    for (int value = 1; value <= 9; value++) {
      if (isACandidate(i, j, value)) {
        candidateValues.add(value);
      }
    }
    return candidateValues;
  }

  @Override
  public void setCandidate(int i, int j, int value, boolean isCandidate) {
    int mask = 0x0001 << value; // the 2^value bit is set to 1, all other bits are set to 0.
    if (isCandidate) {
      candidateSets[i][j] = candidateSets[i][j] | mask;
    } else {
      candidateSets[i][j] = candidateSets[i][j] & ~mask;
    }
  }

  @Override
  public boolean isFixed(int i, int j) {
    return values[i][j] != 0;
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