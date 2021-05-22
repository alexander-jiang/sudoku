package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.BruteForceSolver;
import main.solver.ISquareSudokuSolver;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BruteForceSolverTest {
  @Test
  public void solveClaimingLockedCandidates() throws Exception {
    ISquareSudokuGrid pointingLockedCandidate =
            new StandardSudokuGrid("318..54.6...6.381...6.8.5.3864952137123476958795318264.3.5..78......73.5....39641");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("318295476957643812246781593864952137123476958795318264631524789489167325572839641");

    ISquareSudokuSolver solver = new BruteForceSolver(pointingLockedCandidate);
    ISquareSudokuGrid solved = solver.solve();
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveClaimingLockedCandidatesIterative() throws Exception {
    ISquareSudokuGrid pointingLockedCandidate =
            new StandardSudokuGrid("318..54.6...6.381...6.8.5.3864952137123476958795318264.3.5..78......73.5....39641");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("318295476957643812246781593864952137123476958795318264631524789489167325572839641");

    ISquareSudokuSolver solver = new BruteForceSolver(pointingLockedCandidate);
    ISquareSudokuGrid solvedIterative = ((BruteForceSolver) solver).solveIterative();
    assertEquals(solution.gridToString(), solvedIterative.gridToString());

//    List<ISquareSudokuGrid> solutions = solver.findAllSolutions();
//    assertTrue(solutions.contains(solution));
//    assertEquals(1, solutions.size());
  }

  @Test
  public void testSolveSkyscraper() throws Exception {
    ISquareSudokuGrid skyscraper =
            new StandardSudokuGrid(".76.9..2.2..7.........4...3193....4..274138...4....1329...8.........4.85.8..2.31.");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("376598421214736598859241763193852647627413859548679132961385274732164985485927316");

    ISquareSudokuSolver solver = new BruteForceSolver(skyscraper);
    ISquareSudokuGrid solved = solver.solve();
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void testSolveSkyscraperIterative() throws Exception {
    ISquareSudokuGrid skyscraper =
            new StandardSudokuGrid(".76.9..2.2..7.........4...3193....4..274138...4....1329...8.........4.85.8..2.31.");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("376598421214736598859241763193852647627413859548679132961385274732164985485927316");

    ISquareSudokuSolver solver = new BruteForceSolver(skyscraper);
    // TODO print out the steps of the recursive solver and compare to the iterative solver, why does the iterative solver not make progress?
    ISquareSudokuGrid solvedIterative = ((BruteForceSolver) solver).solveIterative();
    assertEquals(solution.gridToString(), solvedIterative.gridToString());

//    List<ISquareSudokuGrid> solutions = solver.findAllSolutions();
//    assertTrue(solutions.contains(solution));
//    assertEquals(1, solutions.size());
  }

  @Test
  public void solveXWing() throws Exception {
    ISquareSudokuGrid xWing =
        new StandardSudokuGrid(".41729.3.769..34.2.3264.7194.39..17.6.7..49.319537..24214567398376.9.541958431267");
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("841729635769153482532648719423985176687214953195376824214567398376892541958431267");

    ISquareSudokuSolver solver = new BruteForceSolver(xWing);
    ISquareSudokuGrid solved = solver.solve();
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveXWingIterative() throws Exception {
    ISquareSudokuGrid solution =
            new StandardSudokuGrid("841729635769153482532648719423985176687214953195376824214567398376892541958431267");
    ISquareSudokuGrid xWing =
            new StandardSudokuGrid(".41729.3.769..34.2.3264.7194.39..17.6.7..49.319537..24214567398376.9.541958431267");

    ISquareSudokuSolver solver = new BruteForceSolver(xWing);
    ISquareSudokuGrid solvedIterative = ((BruteForceSolver) solver).solveIterative();
    assertEquals(solution.gridToString(), solvedIterative.gridToString());

//    List<ISquareSudokuGrid> solutions = solver.findAllSolutions();
//    assertTrue(solutions.contains(solution));
//    assertEquals(1, solutions.size());
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
  }
}