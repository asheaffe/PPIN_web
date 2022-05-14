package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import functions.Constants;
import functions.Static;

public class Mapping {

  // Singleton pattern
  private static HashMap<String, Mapping> names2idss = new HashMap<String, Mapping>();


  private HashMap<String, HashSet<String>> forward = new HashMap<String, HashSet<String>>();
  private HashMap<String, HashSet<String>> backward = new HashMap<String, HashSet<String>>();

  /**
   * Creates a 2-way 1-to-many mapping of strings.
   *
   * @param filename
   * @param separator
   * @param col1
   * @param col2
   * @throws Exception
   */
  public Mapping(String filename, String separator, int col1, int col2) throws Exception {
    if (filename.trim().length() > 0) {
      BufferedReader in = new BufferedReader(new FileReader(filename));
      String line = "";

      while ((line = Static.skipCommentLines(in)) != null) {
        String[] split = line.split(separator);
        if (split.length < col1 + 1 || split.length < col2 + 1) {
          continue;
        }

        if (split[col1].trim().length() > 0 && split[col2].trim().length() > 0) {
          this.add(split[col1], split[col2]);
        }
      }
      in.close();
    }
  }

  /**
   * Creates an empty Mapping.
   */
  public Mapping() {}


  /**
   * Singleton pattern for Mappings from names to ids. Uses DomainEvolution datafiles.
   * @param species - species for which the name-id mapping is to be retrieved
   * @return
   * @throws Exception
   */
  public static Mapping getNames2Ids(String species) throws Exception {
    if (species.startsWith("test")) {
      return new TestMapping();
    }

    if (!Mapping.names2idss.containsKey(species)) {
      Mapping names2ids = new Mapping("data/" + species + "/" + species + ".ensembl" + Constants.FILE_VERSION + ".txt", "\t", 0, 3);
      Mapping.names2idss.put(species, names2ids);
    }

    return Mapping.names2idss.get(species);
  }

  /**
   * Creates a Hashtable object that maps uniprot IDs to gene names, from a tab-separated uniprot
   * download. Includes any gene name synonyms.
   *
   * Input files: ???.uniprot.mapping.dat
   *
   * @param uniprotFile - location of the uniprot mapping file (Should be the 3-column,
   *        tab-delimited version. Others are incomplete.)
   * @return - a Hashtable that maps a Uniprot ID to a gene name
   */
  public static Mapping uniprotToName(String uniprotFilename) throws Exception {
    Mapping result = new Mapping();

    String line = "";
    BufferedReader in = new BufferedReader(new FileReader(uniprotFilename));
    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");

      String uniprot = split[0];
      String idType = split[1];
      String mappedId = split[2];

      if (idType.equals("Gene_Name") || idType.equals("Gene_Synonym")) {
        result.add(uniprot, mappedId.toUpperCase());
      }
    }

    in.close();

