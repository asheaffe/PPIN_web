package data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class TestMapping extends Mapping {

  /**
   * Creates an empty Mapping.
   */
  public TestMapping() {}

  /**
   * Empty method.
   */
  public void add(String key, String value) {}

  /**
   * Empty method.
   */
  public void add(String key, Collection<String> valueCollection) {}

  /**
   * Empty method.
   */
  public void add(Collection<String> keyCollection, String value) {}

  /**
   * Empty method.
   */
  public void add(Collection<String> keyCollection, Collection<String> valueCollection) {}

  /**
   * @return - true
   */
  public boolean containsForward(String key) {
    return true;
  }

  /**
   * @return - a HashSet containing the key
   */
  public HashSet<String> getForward(String key) {
    HashSet<String> result = new HashSet<String>();
    result.add(key);
    return result;
  }
  
  /** 
   * Empty method
   */
  public HashSet<String> getForwardKeys() {
    return null;
  }

  /**
   * @return - true
   */
  public boolean containsBackward(String value) {
    return true;
  }

  /**
   * @param value - value whose keys are to be retrieved
   * @return - a HashSet containing the value
   */
  public HashSet<String> getBackward(String value) {
    HashSet<String> result = new HashSet<String>();
    result.add(value);
    return result;
  }
  
  /** 
   * Empty method
   */
  public HashSet<String> getBackwardValues() {
    return null;
  }

  /**
   * Empty method.
   */
  public void addMapping(Mapping mapping) {}

  /**
   * Empty method.
   * 
   * @return - null
   */
  public static Mapping mergeMappings(Mapping mapping1, Mapping mapping2, char dir1, char dir2) {
    return null;
  }
  
  /**
   * Remaps every string in the input according to this Mapping, and adds them to the output. The
   * original inputs are also added to the output data structure, for redundancy purposes.
   * 
   * @param input - the strings to be remapped
   * @param output - an output data structure, to which the remapped strings are added - should
   *        usually be empty, but not always
   * @param dir - direction which this mapping is to be read: 'f' for forward or 'b' for backward
   * @throws Exception
   */
  public void remapKeepOriginal(Collection<String> input, Collection<String> output, char dir) throws Exception {
    this.remap(input, output, dir, 'a');
  }

  /**
   * Remaps every string in the input according to this Mapping, and adds them to the output. The
   * original inputs are not added to the output data structure (unless they are mapped to
   * themselves).
   * 
   * @param input - the strings to be remapped
   * @param output - an output data structure, to which the remapped strings are added - should
   *        usually be empty, but not always
   * @param dir - direction which this mapping is to be read: 'f' for forward or 'b' for backward
   * @throws Exception
   */
  public void remapDiscardOriginal(Collection<String> input, Collection<String> output, char dir) throws Exception {
    this.remap(input, output, dir, 'n');
  }

  /**
   * Remaps every string in the input according to this Mapping, and adds them to the output. The
   * original inputs are added to the output data structure if no mapping is found for the input
   * string.
   * 
   * @param input - the strings to be remapped
   * @param output - an output data structure, to which the remapped strings are added - should
   *        usually be empty, but not always
   * @param dir - direction which this mapping is to be read: 'f' for forward or 'b' for backward
   * @throws Exception
   */
  public void remapCheckOriginal(Collection<String> input, Collection<String> output, char dir) throws Exception {
    this.remap(input, output, dir, 'm');
  }
  
  /**
   * Generic helper method that serves as the backbone method for various remapping methods
   * 
   * @param input - a collection of strings to be remapped
   * @param output - an output data structure, to which the remapped strings are to be added -
   *        should usually be empty!
   * @param dir - indicates the direction this mapping is to be read
   * @param keep - indicates whether to include the input strings in the output - 'a' for always,
   *        'n' for never, 'm' for only if no remapping for that input string is found
   * @throws Exception
   */
  private void remap(Collection<String> input, Collection<String> output, char dir, char keep) throws Exception {
    if (keep != 'a' && keep != 'n' && keep != 'm') {
      throw new Exception("keep must be either 'a', 'n', or 'm'.");
    }

    for (String string: input) {
      output.add(string);
    }
  }

  /**
   * Empty method.
   */
  public void removeVersionNumbers() {}

  /**
   * Empty method.
   */
  public void toUpperCase() {}

  /**
   * Returns "Test Mapping".
   */
  public String toString() {
    return "Test Mapping";
  }

  /**
   * Empty method.
   */
  public void toFile(String filename) {}
}
