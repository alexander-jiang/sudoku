package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.util.Pair;

import java.util.*;

/**
 * A naive, brute-force solver that attempts to place each possible value in each empty
 * square and checks the constraints of the whole grid after each attempted place. If the
 * constraints are invalidated, the last placed value is erased and replaced with another
 * value. If the constraints are not valid, the solve attempts to place a value in the next
 * empty square. This process repeats until all squares are filled and the constraints
 * are met.
 */
public class BruteForceSolver implements ISquareSudokuSolver {

  private ISquareSudokuGrid grid;

  public BruteForceSolver(ISquareSudokuGrid grid) {
    this.grid = grid;
  }

  @Override
  public ISquareSudokuGrid getGrid() {
    return grid;
  }

  @Override
  public ISquareSudokuGrid step() {
    return null;
  }

  @Override
  public ISquareSudokuGrid solve() {
    if (grid.isSolved()) {
      return grid;
    }

    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (!grid.isFixed(r, c)) {
          Set<Integer> candidates = grid.getCandidateValues(r, c);
          if (candidates.size() == 0) {
            return null; // No candidates left, no solutions.
          }
          for (int candidate : candidates) {
            // Try placing each candidate value.
            ISquareSudokuGrid gridCopy = grid.copy();
            System.out.printf("Setting element (%d, %d) to %d%n", r, c, candidate);
            gridCopy.setValue(r, c, candidate);
            System.out.println(gridCopy.gridToString());
            ISquareSudokuSolver newSolver = new BruteForceSolver(gridCopy);
            ISquareSudokuGrid solution = newSolver.solve();
            if (solution != null && solution.isSolved()) {
              return solution;
            }

            // TODO try removing the candidate and seeing if that advances the puzzle
          }
          // Tried all candidates, haven't found a solution.
          return null;
        }
      }
    }
    System.out.println("Reached the end!");
    return null;
  }

  @Override
  public List<ISquareSudokuGrid> findAllSolutions() {
    List<ISquareSudokuGrid> solutions = new ArrayList<>();
    if (grid.isSolved()) {
      solutions.add(grid);
      return solutions;
    }

    Set<ISquareSudokuGrid> visited = new HashSet<>();
    List<ISquareSudokuGrid> frontier = new LinkedList<>();
    frontier.add(grid);

    while (!frontier.isEmpty()) {
      ISquareSudokuGrid currentGrid = frontier.remove(0);
      if (!currentGrid.checkBasicConstraints()) {
        continue;
      }
      if (visited.contains(currentGrid)) {
        continue;
      }
//      System.out.println("frontier size: " + frontier.size());
      visited.add(currentGrid);
//      System.out.println("visited size: " + visited.size());
//      System.out.println(currentGrid.gridToString());

      if (currentGrid.isSolved()) {
        if (!solutions.contains(currentGrid)) {
          System.out.println("found solution!");
          solutions.add(currentGrid);
        }
        continue;
      }

      List<ISquareSudokuGrid> neighbors = new ArrayList<>();
      ISquareSudokuGrid forcedNeighbor = currentGrid.copy();
      boolean invalid = false;
      boolean forced = false;
      // generate neighbors
      for (int r = 0; r < currentGrid.getDimension(); r++) {
        for (int c = 0; c < currentGrid.getDimension(); c++) {
          if (!currentGrid.isFixed(r, c)) {
            Set<Integer> candidates = currentGrid.getCandidateValues(r, c);
            if (candidates.size() == 0) {
              invalid = true;
              continue;
            } else if (candidates.size() == 1) {
              forced = true;
              Integer[] candidatesArray = new Integer[candidates.size()];
              candidates.toArray(candidatesArray);
              forcedNeighbor.setValue(r, c, candidatesArray[0]);
            }
            for (int candidate : candidates) {
              ISquareSudokuGrid gridCopy = currentGrid.copy();
              gridCopy.setValue(r, c, candidate);
              neighbors.add(gridCopy);
            }
          }
        }
      }

      if (invalid) {
        continue;
      }
      if (forced) {
        if (!visited.contains(forcedNeighbor)) {
          // skips the candidates check when checking equality
          frontier.add(0, forcedNeighbor);
        }
        continue;
      }
      // add neighbors that haven't been visited to the frontier
      for (ISquareSudokuGrid neighbor : neighbors) {
        if (!visited.contains(neighbor)) {
          // skips the candidates check when checking equality
          frontier.add(0, neighbor);
        }
      }
    }
    return solutions;
  }
}
