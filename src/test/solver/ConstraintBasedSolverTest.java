package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.ConstraintBasedSolver;
import main.util.Pair;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ConstraintBasedSolverTest {
  @Test
  public void initializeCandidateValues() throws Exception {
    ISquareSudokuGrid nakedSingle = new StandardSudokuGrid(new int[][] {
      {8, 0, 0, 7, 3, 9, 0, 0, 6},
      {3, 7, 0, 4, 6, 5, 0, 0, 0},
      {0, 4, 0, 1, 8, 2, 0, 0, 9},
      {0, 0, 0, 6, 0, 0, 0, 4, 0},
      {0, 5, 4, 3, 0, 0, 6, 1, 0},
      {0, 6, 0, 5, 0, 0, 0, 0, 0},
      {4, 0, 0, 8, 5, 3, 0, 7, 0},
      {0, 0, 0, 2, 7, 1, 0, 6, 4},
      {1, 0, 0, 9, 4, 0, 0, 0, 2}
    });

    ISquareSudokuGrid initialized = ConstraintBasedSolver.initializeCandidateValues(nakedSingle);
    for (int r = 0; r < initialized.getDimension(); r++) {
      for (int c = 0; c < initialized.getDimension(); c++) {
        if (initialized.isFixed(r, c)) {
          int value = initialized.getValue(r, c);
          for (Pair<Integer, Integer> sameRowCoord : initialized.getRowElements(r, c)) {
            if (!sameRowCoord.equals(new Pair<>(r, c))) {
              assertFalse("Value is fixed as " + value + " in " + new Pair<>(r, c).toString() +
                      " but is candidate in " + sameRowCoord.toString(),
                  initialized.isACandidate(sameRowCoord.first(), sameRowCoord.second(), value));
            }
          }

          for (Pair<Integer, Integer> sameColCoord : initialized.getColumnElements(r, c)) {
            if (!sameColCoord.equals(new Pair<>(r, c))) {
              assertFalse("Value is fixed as " + value + " in " + new Pair<>(r, c).toString() +
                      " but is candidate in " + sameColCoord.toString(),
                  initialized.isACandidate(sameColCoord.first(), sameColCoord.second(), value));
            }
          }

          for (Pair<Integer, Integer> sameBoxCoord : initialized.getBoxElements(r, c)) {
            if (!sameBoxCoord.equals(new Pair<>(r, c))) {
              assertFalse("Value is fixed as " + value + " in " + new Pair<>(r, c).toString() +
                      " but is candidate in " + sameBoxCoord.toString(),
                  initialized.isACandidate(sameBoxCoord.first(), sameBoxCoord.second(), value));
            }
          }
        }
      }
    }

    for (int r = 0; r < initialized.getDimension(); r++) {
      for (int c = 0; c < initialized.getDimension(); c++) {
        if (!initialized.isFixed(r, c)) {
          Set<Integer> candidates = initialized.getCandidateValues(r, c);
          for (Integer candidate : candidates) {
            for (Pair<Integer, Integer> sameRowCoord : initialized.getRowElements(r, c)) {
              assertFalse("Candidate " + candidate + " in " + new Pair<>(r, c).toString() +
                  " is assigned in " + sameRowCoord.toString(),
                  initialized.getValue(sameRowCoord.first(), sameRowCoord.second()) == candidate);
            }

            for (Pair<Integer, Integer> sameColCoord : initialized.getColumnElements(r, c)) {
              assertFalse("Candidate " + candidate + " in " + new Pair<>(r, c).toString() +
                      " is assigned in " + sameColCoord.toString(),
                  initialized.getValue(sameColCoord.first(), sameColCoord.second()) == candidate);
            }

            for (Pair<Integer, Integer> sameBoxCoord : initialized.getBoxElements(r, c)) {
              assertFalse("Candidate " + candidate + " in " + new Pair<>(r, c).toString() +
                      " is assigned in " + sameBoxCoord.toString(),
                  initialized.getValue(sameBoxCoord.first(), sameBoxCoord.second()) == candidate);
            }
          }
        }
      }
    }
  }

  @Test
  public void solveNakedSingles() throws Exception {
    ISquareSudokuGrid nakedSingle = new StandardSudokuGrid(new int[][] {
        {8, 0, 0, 7, 3, 9, 0, 0, 6},
        {3, 7, 0, 4, 6, 5, 0, 0, 0},
        {0, 4, 0, 1, 8, 2, 0, 0, 9},
        {0, 0, 0, 6, 0, 0, 0, 4, 0},
        {0, 5, 4, 3, 0, 0, 6, 1, 0},
        {0, 6, 0, 5, 0, 0, 0, 0, 0},
        {4, 0, 0, 8, 5, 3, 0, 7, 0},
        {0, 0, 0, 2, 7, 1, 0, 6, 4},
        {1, 0, 0, 9, 4, 0, 0, 0, 2}
    });
    ISquareSudokuGrid initialized = ConstraintBasedSolver.initializeCandidateValues(nakedSingle);
    ISquareSudokuGrid solved = new ConstraintBasedSolver().solve(initialized);
    ISquareSudokuGrid solution = new StandardSudokuGrid(new int[][] {
        {8, 1, 2, 7, 3, 9, 4, 5, 6},
        {3, 7, 9, 4, 6, 5, 1, 2, 8},
        {6, 4, 5, 1, 8, 2, 7, 3, 9},
        {9, 8, 3, 6, 1, 7, 2, 4, 5},
        {2, 5, 4, 3, 9, 8, 6, 1, 7},
        {7, 6, 1, 5, 2, 4, 8, 9, 3},
        {4, 2, 6, 8, 5, 3, 9, 7, 1},
        {5, 9, 8, 2, 7, 1, 3, 6, 4},
        {1, 3, 7, 9, 4, 6, 5, 8, 2}
    });
    assertEquals(solution.gridToString(), solved.gridToString());
  }
}