package main.solver;

import main.grid.model.ISquareSudokuGrid;
import main.util.DisplayStrings;
import main.util.Pair;
import main.util.SubsetHelper;

import java.util.*;

/**
 * A constraint-based solver that attempts to narrow down the candidate values
 * for each empty square based on the constraints of the Sudoku grid. The solver
 * uses logical inferences to determine when candidate values would violate constraints.
 */
public class ConstraintBasedSolver implements ISquareSudokuSolver {

  private ISquareSudokuGrid grid;

  public ConstraintBasedSolver(ISquareSudokuGrid grid) {
    this.grid = grid;
  }

  @Override
  public ISquareSudokuGrid getGrid() {
    return grid;
  }

  @Override
  public ISquareSudokuGrid step() {
    return null; //TODO
  }

  @Override
  public ISquareSudokuGrid solve() {
    // Check for elements with only one candidate value (naked single). These elements should have
    // their value set to the single candidate value.
    boolean updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (grid.getCandidateValues(r, c).size() == 1 && !grid.isFixed(r, c)) {
          int nakedSingle = (Integer) (grid.getCandidateValues(r, c).toArray()[0]);
          System.out.println("Found naked single in element (" + r + ", " + c + "): " + nakedSingle);
          grid.setValue(r, c, nakedSingle);
//          System.out.println(grid.gridToString());
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart to scan from the beginning
    }

    // Check for elements which are the only element in the group (row, column, or box)
    // that contain a certain value as a candidate (hidden single).
    updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (!grid.isFixed(r, c)) {
          // TODO use enum to distinguish between "failed, no constraint violated", "failed, constraint violated" and "success"
          // Check against the row, column, and box (stop as soon as one of them returns true).
          boolean elementUpdated = checkForHiddenSingle(grid, r, c, grid.getRowElements(r, c)) ||
              checkForHiddenSingle(grid, r, c, grid.getColumnElements(r, c)) ||
              checkForHiddenSingle(grid, r, c, grid.getBoxElements(r, c));

          updated = updated || elementUpdated;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // Check if the candidates for a value in a box are restricted to a specific column or row.
    // If so, that value can't be a candidate anywhere else in that column or row.
    updated = false;
    for (int r = 0; r < grid.getDimension(); r += Math.sqrt(grid.getDimension())) {
      for (int c = 0; c < grid.getDimension(); c += Math.sqrt(grid.getDimension())) {
        for (int value = 1; value <= grid.getDimension(); value++) {
          // Separate the checks to avoid the short-circuit caused by using the || operator.
          if (checkForRowLockedCandidate(grid, value, grid.getBoxElements(r, c))) {
            updated = true;
          }

          if (checkForColumnLockedCandidate(grid, value, grid.getBoxElements(r, c))) {
            updated = true;
          }
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // Check if the candidates for a value in a column or row are restricted to a single box.
    // If so, that value can't be a candidate anywhere else in that box.
    updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int value = 1; value <= grid.getDimension(); value++) {
        if (checkForBoxLockingCandidate(grid, value, grid.getRowElements(r, 0))) {
          updated = true;
        }
      }
    }
    for (int c = 0; c < grid.getDimension(); c++) {
      for (int value = 1; value <= grid.getDimension(); value++) {
        if (checkForBoxLockingCandidate(grid, value, grid.getColumnElements(0, c))) {
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // Check for a set of m elements in a group that are the only m elements in the group
    // that contain a set of m values as candidates. In those elements, any value that
    // isn't one of the m shared values is not a candidate (as then there would be at most m-1
    // elements that remain to store the m shared values).
    updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      if (checkForHiddenSet(grid, grid.getRowElements(r, 0))) {
        updated = true;
      }
    }
    for (int c = 0; c < grid.getDimension(); c++) {
      if (checkForHiddenSet(grid, grid.getColumnElements(0, c))) {
        updated = true;
      }
    }
    for (int r = 0; r < grid.getDimension(); r += Math.sqrt(grid.getDimension())) {
      for (int c = 0; c < grid.getDimension(); c += Math.sqrt(grid.getDimension())) {
        if (checkForHiddenSet(grid, grid.getBoxElements(r, c))) {
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // Check for a set of m elements in a group that contain only m candidates (each element must contain at least
    // 2 candidates, but does not necessarily need to contain all m candidates). In that group, those m candidate
    // values are only candidates in those m elements.
    updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      if (checkForNakedSet(grid, grid.getRowElements(r, 0))) {
        updated = true;
      }
    }
    for (int c = 0; c < grid.getDimension(); c++) {
      if (checkForNakedSet(grid, grid.getColumnElements(0, c))) {
        updated = true;
      }
    }
    for (int r = 0; r < grid.getDimension(); r += Math.sqrt(grid.getDimension())) {
      for (int c = 0; c < grid.getDimension(); c += Math.sqrt(grid.getDimension())) {
        if (checkForNakedSet(grid, grid.getBoxElements(r, c))) {
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // Check for two rows/columns (base sets) such that a given digit only appears in those rows/columns
    // in two columns/rows (cover sets). Then, in those cover sets, the digit must be placed
    // into the base sets (i.e. can be eliminated from other cells)
    updated = false;
    for (int fishSize = 2; fishSize <= 4; fishSize++) {
      for (int value = 1; value <= grid.getDimension(); value++) {
        if (checkForBasicFishInRows(grid, value, fishSize)) {
          updated = true;
        }
        if (checkForBasicFishInColumns(grid, value, fishSize)) {
          updated = true;
        }
      }
    }
    if (updated) {
      System.out.println("Restarting scan...");
      return solve(); // Restart
    }

    // TODO is the idea to make the Sudoku grid functional by having the solver return a grid??
    // TODO And then this solve method could be recursive?
    System.out.println("Finished!");
    System.out.println(grid.gridToString());
    System.out.println("Elements and their candidate values:");
    for (int r = 0; r < grid.getDimension(); r++) {
      for (int c = 0; c < grid.getDimension(); c++) {
        if (!grid.isFixed(r, c)) {
          System.out.println("Candidates for element (" + r + ", " + c + "): " +
                  DisplayStrings.setToString(grid.getCandidateValues(r, c)));
        }
      }
    }

    if (!grid.isSolved()) {
      BruteForceSolver bruteForceSolver = new BruteForceSolver(grid);
      return bruteForceSolver.solveIterative();
    }
    return grid;
  }

  @Override
  public List<ISquareSudokuGrid> findAllSolutions() {
    ISquareSudokuGrid firstAttempt = this.solve();

    if (firstAttempt.isSolved()) {
      List<ISquareSudokuGrid> solutions = new ArrayList<>();
      solutions.add(firstAttempt);
      return solutions;
    }

    BruteForceSolver bruteForceSolver = new BruteForceSolver(firstAttempt);
    return bruteForceSolver.findAllSolutions();
  }

  public boolean checkForHiddenSingle(ISquareSudokuGrid grid, int r, int c, List<Pair<Integer, Integer>> groupCoordinates) {
    Set<Integer> candidates = grid.getCandidateValues(r, c);
    for (Pair<Integer, Integer> coords : groupCoordinates) {
      // Don't compare with self.
      if (!(coords.first() == r && coords.second() == c)) {
        candidates.removeAll(grid.getCandidateValues(coords.first(), coords.second()));
      }

      // Early exit: each of this element's candidate values can be found in some other element in the group
      if (candidates.isEmpty()) {
        return false;
      }
    }
    if (candidates.size() > 1) {
      System.out.println("Constraint violated! These values appear only once in element (" + r + ", " + c + "): " +
          DisplayStrings.setToString(candidates));
      return false;
    } else if (candidates.size() == 1) {
      int hiddenSingle = (Integer) (candidates.toArray()[0]);
      System.out.println("Found hidden single in element (" + r + ", " + c + "): " + hiddenSingle);
//      System.out.println("Group elements and their candidate values:");
//      for (Pair<Integer, Integer> coords : groupCoordinates) {
//        System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//            DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//      }
      grid.setValue(r, c, hiddenSingle);
//      System.out.println(grid.gridToString());
      return true;
    } else {
      return false;
    }
  }

  public boolean checkForRowLockedCandidate(ISquareSudokuGrid grid,
                                             int value,
                                             List<Pair<Integer, Integer>> boxElements) {
    boolean inARow = false;
    int lockedRow = -1;
    Pair<Integer, Integer> boxCoordinates = new Pair<>(-1, -1);
    for (Pair<Integer, Integer> coord : boxElements) {
      // If this value is already fixed in this box, this check is invalid.
      if (grid.isFixed(coord.first(), coord.second()) && grid.getValue(coord.first(), coord.second()) == value) {
        return false;
      }

      // Check if the value is a candidate in this row (ignoring multiple occurrences in the same row).
      if (grid.isACandidate(coord.first(), coord.second(), value) && lockedRow != coord.first()) {
        if (!inARow) {
          inARow = true;
          lockedRow = coord.first();
        } else {
          return false; // Found in more than one row.
        }
      }
      boxCoordinates = grid.getBoxCoordinates(coord.first(), coord.second());
    }

    if (lockedRow < 0) {
      // TODO I think this means a constraint is violated:
      // the digit is not a fixed value in the box, and
      // the digit is not a candidate in the box
      return false;
    }

    // If control reaches here, means that this value is restricted to a single row in this box.
    System.out.println("Found locked candidate in box (" + boxCoordinates.first() + ", " + boxCoordinates.second() +
        "), row = " + lockedRow + ": " + value);
//    System.out.println("Box elements and their candidate values:");
//    for (Pair<Integer, Integer> coords : boxElements) {
//      System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//          DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//    }
    boolean updated = false;
    for (int c = 0; c < grid.getDimension(); c++) {
      // Don't constrain the elements in the same box.
      if (!grid.getBoxCoordinates(lockedRow, c).equals(boxCoordinates)) {
        if (grid.isACandidate(lockedRow, c, value)) {
          grid.setCandidate(lockedRow, c, value, false);
          updated = true;
          System.out.println("Removed " + value + " as a candidate from element (" + lockedRow + ", " + c + ")");
        }
      }
    }
    return updated;
  }

  public boolean checkForColumnLockedCandidate(ISquareSudokuGrid grid,
                                                int value,
                                                List<Pair<Integer, Integer>> boxElements) {
    boolean inAColumn = false;
    int lockedColumn = -1;
    Pair<Integer, Integer> boxCoordinates = new Pair<>(-1, -1);
    for (Pair<Integer, Integer> coord : boxElements) {
      // If this value is already fixed in this box, this check is invalid.
      if (grid.isFixed(coord.first(), coord.second()) && grid.getValue(coord.first(), coord.second()) == value) {
        return false;
      }

      // Check if the value is a candidate in this row (ignoring multiple occurrences in the same row).
      if (grid.isACandidate(coord.first(), coord.second(), value) && lockedColumn != coord.second()) {
        if (!inAColumn) {
          inAColumn = true;
          lockedColumn = coord.second();
        } else {
          return false; // Found in more than one column.
        }
      }
      boxCoordinates = grid.getBoxCoordinates(coord.first(), coord.second());
    }

    // If control reaches here, means that this value is restricted to a single row in this box.
    System.out.println("Found locked candidate in box (" + boxCoordinates.first() + ", " + boxCoordinates.second() +
        "), column = " + lockedColumn + ": " + value);
//    System.out.println("Box elements and their candidate values:");
//    for (Pair<Integer, Integer> coords : boxElements) {
//      System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//          DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//    }

    // But did we make any progress (i.e. removing a candidate value)?
    boolean updated = false;
    for (int r = 0; r < grid.getDimension(); r++) {
      // Don't constrain the elements in the same box.
      if (!grid.getBoxCoordinates(r, lockedColumn).equals(boxCoordinates)) {
        if (grid.isACandidate(r, lockedColumn, value)) {
          grid.setCandidate(r, lockedColumn, value, false);
          updated = true;
          System.out.println("Removed " + value + " as a candidate from element (" + r + ", " + lockedColumn + ")");
        }
      }
    }
    return updated;
  }

  public boolean checkForBoxLockingCandidate(ISquareSudokuGrid grid, int value, List<Pair<Integer, Integer>> groupElements) {
    boolean foundInBox = false;
    Pair<Integer, Integer> boxCoordinates = new Pair<>(-1, -1);
    for (Pair<Integer, Integer> coord : groupElements) {
      // If grid has already assigned an element to this value (i.e. the value isn't a candidate), no need to check.
      if (grid.getValue(coord.first(), coord.second()) == value) {
        return false;
      }

      if (grid.isACandidate(coord.first(), coord.second(), value)) {
        if (!foundInBox) {
          foundInBox = true;
          boxCoordinates = grid.getBoxCoordinates(coord.first(), coord.second());
        } else if (!grid.getBoxCoordinates(coord.first(), coord.second()).equals(boxCoordinates)) {
          return false;
        }
      }
    }
    // If control reaches here, means that this value is restricted to a single box in this row or column.
    System.out.println("Found box-locking candidate in box (" + boxCoordinates.first() + ", " + boxCoordinates.second() +
        "): " + value);
//    System.out.println("Group elements and their candidate values:");
//    for (Pair<Integer, Integer> coords : groupElements) {
//      System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//          DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//    }

    // But did we make any progress (i.e. removing a candidate value)?
    boolean updated = false;
    for (Pair<Integer, Integer> coord : grid.getBoxElementsByCoordinates(boxCoordinates)) {
      // Don't constrain the elements in the same row or column.
      if (!groupElements.contains(coord)) {
        if (grid.isACandidate(coord.first(), coord.second(), value)) {
          grid.setCandidate(coord.first(), coord.second(), value, false);
          updated = true;
          System.out.println("Removed " + value + " as a candidate from element (" + coord.first() + ", " + coord.second() + ")");
        }
      }
    }
    return updated;
  }

  public boolean checkForHiddenSet(ISquareSudokuGrid grid, List<Pair<Integer, Integer>> groupElements) {
    // how many cells/values in the group are not fixed?
    int groupCandidateCount = 0;

    // Count the occurrences of each candidate in the group.
    Map<Integer, List<Pair<Integer, Integer>>> candidateCoords = new TreeMap<>();
    for (Pair<Integer, Integer> coord : groupElements) {
      if (!grid.isFixed(coord.first(), coord.second())) {
        groupCandidateCount++;
        for (int candidate : grid.getCandidateValues(coord.first(), coord.second())) {
          if (!candidateCoords.containsKey(candidate)) {
            candidateCoords.put(candidate, new ArrayList<>());
          }
          candidateCoords.get(candidate).add(coord);
        }
      }
    }

    for (int numCandidates = 2; numCandidates < groupCandidateCount; numCandidates++) {
      // Search for a hidden n-subset where n is the number of candidates in the hidden subset.
      List<Integer> eligibleValues = new ArrayList<>();
      for (Integer value : candidateCoords.keySet()) {
        // Values that are candidates in more than n cells can't be used to form a hidden n-subset.
        // Note that values that are fixed (i.e. are not candidates in any cells) cannot form a hidden subset either.
        if (candidateCoords.get(value).size() <= numCandidates && candidateCoords.get(value).size() > 0) {
          eligibleValues.add(value);
        }
      }

      // Try all possible subsets of size n.
      List<List<Integer>> valueSubsets = SubsetHelper.listAllSubsetsOfSize(eligibleValues, numCandidates);

      for (List<Integer> hiddenSubsetValues : valueSubsets) {
        List<Pair<Integer, Integer>> hiddenSubsetCoords = new ArrayList<>();
        for (Integer value : hiddenSubsetValues) {
          for (Pair<Integer, Integer> candidateCoord : candidateCoords.get(value)) {
            if (!hiddenSubsetCoords.contains(candidateCoord)) {
              hiddenSubsetCoords.add(candidateCoord);
            }
          }
        }

        if (hiddenSubsetCoords.size() == numCandidates) {
          // Found a hidden n-subset.
          System.out.println("Found a hidden subset! Candidate values: " + hiddenSubsetValues);
//          System.out.println("Element coordinates: " + hiddenSubsetCoords);
//          System.out.println("Group elements and their candidate values:");
//          for (Pair<Integer, Integer> coords : groupElements) {
//            System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//                DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//          }

          // But did we make any progress (i.e. removing a candidate value)?
          boolean updated = false;
          for (Pair<Integer, Integer> coord : hiddenSubsetCoords) {
            Set<Integer> cellCandidates = grid.getCandidateValues(coord.first(), coord.second());

            // for cells within the hidden subset, remove any candidates are not in the set of values
            for (int cellCandidate : cellCandidates) {
              if (!hiddenSubsetValues.contains(cellCandidate)) {
                grid.setCandidate(coord.first(), coord.second(), cellCandidate, false);
                updated = true;
                System.out.printf("Removed %d as a candidate from element (%d, %d)%n",
                        cellCandidate, coord.first(), coord.second());
              }
            }
          }
          if (updated) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean checkForNakedSet(ISquareSudokuGrid grid, List<Pair<Integer, Integer>> groupElements) {
    // how many cells/values in the group are not fixed?
    int groupCandidateCount = 0;
    for (Pair<Integer, Integer> coord : groupElements) {
      if (!grid.isFixed(coord.first(), coord.second())) {
        groupCandidateCount++;
      }
    }

    for (int numCandidates = 2; numCandidates < groupCandidateCount; numCandidates++) {
      // Search for a naked n-subset where n is the number of candidates in the naked subset.
      List<Pair<Integer, Integer>> eligibleCoords = new ArrayList<>();
      for (Pair<Integer, Integer> coords : groupElements) {
        // Cells with more than n candidate values can't be used to form a naked n-subset.
        if (!grid.isFixed(coords.first(), coords.second()) &&
                grid.getCandidateValues(coords.first(), coords.second()).size() <= numCandidates) {
          eligibleCoords.add(coords);
        }
      }

      // Try all possible subsets of size n.
      List<List<Pair<Integer, Integer>>> coordSubsets = SubsetHelper.listAllSubsetsOfSize(eligibleCoords, numCandidates);
      for (List<Pair<Integer, Integer>> nakedSubsetCoords : coordSubsets) {
        Set<Integer> nakedSubset = new TreeSet<>();
        for (Pair<Integer, Integer> subsetCoord : nakedSubsetCoords) {
          nakedSubset.addAll(grid.getCandidateValues(subsetCoord.first(), subsetCoord.second()));
        }

        if (nakedSubset.size() == numCandidates) {
          // Found a naked n-subset.
          System.out.println("Found a naked subset! Candidate values: " + DisplayStrings.setToString(nakedSubset));
//            System.out.println("Element coordinates: " + nakedSubsetCoords);
//            System.out.println("Group elements and their candidate values:");
//            for (Pair<Integer, Integer> coords : groupElements) {
//              System.out.println("Candidates for element (" + coords.first() + ", " + coords.second() + "): " +
//                  DisplayStrings.setToString(grid.getCandidateValues(coords.first(), coords.second())));
//            }

          // TODO check if the naked subset elements are all in multiple groups (e.g. all in the same row and in the same box)
          // But did we make any progress (i.e. removing a candidate value)?
          boolean updated = false;
          for (Pair<Integer, Integer> coord : groupElements) {
            if (!grid.isFixed(coord.first(), coord.second()) && !nakedSubsetCoords.contains(coord)) {
              Set<Integer> cellCandidates = grid.getCandidateValues(coord.first(), coord.second());

              // for cells outside the naked subset, remove any candidates that are in the set of values
              for (int nakedSubsetValue : nakedSubset) {
                if (cellCandidates.contains(nakedSubsetValue)) {
                  grid.setCandidate(coord.first(), coord.second(), nakedSubsetValue, false);
                  updated = true;
                  System.out.printf("Removed %d as a candidate from element (%d, %d)%n",
                          nakedSubsetValue, coord.first(), coord.second());
                }
              }
            }
          }
          if (updated) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean checkForBasicFishInRows(ISquareSudokuGrid grid, Integer focusValue, int fishSize) {
    // try to find N rows so that, across all rows, there are only N columns that contain candidates for the digit
    Map<Integer, Set<Integer>> rowCandidateColumns = new HashMap<>();

    // only rows with the candidate in at most N columns are eligible to be a part of the basic fish
    List<Integer> eligibleRows = new ArrayList<>();

    for (int r = 0; r < grid.getDimension(); r++) {
      Set<Integer> candidateColumns = new HashSet<>();
      for (int c = 0; c < grid.getDimension(); c++) {
        if (grid.isFixed(r, c) && grid.getValue(r, c) == focusValue) {
          // this digit is already fixed in this row, skip
          candidateColumns = new HashSet<>();
          break;
        }

        if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
          candidateColumns.add(c);
        }
      }
      rowCandidateColumns.put(r, candidateColumns);
      if (candidateColumns.size() <= fishSize && candidateColumns.size() > 0) {
        eligibleRows.add(r);
      }
    }

    // need to find a subset of N of the rows such that:
    // - size of the union of columns = N (i.e. across each of the N rows, the digit only appears as
    //   a candidate in N columns)
    boolean updated = false;
    List<List<Integer>> rowSubsets = SubsetHelper.listAllSubsetsOfSize(eligibleRows, fishSize);
    for (List<Integer> rowSubset : rowSubsets) {
      Set<Integer> columnsUnion = new HashSet<>();
      for (Integer rowIndex : rowSubset) {
        columnsUnion.addAll(rowCandidateColumns.get(rowIndex));
      }

      if (columnsUnion.size() == fishSize) {
        // found a basic fish
        System.out.printf("Found basic fish, size = %d! Value: %d in rows %s is locked to columns %s%n", fishSize, focusValue, rowSubset, DisplayStrings.setToString(columnsUnion));

        // eliminate all candidates in columns (the cover sets)
        // that are not present in any of the rows (the base sets)
        List<Pair<Integer, Integer>> elementCoords = new ArrayList<>();
        for (Integer colIdx : columnsUnion) {
          elementCoords.addAll(grid.getColumnElements(0, colIdx));
        }

        for (Pair<Integer, Integer> coord : elementCoords) {
          int r = coord.first(), c = coord.second();
          if (rowSubset.contains(r)) {
            continue;
          }
          if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
            grid.setCandidate(r, c, focusValue, false);
            System.out.printf("Removed candidate %d from (%d, %d)%n", focusValue, r, c);
            updated = true;
          }
        }
      }
    }

    return updated;
  }

  public boolean checkForBasicFishInColumns(ISquareSudokuGrid grid, Integer focusValue, int fishSize) {
    // try to find N columns so that, across all columns, there are only N rows that contain candidates for the digit
    Map<Integer, Set<Integer>> columnCandidateRows = new HashMap<>();

    // only rows with the candidate in at most N columns are eligible to be a part of the basic fish
    List<Integer> eligibleColumns = new ArrayList<>();

    for (int c = 0; c < grid.getDimension(); c++) {
      Set<Integer> candidateRows = new HashSet<>();
      for (int r = 0; r < grid.getDimension(); r++) {
        if (grid.isFixed(r, c) && grid.getValue(r, c) == focusValue) {
          // this digit is already fixed in this column, skip
          candidateRows = new HashSet<>();
          break;
        }

        if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
          candidateRows.add(r);
        }
      }
      columnCandidateRows.put(c, candidateRows);
      if (candidateRows.size() <= fishSize && candidateRows.size() > 0) {
        eligibleColumns.add(c);
      }
    }

    // need to find a subset of N of the columns such that:
    // - size of the union of rows = N (i.e. across each of the N columns, the digit only appears as
    //   a candidate in N rows)
    boolean updated = false;
    List<List<Integer>> columnSubsets = SubsetHelper.listAllSubsetsOfSize(eligibleColumns, fishSize);
    for (List<Integer> columnSubset : columnSubsets) {
      Set<Integer> rowsUnion = new HashSet<>();
      for (Integer colIndex : columnSubset) {
        rowsUnion.addAll(columnCandidateRows.get(colIndex));
      }

      if (rowsUnion.size() == fishSize) {
        // found a basic fish
        System.out.printf("Found basic fish, size = %d! Value: %d in columns %s is locked to rows %s%n", fishSize, focusValue, columnSubset, DisplayStrings.setToString(rowsUnion));

        // eliminate all candidates in columns (the cover sets)
        // that are not present in any of the rows (the base sets)
        List<Pair<Integer, Integer>> elementCoords = new ArrayList<>();
        for (Integer rowIdx : rowsUnion) {
          elementCoords.addAll(grid.getRowElements(rowIdx, 0));
        }

        for (Pair<Integer, Integer> coord : elementCoords) {
          int r = coord.first(), c = coord.second();
          if (columnSubset.contains(c)) {
            continue;
          }
          if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
            grid.setCandidate(r, c, focusValue, false);
            System.out.printf("Removed candidate %d from (%d, %d)%n", focusValue, r, c);
            updated = true;
          }
        }
      }
    }

    return updated;
  }


//  public boolean checkForXWingInRows(ISquareSudokuGrid grid, Integer focusValue) {
//    // try to find two rows so that the only candidates for that digit in those rows are
//    // in the same columns
//    List<Set<Integer>> rowCandidateColumns = new ArrayList<>();
//
//    for (int r = 0; r < grid.getDimension(); r++) {
//      Set<Integer> candidateColumns = new HashSet<>();
//      for (int c = 0; c < grid.getDimension(); c++) {
//        if (grid.isFixed(r, c) && grid.getValue(r, c) == focusValue) {
//          // this digit is already fixed in this column, skip
//          candidateColumns = new HashSet<>();
//          break;
//        }
//
//        if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
//          candidateColumns.add(c);
//        }
//      }
//      rowCandidateColumns.add(candidateColumns);
//    }
//
//    boolean updated = false;
//    for (int r1 = 0; r1 < grid.getDimension(); r1++) {
//      if (rowCandidateColumns.get(r1).size() == 2) {
//        for (int r2 = r1 + 1; r2 < grid.getDimension(); r2++) {
//          if (rowCandidateColumns.get(r2).size() == 2 && rowCandidateColumns.get(r1).equals(rowCandidateColumns.get(r2))) {
//            // found an X-wing, eliminate the value as a candidate from cells in columns specified by
//            // rowCandidateColumns.get(r1) and that are not in rows r1 or r2
//            Integer[] dummyArray = new Integer[]{};
//            Integer[] columns = rowCandidateColumns.get(r1).toArray(dummyArray);
//            int c1 = columns[0], c2 = columns[1];
//            System.out.printf("Found X-wing! Value: %d in rows %d & %d is locked to columns %d & %d%n", focusValue, r1, r2, c1, c2);
//            List<Pair<Integer, Integer>> elementCoords = grid.getColumnElements(r1, c1);
//            elementCoords.addAll(grid.getColumnElements(r1, c2));
//
//            for (Pair<Integer, Integer> coord : elementCoords) {
//              int r = coord.first(), c = coord.second();
//              if (r == r1 || r == r2) {
//                continue;
//              }
//              if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
//                grid.setCandidate(r, c, focusValue, false);
//                updated = true;
//              }
//            }
//          }
//        }
//      }
//    }
//    return updated;
//  }
//
//  public boolean checkForXWingInColumns(ISquareSudokuGrid grid, Integer focusValue) {
//    // try to find two columns so that the only candidates for that digit in those cols are
//    // in the same rows
//    List<Set<Integer>> columnCandidateRows = new ArrayList<>();
//
//    for (int c = 0; c < grid.getDimension(); c++) {
//      Set<Integer> candidateRows = new HashSet<>();
//      for (int r = 0; r < grid.getDimension(); r++) {
//        if (grid.isFixed(r, c) && grid.getValue(r, c) == focusValue) {
//          // this digit is already fixed in this column, skip
//          candidateRows = new HashSet<>();
//          break;
//        }
//
//        if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
//          candidateRows.add(r);
//        }
//      }
//      columnCandidateRows.add(candidateRows);
//    }
//
//    boolean updated = false;
//    for (int c1 = 0; c1 < grid.getDimension(); c1++) {
//      if (columnCandidateRows.get(c1).size() == 2) {
//        for (int c2 = c1 + 1; c2 < grid.getDimension(); c2++) {
//          if (columnCandidateRows.get(c2).size() == 2 && columnCandidateRows.get(c1).equals(columnCandidateRows.get(c2))) {
//            // found an X-wing, eliminate value as a candidate from cells in rows specified by
//            // columnCandidateRows.get(c1) and that are not in columns c1 or c2
//            Integer[] dummyArray = new Integer[]{};
//            Integer[] rows = columnCandidateRows.get(c1).toArray(dummyArray);
//            int r1 = rows[0], r2 = rows[1];
//            System.out.printf("Found X-wing! Value: %d in columns %d & %d is locked to rows %d & %d%n", focusValue, c1, c2, r1, r2);
//            List<Pair<Integer, Integer>> elementCoords = grid.getRowElements(r1, c1);
//            elementCoords.addAll(grid.getRowElements(r2, c1));
//
//            for (Pair<Integer, Integer> coord : elementCoords) {
//              int r = coord.first(), c = coord.second();
//              if (c == c1 || c == c2) {
//                continue;
//              }
//              if (!grid.isFixed(r, c) && grid.getCandidateValues(r, c).contains(focusValue)) {
//                grid.setCandidate(r, c, focusValue, false);
//                updated = true;
//              }
//            }
//          }
//        }
//      }
//    }
//    return updated;
//  }
}