    return result;
  }

  /**
   * Creates a Hashtable object that maps uniprot IDs to Entrezgene IDs, from a tab-separated
   * uniprot download.
   *
   * Input files: ???.uniprot.mapping.dat
   *
   * @param uniprotFile - location of the uniprot mapping file (Should be the 3-column,
   *        tab-delimited version. Others are incomplete.)
   * @return - a Hashtable that maps a Uniprot ID to a gene name
   */
  public static Mapping uniprotToEntrezgene(String uniprotFilename) throws Exception {
    Mapping result = new Mapping();

    String line = "";
    BufferedReader in = new BufferedReader(new FileReader(uniprotFilename));
    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");

      String uniprot = split[0];
      String idType = split[1];
      String mappedId = split[2];

      if (idType.equals("GeneID")) {

        result.add(uniprot, mappedId.toUpperCase());
      }
    }

    in.close();

    return result;
  }

  /**
   * A convenience method to create the best possible Mapping from Refseq and Uniprot IDs and
   * EntrezGene #s to Ensembl protein names. Filenames are HARD-CODED. Should be changed as data
   * files are updated!
   *
   * @param species
   * @return
   * @throws Exception
   */
  public static Mapping masterNameMapping(String species) throws Exception {
    Mapping newMapping =
        new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 0, 4);
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 0, 5));
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2refseq.ensembl89.txt", "\t", 0, 4));
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2refseq.ensembl89.txt", "\t", 0, 5));
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2entrezgene.ensembl89.txt", "\t", 0, 4));
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".uniprot2genename.txt", "\t", 0, 1));

    return newMapping;
  }

  /**
   * A convenience method to create the best possible Mapping from Refseq and Uniprot IDs to Ensembl
   * protein ids. Filenames are HARD-CODED. Should be changed as data files are updated!
   *
   * @param species
   * @return
   * @throws Exception
   */
  public static Mapping masterEnsemblpMapping(String species) throws Exception {
    Mapping newMapping =
        new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 3, 4);
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 3, 5));
    newMapping.addMapping(new Mapping( "../DomainEvolution/" + species + "/" + species + ".ensembl2refseq.ensembl89.txt", "\t", 3, 4));
    newMapping.addMapping(new Mapping("../DomainEvolution/" + species + "/" + species + ".ensembl2refseq.ensembl89.txt", "\t", 3, 5));

    newMapping.addMapping(new Mapping("data/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 3, 4));
    newMapping.addMapping(new Mapping("data/" + species + "/" + species + ".ensembl2uniprot.ensembl89.txt", "\t", 3, 5));

    Mapping u2eg = new Mapping("data/" + species + "/" + species + ".uniprot2entrezgene.txt", "\t", 0, 1);
    Mapping e2eg =
        new Mapping("data/" + species + "/" + species + ".ensembl2entrezgene.ensembl89.txt", "\t", 3, 4);

    Mapping e2u = Mapping.mergeMappings(e2eg, u2eg, 'b', 'b');
    newMapping.addMapping(e2u);

    return newMapping;
  }

  /**
   * Adds a key-value pair to this 2-way Mapping.
   *
   * @param key - a key that will map to the value
   * @param value - a value that will reverse-map to the key
   */
  public void add(String key, String value) {
    String newKey = key.trim();
    String newValue = value.trim();

    if (newKey.length() == 0 || newValue.length() == 0) {
      return;
    }

    if (!this.containsForward(newKey)) {
      this.forward.put(newKey, new HashSet<String>());
    }
    this.forward.get(newKey).add(newValue);

    if (!this.containsBackward(newValue)) {
      this.backward.put(newValue, new HashSet<String>());
    }
    this.backward.get(newValue).add(newKey);
  }

  /**
   * Adds one-to-many key-value pairings to this 2-way Mapping
   *
   * @param key - the key that will map to all the values
   * @param valueCollection - the values that will all reverse-map to the key
   */
  public void add(String key, Collection<String> valueCollection) {
    for (String value: valueCollection) {
      String newKey = key.trim();
      String newValue = value.trim();

      if (newKey.length() == 0 || newValue.length() == 0) {
        continue;
      }

      this.add(newKey, newValue);
    }
  }

  /**
   * Adds many-to-one key-value pairings to this 2-way Mapping
   *
   * @param keyCollection - the keys that will all reverse-map to the value
   * @param value - the value that will map to all the keys
   */
  public void add(Collection<String> keyCollection, String value) {
    for (String key: keyCollection) {
      this.add(key, value);
    }
  }

  /**
   * Adds many-to-many key-value pairings to this 2-way Mapping
   *
   * @param keyCollection - the keys that will all map to all the values
   * @param valueCollection - the values that will all reverse-map to the keys
   */
  public void add(Collection<String> keyCollection, Collection<String> valueCollection) {
    for (String key: keyCollection) {
      this.add(key, valueCollection);
    }
  }

  /**
   * Check to see if this map has a key, in the forward direction
   *
   * @param key - key to be checked
   * @return - true or false
   */
  public boolean containsForward(String key) {
    return this.forward.containsKey(key);
  }

  /**
   * Gets the values associated with a key, in the forward direction
   *
   * @param key - key whose values are to be retrieved
   * @return - values associated with the key
   */
  public HashSet<String> getForward(String key) {
    return this.forward.get(key);
  }

  public HashSet<String> getForwardKeys() {
    return new HashSet<String>(this.forward.keySet());
  }

  /**
   * Check to see if this map has a value, in the backward direction
   *
   * @param value - key to be checked
   * @return - true or false
   */
  public boolean containsBackward(String value) {
    return this.backward.containsKey(value);
  }

  /**
   * Get the keys associated with a value, in the backward direction
   *
   * @param value - value whose keys are to be retrieved
   * @return - keys associated with the value
   */
  public HashSet<String> getBackward(String value) {
    return this.backward.get(value);
  }

  public HashSet<String> getBackwardValues() {
    return new HashSet<String>(this.backward.keySet());
  }

  /**
   * Adds the contents of another Mapping to this one.
   *
   * @param mapping - the other Mapping whose pairings are to be added
   */
  public void addMapping(Mapping mapping) {
    for (String name: mapping.forward.keySet()) {
      this.add(name, mapping.getForward(name));
    }
  }

  /**
   * Merges two Mapping objects into a new Mapping object, leaving the two original Mappings
   * unaltered. Will merge along the keys of the Mapping objects - the resulting object will map the
   * values of the first Mapping to the values of the second Mapping, where the keys of those
   * Mappings were the same (subject to directional flipping)
   *
   * @param mapping1 - the first Mapping object to be merged
   * @param mapping2 - the second Mapping object to be merged
   * @param dir1 - the direction the first Mapping object is to be considered
   * @param dir2 - the direction the second Mapping object is to be considered
   * @return - the new merged Mapping object
   * @throws Exception
   */
  public static Mapping mergeMappings(Mapping mapping1, Mapping mapping2, char dir1, char dir2) throws Exception {
    HashMap<String, HashSet<String>> dictionary1;
    HashMap<String, HashSet<String>> dictionary2;
    Mapping newMapping = new Mapping();

    // Adjust for directionality as required
    if (dir1 == 'f') {
      dictionary1 = mapping1.forward;
    }
    else if (dir1 == 'b') {
      dictionary1 = mapping1.backward;
    }
    else {
      throw new Exception("dir1 must be either 'f' or 'b'.");
    }

    if (dir2 == 'f') {
      dictionary2 = mapping2.forward;
    }
    else if (dir1 == 'b') {
      dictionary2 = mapping2.backward;
    }
    else {
      throw new Exception("dir2 must be either 'f' or 'b'.");
    }


    for (String key: dictionary1.keySet()) {
      // System.out.println("Key: " + key);
      // System.out.println(dictionary1.get(key));
      // System.out.println(dictionary2.get(key));
      if (dictionary2.get(key) != null) {
        for (String value1: dictionary1.get(key)) {
          for (String value2: dictionary2.get(key)) {
            newMapping.add(value1, value2);
          }
        }
      }
    }

    return newMapping;
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
    HashMap<String, HashSet<String>> dictionary1;

    // Adjust for directionality as required
    if (dir == 'f') {
      dictionary1 = this.forward;
    }
    else if (dir == 'b') {
      dictionary1 = this.backward;
    }
    else {
      throw new Exception("dir must be either 'f' or 'b'.");
    }

    if (keep != 'a' && keep != 'n' && keep != 'm') {
      throw new Exception("keep must be either 'a', 'n', or 'm'.");
    }

    for (String string: input) {
      if (dictionary1.containsKey(string)) {
        output.addAll(dictionary1.get(string));
      }

      // If we always keep the input string, or we only keep the input string if no remap is found
      // and no remap is found
      if (keep == 'a' || (keep == 'm' && !dictionary1.containsKey(string))) {
        output.add(string);
      }
    }
  }

  /**
   * Removes version numbers from all of the strings in this Mapping. That is, any string ending in
   * .X, where X is an integer, has the .X removed. Also any string ending in -X, where X is an
   * integer, has the -X removed.
   */
  public void removeVersionNumbers() {
    HashMap<String, HashSet<String>> newForward = new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> newBackward = new HashMap<String, HashSet<String>>();
    Pattern p = Pattern.compile("[.]+[\\.\\-]\\d");

    for (String key: this.forward.keySet()) {
      String newKey = key;
      Matcher m = p.matcher(newKey);
      if (m.matches()) {
        newKey = m.group(1);
      }
      if (!newForward.containsKey(newKey)) {
        newForward.put(newKey, new HashSet<String>());
      }
      for (String value: this.forward.get(key)) {
        String newValue = value;
        m = p.matcher(newValue);
        if (m.matches()) {
          newValue = m.group(1);
        }
        newForward.get(newKey).add(newValue);
      }
    }

    for (String value: this.backward.keySet()) {
      String newValue = value;
      Matcher m = p.matcher(newValue);
      if (m.matches()) {
        newValue = m.group(1);
      }
      if (!newBackward.containsKey(newValue)) {
        newBackward.put(newValue, new HashSet<String>());
      }
      for (String key: this.backward.get(value)) {
        String newKey = key;
        m = p.matcher(newKey);
        if (m.matches()) {
          newKey = m.group(1);
        }
        newBackward.get(newValue).add(newKey);
      }
    }

    this.forward = newForward;
    this.backward = newBackward;
  }

  /**
   * Changes every string in this Mapping object to all upper case.
   */
  public void toUpperCase() {
    HashMap<String, HashSet<String>> newForward = new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> newBackward = new HashMap<String, HashSet<String>>();

    for (String key: this.forward.keySet()) {
      String newKey = key.toUpperCase();
      newForward.putIfAbsent(newKey, new HashSet<String>());

      for (String value: this.forward.get(key)) {
        String newValue = value.toUpperCase();
        newForward.get(newKey).add(newValue);
      }
    }

    for (String value: this.backward.keySet()) {
      String newValue = value.toUpperCase();
      newBackward.putIfAbsent(newValue, new HashSet<String>());

      for (String key: this.backward.get(value)) {
        String newKey = key.toUpperCase();
        newBackward.get(newValue).add(newKey);
      }
    }

    this.forward = newForward;
    this.backward = newBackward;
  }

  /**
   * Returns string output for this Mapping object, one mapped pair per line, tab-separated, lexographically sorted.
   */
  public String toString() {
    StringBuffer output = new StringBuffer();
    ArrayList<String> firstOrder = new ArrayList<String>(this.forward.keySet());
    Collections.sort(firstOrder);

    for (String first: firstOrder) {
      ArrayList<String> secondOrder = new ArrayList<String>(this.forward.get(first));
      Collections.sort(secondOrder);

      for (String second: secondOrder) {
        output.append(first + "\t" + second + "\n");
      }
    }
    return output.toString();
  }

  public void toFile(String filename) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(filename));
    out.write(this.toString());
    out.close();
  }

}
