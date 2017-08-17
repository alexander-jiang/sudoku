package test.solver;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.solver.BruteForceSolver;
import main.solver.ISquareSudokuSolver;
import org.junit.Test;

import static org.junit.Assert.*;

public class BruteForceSolverTest {
  @Test
  public void testSolveSkyscraper() throws Exception {
    ISquareSudokuGrid skyscraper =
        new StandardSudokuGrid(".76.9..2.2..7.........4...3193....4..274138...4....1329...8.........4.85.8..2.31.");

    ISquareSudokuSolver solver = new BruteForceSolver(skyscraper);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("376598421214736598859241763193852647627413859548679132961385274732164985485927316");
    assertEquals(solution.gridToString(), solved.gridToString());
  }

  @Test
  public void solveXWing() throws Exception {
    ISquareSudokuGrid xWing =
        new StandardSudokuGrid(".41729.3.769..34.2.3264.7194.39..17.6.7..49.319537..24214567398376.9.541958431267");

    ISquareSudokuSolver solver = new BruteForceSolver(xWing);
    ISquareSudokuGrid solved = solver.solve();
    ISquareSudokuGrid solution =
        new StandardSudokuGrid("841729635769153482532648719423985176687214953195376824214567398376892541958431267");
    assertEquals(solution.gridToString(), solved.gridToString());
  }
}