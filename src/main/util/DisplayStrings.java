package main.util;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Helper methods for display string representations.
 */
public class DisplayStrings {
  /**
   * Displays the contents of a set in a single line, with elements separated by spaces.
   * @param set the set to display
   * @param <T> the type of the elements in the set
   * @return  a string representation of the set
   */
  @NotNull
  public static <T> String setToString(Set<T> set) {
    StringBuilder sb = new StringBuilder();
    for (T value : set) {
      sb.append(value);
      sb.append(' ');
    }
    return sb.toString();
  }
}
