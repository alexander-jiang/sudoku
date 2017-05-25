package util;

/**
 * Utility class to represent an ordered pair.
 */
public class Pair<A, B> {
  private A first;
  private B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A first() {
    return first;
  }

  public B second() {
    return second;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Pair)) {
      return false;
    }
    Pair<?, ?> otherPair = (Pair) other;
    return first().equals(otherPair.first()) && second().equals(otherPair.second());
  }
}
