package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.ConstraintBasedSolver;
import main.solver.ISquareSudokuSolver;
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

    ISquareSudokuSolver solver = new ConstraintBasedSolver(nakedSingle);
    ISquareSudokuGrid initialized = solver.getGrid();
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
    ISquareSudokuSolver solver = new ConstraintBasedSolver(nakedSingle);
    ISquareSudokuGrid solved = solver.solve();
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

  @Test
  public void solveHiddenSingles() throws Exception {
    ISquareSudokuGrid hiddenSingle = new StandardSudokuGrid(new int[][] {
        {0, 2, 8, 0, 0, 7, 0, 0, 0},
        {0, 1, 6, 0, 8, 3, 0, 7, 0},
        {0, 0, 0, 0, 2, 0, 8, 5, 1},
        {1, 3, 7, 2, 9, 0, 0, 0, 0},
        {0, 0, 0, 7, 3, 0, 0, 0, 0},
        {0, 0, 0, 0, 4, 6, 3, 0, 7},
        {2, 9, 0, 0, 7, 0, 0, 0, 0},
        {0, 0, 0, 8, 6, 0, 1, 4, 0},
        {0, 0, 0, 3, 0, 0, 7, 0, 0}
    });
    ISquareSudokuSolver solver = new ConstraintBasedSolver(hiddenSingle);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution = new StandardSudokuGrid(new int[][] {
        {4, 2, 8, 1, 5, 7, 9, 3, 6},
        {5, 1, 6, 9, 8, 3, 4, 7, 2},
        {3, 7, 9, 6, 2, 4, 8, 5, 1},
        {1, 3, 7, 2, 9, 8, 5, 6, 4},
        {6, 4, 5, 7, 3, 1, 2, 9, 8},
        {9, 8, 2, 5, 4, 6, 3, 1, 7},
        {2, 9, 1, 4, 7, 5, 6, 8, 3},
        {7, 5, 3, 8, 6, 2, 1, 4, 9},
        {8, 6, 4, 3, 1, 9, 7, 2, 5}
    });
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solvePointingLockedCandidates() throws Exception {
    ISquareSudokuGrid pointingLockedCandidate = new StandardSudokuGrid(new int[][] {
        {9, 8, 4, 0, 0, 0, 0, 0, 0},
        {0, 0, 2, 5, 0, 0, 0, 4, 0},
        {0, 0, 1, 9, 0, 4, 0, 0, 2},
        {0, 0, 6, 0, 9, 7, 2, 3, 0},
        {0, 0, 3, 6, 0, 2, 0, 0, 0},
        {2, 0, 9, 0, 3, 5, 6, 1, 0},
        {1, 9, 5, 7, 6, 8, 4, 2, 3},
        {4, 2, 7, 3, 5, 1, 8, 9, 6},
        {6, 3, 8, 0, 0, 9, 7, 5, 1}
    });

    ISquareSudokuSolver solver = new ConstraintBasedSolver(pointingLockedCandidate);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution = new StandardSudokuGrid(new int[][] {
        {9, 8, 4, 2, 7, 3, 1, 6, 5},
        {3, 7, 2, 5, 1, 6, 9, 4, 8},
        {5, 6, 1, 9, 8, 4, 3, 7, 2},
        {8, 5, 6, 1, 9, 7, 2, 3, 4},
        {7, 1, 3, 6, 4, 2, 5, 8, 9},
        {2, 4, 9, 8, 3, 5, 6, 1, 7},
        {1, 9, 5, 7, 6, 8, 4, 2, 3},
        {4, 2, 7, 3, 5, 1, 8, 9, 6},
        {6, 3, 8, 4, 2, 9, 7, 5, 1}
    });
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveClaimingLockedCandidates() throws Exception {
    ISquareSudokuGrid pointingLockedCandidate =
        new StandardSudokuGrid("318..54.6...6.381...6.8.5.3864952137123476958795318264.3.5..78......73.5....39641");

    ISquareSudokuSolver solver = new ConstraintBasedSolver(pointingLockedCandidate);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("318295476957643812246781593864952137123476958795318264631524789489167325572839641");
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenSubsets() throws Exception {
    ISquareSudokuGrid hiddenQuad =
        new StandardSudokuGrid("816573294392......4572.9..6941...5687854961236238...4.279.....1138....7.564....82");

    ISquareSudokuSolver solver = new ConstraintBasedSolver(hiddenQuad);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("816573294392164857457289316941327568785496123623815749279658431138942675564731982");
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedSubsets() throws Exception {
    ISquareSudokuGrid nakedQuad = new StandardSudokuGrid(new int[][] {
        {0, 1, 0, 7, 2, 0, 5, 6, 3},
        {0, 5, 6, 0, 3, 0, 2, 4, 7},
        {7, 3, 2, 5, 4, 6, 1, 8, 9},
        {6, 9, 3, 2, 8, 7, 4, 1, 5},
        {2, 4, 7, 6, 1, 5, 9, 3, 8},
        {5, 8, 1, 3, 9, 4, 0, 0, 0},
        {0, 0, 0, 0, 0, 2, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1},
        {0, 0, 5, 8, 7, 0, 0, 0, 0}
    });

    ISquareSudokuSolver solver = new ConstraintBasedSolver(nakedQuad);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("419728563856931247732546189693287415247615938581394672374152896928463751165879324");
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveXWing() throws Exception {
    ISquareSudokuGrid xWing =
        new StandardSudokuGrid(".41729.3.769..34.2.3264.7194.39..17.6.7..49.319537..24214567398376.9.541958431267");

    ISquareSudokuSolver solver = new ConstraintBasedSolver(xWing);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("841729635769153482532648719423985176687214953195376824214567398376892541958431267");
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveSkyscraper() throws Exception {
    ISquareSudokuGrid skyscraper =
        new StandardSudokuGrid(".76.9..2.2..7.........4...3193....4..274138...4....1329...8.........4.85.8..2.31.");

    ISquareSudokuSolver solver = new ConstraintBasedSolver(skyscraper);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("376598421214736598859241763193852647627413859548679132961385274732164985485927316");
    assertEquals(solution.gridToString(), solved.gridToString());
  }
}