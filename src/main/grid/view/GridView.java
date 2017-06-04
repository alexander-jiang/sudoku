package main.grid.view;

import javax.swing.*;
import java.util.Scanner;
import java.util.Set;

/**
 * The view (GUI) for the Sudoku grid.
 */
public class GridView extends JFrame {

  private IModelAdapter model;

  public GridView(IModelAdapter modelAdpt) {
    this.model = modelAdpt;
    initGUI();
  }

  /**
   * Initialize the GUI components.
   */
  private void initGUI() {

  }

  public void start() {
    setVisible(true);
  }

  public void waitForInput() {
    Scanner inputReader = new Scanner(System.in);

    while (true) {
      System.out.println("Menu:");
      System.out.println("1. Update a value");
      System.out.println("2. Update a candidate value");
      System.out.println("3. View candidate values for an element");
      System.out.println("0. Quit");
      int option = Integer.parseInt(inputReader.nextLine());
      if (option == 1) {
        System.out.println("Enter the row and column coordinates and the new value to enter at those coordinates:");
        String line = inputReader.nextLine();
        String[] tokens = line.split("\\s*,\\s*");
        model.updateGridValue(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
      } else if (option == 2) {
        System.out.println("Enter the row and column coordinates and the candidate value and ");
        System.out.println("whether the value should be a candidate or not (1 or 0) at those coordinates:");
        String line = inputReader.nextLine();
        String[] tokens = line.split("\\s*,\\s*");
        model.updateGridCandidateValue(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
            Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]) == 1);
      } else if (option == 3) {
        System.out.println("Enter the row and column coordinates to see the candidate values at those coordinates");
        String line = inputReader.nextLine();
        String[] tokens = line.split("\\s*,\\s*");
        Set<Integer> candidates = model.getCandidateValues(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
        for (int value : candidates) {
          System.out.print(value + " ");
        }
        System.out.print("\n");
      } else if (option == 0) {
        break;
      }
    }
    inputReader.close();
  }
}
