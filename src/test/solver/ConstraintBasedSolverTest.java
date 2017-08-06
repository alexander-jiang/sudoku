package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.ConstraintBasedSolver;
import main.util.Pair;

import java.util.Set;

import static org.junit.Assert.*;

public class ConstraintBasedSolverTest {
  @org.junit.Test
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

}