package data;

/**
 * Helper class for adding false positives to a network. A 2-tuple of Strings.
 *
 * @author Brian
 *
 */
public class PairOfStrings implements Comparable<PairOfStrings> {
  private String string1;
  private String string2;

  public PairOfStrings(String string1, String string2) {
    this.string1 = string1;
    this.string2 = string2;
  }

  /**
   * @return the string1
   */
  public String getString1() {
    return string1;
  }

  /**
   * @param string1 the string1 to set
   */
  public void setString1(String string1) {
    this.string1 = string1;
  }

  /**
   * @param string2 the string2 to set
   */
  public void setString2(String string2) {
    this.string2 = string2;
  }

  /**
   * @return the string2
   */
  public String getString2() {
    return string2;
  }

  /**
   * Checks to see if a given string is in either spot in this PairOfStrings.
   * @param string
   * @return
   */
  public boolean contains(String string) {
    return this.isString1(string) || this.isString2(string);
  }

  /**
   * Checks to see if a given string is in the first spot in this PairOfStrings.
   * @param string
   * @return
   */
  public boolean isString1(String string) {
    return (string == null && this.string1 == null) || this.string1.equals(string);
  }

  /**
   * Checks to see if a given string is in the second spot in this PairOfStrings.
   * @param string
   * @return
   */
  public boolean isString2(String string) {
    return (string == null && this.string2 == null) || this.string2.equals(string);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((string1 == null) ? 0 : string1.hashCode());
    result = prime * result + ((string2 == null) ? 0 : string2.hashCode());
    return result;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PairOfStrings)) {
      return false;
    }
    PairOfStrings other = (PairOfStrings) obj;
    if (string1 == null) {
      if (other.string1 != null) {
        return false;
      }
    }
    else if (!string1.equals(other.string1)) {
      return false;
    }
    if (string2 == null) {
      if (other.string2 != null) {
        return false;
      }
    }
    else if (!string2.equals(other.string2)) {
      return false;
    }
    return true;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "PairOfStrings [string1=" + string1 + ", string2=" + string2 + "]";
  }

  @Override
  public int compareTo(PairOfStrings arg0) {
    if (this.string1.compareTo(arg0.string1) != 0) {
      return this.string1.compareTo(arg0.string1);
    }
    else {
      return this.string2.compareTo(arg0.string2);
    }
  }







}