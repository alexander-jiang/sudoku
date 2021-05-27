package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.BruteForceSolver;
import main.solver.ConstraintBasedSolver;
import main.solver.ISquareSudokuSolver;
import main.util.Pair;
import org.junit.Test;

import java.util.List;
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

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenSingles() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_singles.php#h1
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
    ConstraintBasedSolver solver = new ConstraintBasedSolver(hiddenSingle);

    boolean foundHiddenSingle = solver.checkForHiddenSingle(hiddenSingle, 2, 3, hiddenSingle.getRowElements(2, 3));
    assertTrue(foundHiddenSingle);

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

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solvePointingLockedCandidatesRow() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_intersections.php#lc1
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

    ConstraintBasedSolver solver = new ConstraintBasedSolver(pointingLockedCandidate);

    boolean foundLockedCandidate = solver.checkForRowLockedCandidate(pointingLockedCandidate, 5, pointingLockedCandidate.getBoxElements(2, 0));
    assertTrue(foundLockedCandidate);

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

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveClaimingLockedCandidatesRow() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_intersections.php#lc2
    ISquareSudokuGrid pointingLockedCandidate =
        new StandardSudokuGrid("318..54.6...6.381...6.8.5.3864952137123476958795318264.3.5..78......73.5....39641");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(pointingLockedCandidate);
    boolean foundLockedCandidate = solver.checkForBoxLockingCandidate(pointingLockedCandidate, 7, pointingLockedCandidate.getRowElements(1, 1));
    assertTrue(foundLockedCandidate);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("318295476957643812246781593864952137123476958795318264631524789489167325572839641");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveClaimingLockedCandidatesColumn() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_intersections.php#lc2
    ISquareSudokuGrid pointingLockedCandidate =
            new StandardSudokuGrid("762..8..198......615.....87478..3169526..98733198..425835..1692297685314641932758");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(pointingLockedCandidate);
    boolean foundLockedCandidate = solver.checkForBoxLockingCandidate(pointingLockedCandidate, 4, pointingLockedCandidate.getColumnElements(1, 5));
    assertTrue(foundLockedCandidate);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("762398541984157236153264987478523169526419873319876425835741692297685314641932758");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenPairInColumn() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_hidden.php#h2
    ISquareSudokuGrid hiddenPair =
            new StandardSudokuGrid(".49132....81479...327685914.96.518...75.28....38.46..5853267...712894563964513...");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(hiddenPair);

    boolean foundHiddenSet = solver.checkForHiddenSet(hiddenPair, hiddenPair.getColumnElements(4, 8));
    assertTrue(foundHiddenSet);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("649132758581479326327685914496751832175328649238946175853267491712894563964513287");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenPairInRow() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_hidden.php#h2
    ISquareSudokuGrid hiddenPair =
            new StandardSudokuGrid("....6........42736..673..4..94....68....964.76.7.5.9231......85.6..8.271..5.1..94");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(hiddenPair);

    boolean foundHiddenSetInRow = solver.checkForHiddenSet(hiddenPair, hiddenPair.getRowElements(0, 0));
    assertTrue(foundHiddenSetInRow);
    boolean foundHiddenSetInBox = solver.checkForHiddenSet(hiddenPair, hiddenPair.getBoxElements(0, 0));
    assertTrue(foundHiddenSetInBox);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("473965812951842736826731549594327168238196457617458923142679385369584271785213694");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenQuadInBox() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_hidden.php#h4
    ISquareSudokuGrid hiddenQuad =
        new StandardSudokuGrid("816573294392......4572.9..6941...5687854961236238...4.279.....1138....7.564....82");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(hiddenQuad);

    boolean foundHiddenSet = solver.checkForHiddenSet(hiddenQuad, hiddenQuad.getBoxElements(6, 3));
    assertTrue(foundHiddenSet);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("816573294392164857457289316941327568785496123623815749279658431138942675564731982");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveHiddenQuadInColumn() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_hidden.php#h4
    ISquareSudokuGrid hiddenQuad =
            new StandardSudokuGrid(".3.....1...8.9....4..6.8......57694....98352....124...276..519....7.9....95...47.");
    System.out.println(hiddenQuad.gridToString());

    ConstraintBasedSolver solver = new ConstraintBasedSolver(hiddenQuad);

    // need to find the pointing locked candidate first (2 is locked to the ninth row in the bottom-middle box,
    // which eliminates 2 as a candidate from one cell in the ninth column)
    boolean foundPointingLockedCandidates = solver.checkForRowLockedCandidate(hiddenQuad, 2, hiddenQuad.getBoxElements(8, 3));
    assertTrue(foundPointingLockedCandidates);

    boolean foundHiddenSet = solver.checkForHiddenSet(hiddenQuad, hiddenQuad.getColumnElements(0, 8));
    assertTrue(foundHiddenSet);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("639247815528391764417658239182576943764983521953124687276435198841769352395812476");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedPairInRow() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n2n2
    ISquareSudokuGrid nakedPair =
            new StandardSudokuGrid("7..849.3.928135..64..267.896427839513974516288156923..2.4516.931....8.6.5....4.1.");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("761849235928135476453267189642783951397451628815692347284516793179328564536974812");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedPair);
    boolean foundNakedPair = solver.checkForNakedSet(nakedPair, nakedPair.getRowElements(7, 2));
    assertTrue(foundNakedPair);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedPairInBox() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n2n2
    ISquareSudokuGrid nakedPair =
            new StandardSudokuGrid("687..4523953..261414235697831...724676....3.5.2....7.1.96..1.3223.....57.7.....69");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("687914523953872614142356978319587246764129385825463791596741832231698457478235169");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedPair);
    boolean foundNakedPair = solver.checkForNakedSet(nakedPair, nakedPair.getBoxElements(3, 4));
    assertTrue(foundNakedPair);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedTripleInColumn() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n3n3
    ISquareSudokuGrid nakedTriple =
            new StandardSudokuGrid("...29438....17864.48.3561....48375.1...4157..5..629834953782416126543978.4.961253");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("615294387392178645487356129264837591839415762571629834953782416126543978748961253");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedTriple);
    boolean foundNakedTriple = solver.checkForNakedSet(nakedTriple, nakedTriple.getColumnElements(1, 1));
    assertTrue(foundNakedTriple);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedTripleInBox() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n3n3
    ISquareSudokuGrid nakedTriple =
            new StandardSudokuGrid("39....7........65.5.7...349.4938.5.66.1.54983853...4..9..8..134..294.8654.....297");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("396425718184739652527168349249381576671254983853697421965872134712943865438516297");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedTriple);
    boolean foundNakedTriple = solver.checkForNakedSet(nakedTriple, nakedTriple.getBoxElements(0, 4));
    assertTrue(foundNakedTriple);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedQuadInRow() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n4
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

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedQuad);
    boolean foundNakedSet = solver.checkForNakedSet(nakedQuad, nakedQuad.getRowElements(7, 0));
    assertTrue(foundNakedSet);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("419728563856931247732546189693287415247615938581394672374152896928463751165879324");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveNakedQuadInBox() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_naked.php#n4
    ISquareSudokuGrid nakedQuad = new StandardSudokuGrid(new int[][] {
            {5, 3, 2, 7, 8, 6, 0, 0, 0},
            {9, 7, 8, 2, 4, 1, 0, 6, 0},
            {0, 0, 1, 9, 5, 3, 2, 8, 7},
            {0, 2, 5, 4, 0, 0, 6, 7, 0},
            {0, 0, 3, 6, 1, 7, 0, 5, 2},
            {7, 0, 0, 5, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 8, 0, 5, 1, 0, 6},
            {0, 0, 0, 3, 0, 0, 0, 9, 8}
    });

    ConstraintBasedSolver solver = new ConstraintBasedSolver(nakedQuad);
    boolean foundNakedSet = solver.checkForNakedSet(nakedQuad, nakedQuad.getBoxElements(6, 2));
    assertTrue(foundNakedSet);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("532786419978241365641953287125498673483617952769532841856129734394875126217364598");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveXWingInRows() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_fishb.php#bf2
    ISquareSudokuGrid xWing =
        new StandardSudokuGrid(".41729.3.769..34.2.3264.7194.39..17.6.7..49.319537..24214567398376.9.541958431267");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(xWing);
    boolean foundXWing = solver.checkForXWingInRows(xWing, 5);
    assertTrue(foundXWing);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("841729635769153482532648719423985176687214953195376824214567398376892541958431267");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveXWingInColumns() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_fishb.php#bf2
    ISquareSudokuGrid xWing =
            new StandardSudokuGrid("98..62753.65..3...327.5...679..3.5...5...9...832.45..9673591428249.87..5518.2...7");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(xWing);
    boolean foundXWing = solver.checkForXWingInColumns(xWing, 1);
    assertTrue(foundXWing);

    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("981462753465713892327958146794236581156879234832145679673591428249687315518324967");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveSwordfishInRows() throws Exception {
    // source: HoDoKu solving techniques: http://hodoku.sourceforge.net/en/tech_fishb.php#bf3
    ISquareSudokuGrid swordfish =
            new StandardSudokuGrid("16.543.7..786.14354358.76.172.458.696..912.57...376..4.16.3..4.3...8..16..71645.3");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("");

    ConstraintBasedSolver solver = new ConstraintBasedSolver(swordfish);
    boolean foundSwordfish = solver.checkForBasicFishInRows(swordfish, 2, 3);
    assertTrue(foundSwordfish);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    System.out.println(solved.gridToString());
    // TODO
//    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveSkyscraper() throws Exception {
    ISquareSudokuGrid skyscraper =
        new StandardSudokuGrid(".76.9..2.2..7.........4...3193....4..274138...4....1329...8.........4.85.8..2.31.");

    ISquareSudokuSolver solver = new ConstraintBasedSolver(skyscraper);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("376598421214736598859241763193852647627413859548679132961385274732164985485927316");

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveBacktrackingAdversarial() throws Exception {
    // source: https://www.flickr.com/photos/npcomplete/2361922699
    // and also the Wikipedia article on Sudoku solving techniques: https://en.wikipedia.org/wiki/Sudoku_solving_algorithms#Backtracking
    ISquareSudokuGrid adversarial =
            new StandardSudokuGrid("..............3.85..1.2.......5.7.....4...1...9.......5......73..2.1........4...9");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("987654321246173985351928746128537694634892157795461832519286473472319568863745219");
    ISquareSudokuSolver solver = new ConstraintBasedSolver(adversarial);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveDailyGoodSudokuExpert() throws Exception {
    // source: daily Sudoku puzzle (May 21, 2021) on the Good Sudoku iOS app
    ISquareSudokuGrid puzzle =
            new StandardSudokuGrid("..3....8....2...6...65...7317..4...8...1.9...5..73..1432...65...8...4....5.......");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("293467185715283469846591273179645328438129657562738914324876591681954732957312846");
    ISquareSudokuSolver solver = new ConstraintBasedSolver(puzzle);
    ISquareSudokuGrid solved = solver.solve();

    assertTrue(solved.isSolved());
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void findMultipleSolutions() throws Exception {
    ISquareSudokuGrid improperPuzzle =
            new StandardSudokuGrid("2957438614318659..8761925433874592166123874955492167387635241899286713541549386..");

    ISquareSudokuSolver solver = new BruteForceSolver(improperPuzzle);
    List<ISquareSudokuGrid> solutions = solver.findAllSolutions();
    ISquareSudokuGrid solution1 =
            new StandardSudokuGrid("295743861431865927876192543387459216612387495549216738763524189928671354154938672");
    ISquareSudokuGrid solution2 =
            new StandardSudokuGrid("295743861431865972876192543387459216612387495549216738763524189928671354154938627");

    assertTrue(solutions.contains(solution1));
    assertTrue(solutions.contains(solution2));
    assertEquals(2, solutions.size());
    assertTrue(solutions.get(0).isSolved());
    assertTrue(solutions.get(1).isSolved());
  }
}