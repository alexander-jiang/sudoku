package test.grid.model;

import main.grid.model.ISquareSudokuGrid;
import main.grid.model.StandardSudokuGrid;
import main.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Unit tests for the standard Sudoku grid representation.
 */
public class StandardSudokuGridTest {

  private ISquareSudokuGrid emptyGrid = new StandardSudokuGrid();

  @org.junit.Test
  public void testGetDimension() throws Exception {
    assertEquals(9, emptyGrid.getDimension());
  }

  @org.junit.Test
  public void testGetSetValue() throws Exception {
    ISquareSudokuGrid solvedGrid = new StandardSudokuGrid(new int[][] {
        {3, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    });

    assertEquals(8, solvedGrid.getValue(0, 2));
    assertEquals(1, solvedGrid.getValue(5, 8));
    assertEquals(9, solvedGrid.getValue(7, 4));
    assertEquals(2, solvedGrid.getValue(4, 7));

    ISquareSudokuGrid updatedGrid = solvedGrid.setValue(4, 7, 0);
    assertEquals(0, updatedGrid.getValue(4, 7));
    assertEquals(2, solvedGrid.getValue(4, 7));

    ISquareSudokuGrid updatedGrid2 = solvedGrid.setValue(4, 7, 3);
    assertEquals(3, updatedGrid2.getValue(4, 7));
    assertEquals(0, updatedGrid.getValue(4, 7));
    assertEquals(2, solvedGrid.getValue(4, 7));
  }

  @org.junit.Test
  public void testGetBoxCoordinates() throws Exception {
    assertEquals(new Pair<>(0, 0), emptyGrid.getBoxCoordinates(0, 0));
    assertEquals(new Pair<>(0, 0), emptyGrid.getBoxCoordinates(0, 2));
    assertEquals(new Pair<>(0, 0), emptyGrid.getBoxCoordinates(2, 0));
    assertEquals(new Pair<>(0, 0), emptyGrid.getBoxCoordinates(2, 2));

    assertEquals(new Pair<>(0, 1), emptyGrid.getBoxCoordinates(0, 3));
    assertEquals(new Pair<>(0, 1), emptyGrid.getBoxCoordinates(0, 5));
    assertEquals(new Pair<>(0, 1), emptyGrid.getBoxCoordinates(2, 3));
    assertEquals(new Pair<>(0, 1), emptyGrid.getBoxCoordinates(2, 5));

    assertEquals(new Pair<>(0, 2), emptyGrid.getBoxCoordinates(0, 6));
    assertEquals(new Pair<>(0, 2), emptyGrid.getBoxCoordinates(0, 8));
    assertEquals(new Pair<>(0, 2), emptyGrid.getBoxCoordinates(2, 6));
    assertEquals(new Pair<>(0, 2), emptyGrid.getBoxCoordinates(2, 8));

    assertEquals(new Pair<>(1, 0), emptyGrid.getBoxCoordinates(3, 0));
    assertEquals(new Pair<>(1, 0), emptyGrid.getBoxCoordinates(3, 2));
    assertEquals(new Pair<>(1, 0), emptyGrid.getBoxCoordinates(5, 0));
    assertEquals(new Pair<>(1, 0), emptyGrid.getBoxCoordinates(5, 2));

    assertEquals(new Pair<>(1, 1), emptyGrid.getBoxCoordinates(3, 3));
    assertEquals(new Pair<>(1, 1), emptyGrid.getBoxCoordinates(3, 5));
    assertEquals(new Pair<>(1, 1), emptyGrid.getBoxCoordinates(5, 3));
    assertEquals(new Pair<>(1, 1), emptyGrid.getBoxCoordinates(5, 5));

    assertEquals(new Pair<>(1, 2), emptyGrid.getBoxCoordinates(3, 6));
    assertEquals(new Pair<>(1, 2), emptyGrid.getBoxCoordinates(3, 8));
    assertEquals(new Pair<>(1, 2), emptyGrid.getBoxCoordinates(5, 6));
    assertEquals(new Pair<>(1, 2), emptyGrid.getBoxCoordinates(5, 8));

    assertEquals(new Pair<>(2, 0), emptyGrid.getBoxCoordinates(6, 0));
    assertEquals(new Pair<>(2, 0), emptyGrid.getBoxCoordinates(6, 2));
    assertEquals(new Pair<>(2, 0), emptyGrid.getBoxCoordinates(8, 0));
    assertEquals(new Pair<>(2, 0), emptyGrid.getBoxCoordinates(8, 2));

    assertEquals(new Pair<>(2, 1), emptyGrid.getBoxCoordinates(6, 3));
    assertEquals(new Pair<>(2, 1), emptyGrid.getBoxCoordinates(6, 5));
    assertEquals(new Pair<>(2, 1), emptyGrid.getBoxCoordinates(8, 3));
    assertEquals(new Pair<>(2, 1), emptyGrid.getBoxCoordinates(8, 5));

    assertEquals(new Pair<>(2, 2), emptyGrid.getBoxCoordinates(6, 6));
    assertEquals(new Pair<>(2, 2), emptyGrid.getBoxCoordinates(6, 8));
    assertEquals(new Pair<>(2, 2), emptyGrid.getBoxCoordinates(8, 6));
    assertEquals(new Pair<>(2, 2), emptyGrid.getBoxCoordinates(8, 8));
  }

  @org.junit.Test
  public void testGetElements() throws Exception {
    assertEquals(new ArrayList<>(Arrays.asList(
        new Pair<>(2, 0), new Pair<>(2, 1), new Pair<>(2, 2),
        new Pair<>(2, 3), new Pair<>(2, 4), new Pair<>(2, 5),
        new Pair<>(2, 6), new Pair<>(2, 7), new Pair<>(2, 8))), emptyGrid.getRowElements(2, 3));

    assertEquals(new ArrayList<>(Arrays.asList(
        new Pair<>(0, 3), new Pair<>(1, 3), new Pair<>(2, 3),
        new Pair<>(3, 3), new Pair<>(4, 3), new Pair<>(5, 3),
        new Pair<>(6, 3), new Pair<>(7, 3), new Pair<>(8, 3))), emptyGrid.getColumnElements(2, 3));

    assertEquals(new ArrayList<>(Arrays.asList(
        new Pair<>(0, 3), new Pair<>(0, 4), new Pair<>(0, 5),
        new Pair<>(1, 3), new Pair<>(1, 4), new Pair<>(1, 5),
        new Pair<>(2, 3), new Pair<>(2, 4), new Pair<>(2, 5))), emptyGrid.getBoxElements(2, 3));
  }

  @org.junit.Test
  public void testGetSetCandidate() throws Exception {
    ISquareSudokuGrid emptyGrid = new StandardSudokuGrid(new int[][] {
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    });

    // Initially, no candidate values are set.
    assertFalse(emptyGrid.isACandidate(1, 0, 9));
    assertFalse(emptyGrid.isACandidate(2, 4, 6));
    assertFalse(emptyGrid.isACandidate(8, 6, 2));
    assertFalse(emptyGrid.isACandidate(5, 0, 3));

    ISquareSudokuGrid updatedGrid = emptyGrid.setCandidate(1, 0, 5, true)
        .setCandidate(1, 0, 6, false).setCandidate(8, 6, 3, true).setCandidate(8, 6, 4, true);

    // Shouldn't have updated the original grid.
    assertFalse(emptyGrid.isACandidate(1, 0, 5));
    assertFalse(emptyGrid.isACandidate(8, 6, 3));
    assertFalse(emptyGrid.isACandidate(8, 6, 4));

    assertTrue(updatedGrid.isACandidate(1, 0, 5));
    assertFalse(updatedGrid.isACandidate(1, 0, 6));
    assertEquals(new TreeSet<>(Collections.singletonList(5)), updatedGrid.getCandidateValues(1, 0));

    assertTrue(updatedGrid.isACandidate(8, 6, 3));
    assertTrue(updatedGrid.isACandidate(8, 6, 4));
    assertEquals(new TreeSet<>(Arrays.asList(3, 4)), updatedGrid.getCandidateValues(8, 6));
  }

  @org.junit.Test
  public void testIsFixed() throws Exception {
    ISquareSudokuGrid partiallyFilledGrid = new StandardSudokuGrid(new int[][] {
        {0, 0, 0, 0, 0, 5, 0, 0, 7},
        {7, 0, 0, 0, 8, 9, 1, 0, 3},
        {0, 9, 2, 7, 0, 0, 0, 0, 5},
        {0, 0, 0, 2, 4, 7, 0, 0, 1},
        {0, 0, 5, 0, 0, 0, 9, 0, 0},
        {0, 2, 8, 9, 0, 0, 6, 0, 0},
        {2, 0, 9, 1, 0, 0, 3, 0, 0},
        {3, 4, 6, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    });
    partiallyFilledGrid.setCandidate(0, 7, 2, true);
    partiallyFilledGrid.setCandidate(0, 7, 4, true);
    partiallyFilledGrid.setCandidate(0, 7, 6, true);
    partiallyFilledGrid.setCandidate(0, 7, 8, true);
    partiallyFilledGrid.setCandidate(0, 7, 9, true);

    // Being fixed is independent of the number of candidate values (somewhat unexpected behavior).
    assertEquals(0, partiallyFilledGrid.getValue(0, 7));
    assertFalse(partiallyFilledGrid.isFixed(0, 7));
    assertTrue(partiallyFilledGrid.isFixed(6, 3));
  }

  @org.junit.Test
  public void testIsSolved() throws Exception {
    ISquareSudokuGrid solvedGrid = new StandardSudokuGrid(new int[][] {
        {3, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    });
    assertTrue(solvedGrid.isSolved());

    ISquareSudokuGrid notFilledGrid = new StandardSudokuGrid(new int[][] {
        {0, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    });
    assertFalse(notFilledGrid.isSolved());

    ISquareSudokuGrid repeatInRow = new StandardSudokuGrid(new int[][] {
        {3, 7, 8, 2, 6, 5, 9, 1, 3},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    });
    assertFalse(repeatInRow.isSolved());

    ISquareSudokuGrid repeatInCol = new StandardSudokuGrid(new int[][] {
        {3, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 2, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {3, 2, 1, 6, 5, 8, 3, 4, 7}
    });
    assertFalse(repeatInCol.isSolved());

    ISquareSudokuGrid repeatInBox = new StandardSudokuGrid(new int[][] {
        {3, 7, 8, 2, 6, 5, 9, 1, 4},
        {5, 9, 6, 8, 1, 4, 7, 3, 2},
        {1, 4, 3, 7, 3, 9, 5, 6, 8},
        {2, 1, 7, 3, 8, 6, 4, 5, 9},
        {8, 5, 4, 9, 7, 1, 6, 2, 3},
        {6, 3, 9, 5, 4, 2, 8, 7, 1},
        {7, 8, 5, 4, 2, 3, 1, 9, 6},
        {4, 6, 3, 1, 9, 7, 2, 8, 5},
        {9, 2, 1, 6, 5, 8, 3, 4, 7}
    });
    assertFalse(repeatInBox.isSolved());
  }

  @org.junit.Test
  public void testGridToString() throws Exception {
    ISquareSudokuGrid partiallyFilledGrid = new StandardSudokuGrid(new int[][] {
        {0, 0, 0, 0, 0, 5, 0, 0, 7},
        {7, 0, 0, 0, 8, 9, 1, 0, 3},
        {0, 9, 2, 7, 0, 0, 0, 0, 5},
        {0, 0, 0, 2, 4, 7, 0, 0, 1},
        {0, 0, 5, 0, 0, 0, 9, 0, 0},
        {0, 2, 8, 9, 0, 0, 6, 0, 0},
        {2, 0, 9, 1, 0, 0, 3, 0, 0},
        {3, 4, 6, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0}
    });
    assertEquals("   |  5|  7\n" +
        "7  | 89|1 3\n" +
        " 92|7  |  5\n" +
        "---+---+---\n" +
        "   |247|  1\n" +
        "  5|   |9  \n" +
        " 28|9  |6  \n" +
        "---+---+---\n" +
        "2 9|1  |3  \n" +
        "346|   |   \n" +
        "   |   |   \n", partiallyFilledGrid.gridToString());
  }
}