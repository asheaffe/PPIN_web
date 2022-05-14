package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import functions.Constants;
import functions.Ortholog;
import functions.Static;

public class Orthologies {
  // Mapping is species1 -> species2 -> protein1 -> protein2 (ortholog) -> sources
  private HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>> orthoMapper;
  private List<String> SPECIESES;
  private static Orthologies orthologies;

  public Orthologies(String[] specieses) throws Exception {
    Static.debugOutput("Constructing Orthologies object for specieses " + Arrays.toString(specieses) + ". (" + new Date().toString()
        + ")");
    this.orthoMapper = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>>();
    this.SPECIESES = Collections.unmodifiableList(Arrays.asList(specieses));

    for (String species1: specieses) {
      this.orthoMapper.putIfAbsent(species1, new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>());

      for (String species2: specieses) {
        if (species1.compareToIgnoreCase(species2) >= 0) {
          continue;
        }
        this.processTwoSpecies(species1, species2);
      }
    }

    // Manual addition of TOCA-1/2 with BZZ1. Inparanoid indicates they are both orthologs of human
    // TRIO, but doesn't
    // mark them as mutual orthologs (??)
    if (this.SPECIESES.contains("C.elegans") && this.SPECIESES.contains("S.cerevisiae")) {
      this.addOrtholog("C.elegans", "F09E10.8a", "S.cerevisiae", "YHR114W");
      this.addOrtholog("C.elegans", "K08E3.3a", "S.cerevisiae", "YHR114W");
    }

    Static.debugOutput("Done constructing Orthologies object for specieses " + Arrays.toString(specieses) + ". ("
        + new Date().toString() + ")");
  }

  /**
   * Creates an empty Orthologies object.
   */
  private Orthologies() {
    this.orthoMapper = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>>();
    this.SPECIESES = new ArrayList<String>();
  }

  /**
   * Initialize the singleton Orthologies object if it does not already exist, using orthology data
   * from all default species.
   *
   * @return
   * @throws Exception
   */
  public static Orthologies getOrthologies() throws Exception {
    String[] specieses =
        {"H.sapiens", "M.musculus", "R.norvegicus", "D.rerio", "D.melanogaster", "C.elegans", "S.cerevisiae"};
    if (Orthologies.orthologies == null || !Orthologies.orthologies.SPECIESES.containsAll(Arrays.asList(specieses))) {
      Orthologies.orthologies = new Orthologies(specieses);
    }
    return Orthologies.orthologies;
  }

  /**
   * Initialize the singleton Orthologies object if it does not already exist. Adds the orthology
   * data for the two specified species if not already done.
   *
   * @param species1 - one of the two species for which orthology data is needed
   * @param species2 - one of the two species for which orthology data is needed
   * @return
   * @throws Exception
   */
  public static Orthologies getOrthologies(String species1, String species2) throws Exception {
    String[] specieses = new String[2];
    specieses[0] = species1;
    specieses[1] = species2;

    if (Orthologies.orthologies == null) {
      Orthologies.orthologies = new Orthologies(specieses);
    }
    else if (Orthologies.orthologies.orthoMapper.get(species1) == null ||
        !Orthologies.orthologies.orthoMapper.get(species1).containsKey(species2)) {
      Orthologies.orthologies.processTwoSpecies(species1, species2);
    }

    return Orthologies.orthologies;
  }

  /**
   * Helper method to process ortholog data for two species and add it to this Orthologies object.
   * Either called by the constructor Orthologies(String[]) or individually through
   * getOrthologies(String, String). Bi-directional. To be used only by constructors.
   *
   * @param species1
   * @param species2
   * @throws Exception
   */
  private void processTwoSpecies(String species1, String species2) throws Exception {
    // Do not check to ensure two difference species; responsibility is caller's

    // Escape hatch for test data
    if (species1.startsWith("test")) {
      this.processTestData(species1, species2);
      return;
    }

    Static.debugOutput("Orthologies.processTwoSpecies() collecting orthology data from " + species1 + " & " + species2 + ". (" + new Date().toString()
        + ")");

    // Process orthology data files
    HashMap<String, HashMap<String, HashSet<String>>> ortho1 = Ortholog.getOrthologies(species1, species2);
    this.orthoMapper.putIfAbsent(species1, new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>());
    this.orthoMapper.get(species1).putIfAbsent(species2, ortho1);

    // Flip orthology data for the other species
    HashMap<String, HashMap<String, HashSet<String>>> flippedOrthos =
        new HashMap<String, HashMap<String, HashSet<String>>>();

    for (String protein1: ortho1.keySet()) {
      for (String protein2: ortho1.get(protein1).keySet()) {
        flippedOrthos.putIfAbsent(protein2, new HashMap<String, HashSet<String>>());
        flippedOrthos.get(protein2).put(protein1, ortho1.get(protein1).get(protein2));
      }
    }

    // Add flipped orthology data to this Orthologies object.
    this.orthoMapper.putIfAbsent(species2, new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>());
    this.orthoMapper.get(species2).put(species1, flippedOrthos);

    Static.debugOutput("Done collecting orthology data from " + species1 + " & " + species2 + ". (" + new Date().toString()
        + ")");

  }

  /**
   * Adds test orthology data to the singleton Orthologs object.
   *
   * @param species1 - the first test species name; should start with "test"
   * @param species2 - the second test species name; should start with "test"
   * @throws Exception
   */
  private void processTestData(String species1, String species2) throws Exception {
    BufferedReader in =
        new BufferedReader(new FileReader("testbed/ortholog_" + species1.substring(4) + "_" + species2.substring(4) + ".txt"));
    String line = "";

    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");
      this.orthoMapper.putIfAbsent(species1, new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>());
      this.orthoMapper.get(species1).putIfAbsent(species2, new HashMap<String, HashMap<String, HashSet<String>>>());
      this.orthoMapper.get(species1).get(species2).putIfAbsent(split[0], new HashMap<String, HashSet<String>>());
      this.orthoMapper.get(species1).get(species2).get(split[0]).put(split[1], new HashSet<String>());
      this.orthoMapper.get(species1).get(species2).get(split[0]).get(split[1]).add("Test Data");

      this.orthoMapper.putIfAbsent(species2, new HashMap<String, HashMap<String, HashMap<String, HashSet<String>>>>());
      this.orthoMapper.get(species2).putIfAbsent(species1, new HashMap<String, HashMap<String, HashSet<String>>>());
      this.orthoMapper.get(species2).get(species1).putIfAbsent(split[1], new HashMap<String, HashSet<String>>());
      this.orthoMapper.get(species2).get(species1).get(split[1]).put(split[0], new HashSet<String>());
      this.orthoMapper.get(species2).get(species1).get(split[1]).get(split[0]).add("Test Data");
    }

    in.close();
  }

  public boolean containsProtein(String species, String protein) {
    // Don't check the same species for self-orthologies
    for (String oSpecies: this.SPECIESES) {
      if (species.equals(oSpecies)) {
        continue;
      }

      if (this.orthoMapper.get(species).get(oSpecies).containsKey(protein)) {
        return true;
      }

    }
    return false;
  }

  /**
   * Checks whether two specified proteins are orthologs, by Ensembl Ids.
   *
   * @param species1
   * @param protein1
   * @param species2
   * @param protein2
   * @return
   */
  public boolean areOrthologs(String species1, String protein1, String species2, String protein2) {
    return this.orthoMapper.get(species1).get(species2).get(protein1).containsKey(protein2);
  }

  /**
   * Manually adds an orthology to this Orthologies object. Bi-directional. Should ONLY be used when
   * necessary for manual data editing!
   *
   * @param species1
   * @param protein1
   * @param species2
   * @param protein2
   */
  public void addOrtholog(String species1, String protein1, String species2, String protein2) {
    this.orthoMapper.get(species1).putIfAbsent(species2, new HashMap<String, HashMap<String, HashSet<String>>>());
    this.orthoMapper.get(species1).get(species2).putIfAbsent(protein1, new HashMap<String, HashSet<String>>());
    this.orthoMapper.get(species1).get(species2).get(protein1).putIfAbsent(protein2, new HashSet<String>());
    if (this.orthoMapper.get(species1).get(species2).get(protein1).get(protein2).size() == 0) {
      this.orthoMapper.get(species1).get(species2).get(protein1).get(protein2).add("Manual");
    }
    this.orthoMapper.get(species2).putIfAbsent(species1, new HashMap<String, HashMap<String, HashSet<String>>>());
    this.orthoMapper.get(species2).get(species1).putIfAbsent(protein2, new HashMap<String, HashSet<String>>());
    this.orthoMapper.get(species2).get(species1).get(protein2).putIfAbsent(protein1, new HashSet<String>());
    if (this.orthoMapper.get(species2).get(species1).get(protein2).get(protein1).size() == 0) {
      this.orthoMapper.get(species2).get(species1).get(protein2).get(protein1).add("Manual");
    }
  }

  /**
   * Checks whether the specified protein has an ortholog in another species, by proteinID
   *
   * @param species1
   * @param proteinId1
   * @param species2
   * @return
   */
  public boolean hasOrthologInSpecies(String species1, String proteinId1, String species2) {
    return this.orthoMapper.get(species1).get(species2).containsKey(proteinId1);
  }

  /**
   * Checkes whether the specified protein has orthologs in all (other) species
   *
   * @param species
   * @param proteinId
   * @return
   */
  public boolean hasOrthologInAllSpecies(String species, String proteinId) {
    for (String species2: this.SPECIESES) {
      // Don't check the same species for self-orthologies
      if (species.equals(species2)) {
        continue;
      }

      if (!this.hasOrthologInSpecies(species, proteinId, species2)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a complex data object with all the orthologs of the specified protein
   *
   * @param species
   * @param protein
   * @return - a mapping of all orthologs of the specified protein, grouped by species
   */
  public HashMap<String, HashSet<String>> getOrthologs(String species, String protein) {
    HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();

    for (String species2: this.SPECIESES) {
      // Don't check the same species for self-orthologies
      if (species.equals(species2)) {
        continue;
      }
      if (this.hasOrthologInSpecies(species, protein, species2)) {
        result.put(species2, new HashSet<String>());
        result.get(species2).addAll(this.orthoMapper.get(species).get(species2).get(protein).keySet());
      }
    }

    return result;
  }

  /**
   * Get the species in this orthologies object, in order. Not backed by original data structure
   *
   * @return
   */
  public List<String> getSpecies() {
    return new ArrayList<String>(this.SPECIESES);

  }

  /**
   * Retrieves all the orthologs for the specified protein from the specified species
   *
   * @param species1 - abbreviated scientific name of the species for the specified protein
   * @param protein1 - ID of the protein whose orthologs are to be found
   * @param species2 - abbreviated scientific name of the species in which orthologs are to be found
   * @return
   */
  public HashSet<String> getOrthologsFromSpecies(String species1, String protein1, String species2) {
    HashSet<String> result = new HashSet<String>();
    if (!this.hasOrthologInSpecies(species1, protein1, species2)) {
      return result;
    }

    result.addAll(this.orthoMapper.get(species1).get(species2).get(protein1).keySet());

    return result;
  }

  /**
   * Retrieves all the orthologs for the specified protein from the specified species. Use default
   * names2ids Mappings.
   *
   * @param species1 - abbreviated scientific name of the species for the specified protein
   * @param protein1 - name of the protein whose orthologs are to be found
   * @param species2 - abbreviated scientific name of the species in which orthologs are to be found
   * @return - the names of the proteins in species2 orthologous to species1's protein1
   * @throws Exception
   */
  public HashSet<String> getOrthologsFromSpeciesByName(String species1, String protein1, String species2)
      throws Exception {

    Mapping names2ids1 = Mapping.getNames2Ids(species1);
    Mapping names2ids2 = Mapping.getNames2Ids(species2);

    return this.getOrthologsFromSpeciesByName(species1, protein1, species2, names2ids1, names2ids2);
  }

  /**
   * Retrieves all the orthologs for the specified protein from the specified species
   *
   * @param species1 - abbreviated scientific name of the species for the specified protein
   * @param protein1 - name of the protein whose orthologs are to be found
   * @param species2 - abbreviated scientific name of the species in which orthologs are to be found
   * @param names2ids1 - mapping of protein names to protein ids in species1
   * @param names2ids2 - mapping of protein names to protein ids in species2
   * @return - a HashSet of protein names, or an empty HashSet if no orthologs exist
   * @throws Exception
   */
  public HashSet<String> getOrthologsFromSpeciesByName(String species1, String protein1, String species2,
      Mapping names2ids1, Mapping names2ids2) throws Exception {
    HashSet<String> orthologousIds = new HashSet<String>();

    HashSet<String> proteinIds1 = names2ids1.getForward(protein1);
    for (String proteinId1: proteinIds1) {
      if (this.hasOrthologInSpecies(species1, proteinId1, species2)) {
        orthologousIds.addAll(this.orthoMapper.get(species1).get(species2).get(proteinId1).keySet());
      }
    }

    HashSet<String> orthologs = new HashSet<String>();
    names2ids2.remapDiscardOriginal(orthologousIds, orthologs, 'b');

    return orthologs;
  }

  /**
   * Get the proteins in this Orthologies object. Useful as a proteinFilter.
   *
   * @return - an unlinked mapping of species -> proteinIds
   */
  public HashMap<String, HashSet<String>> getProteins() {
    HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();

    for (String species1: this.orthoMapper.keySet()) {
      result.put(species1, new HashSet<String>());

      for (String species2: this.orthoMapper.get(species1).keySet()) {
        result.get(species1).addAll(this.orthoMapper.get(species1).get(species2).keySet());
      }
    }

    return result;
  }

  /**
   * Retrieves all the proteins from the specified species that have orthologs in this data
   * structure
   *
   * @param species
   * @return
   */
  public HashSet<String> getProteinsWithOrthologs(String species) {
    HashSet<String> result = new HashSet<String>();

    for (String species2: this.SPECIESES) {
      // Don't check the same species for self-orthologies
      if (species.equals(species2)) {
        continue;
      }

      result.addAll(this.orthoMapper.get(species).get(species2).keySet());
    }

    return result;
  }

  /**
   * Get the origin sources for the orthology between two proteins
   *
   * @param species1
   * @param protein1
   * @param species2
   * @param protein2
   * @return
   */
  public HashSet<String> getSources(String species1, String protein1, String species2, String protein2) {
    return new HashSet<String>(this.orthoMapper.get(species1).get(species2).get(protein1).get(protein2));
  }

/*  public static void main(String[] args) throws Exception {
    String[] specieses = {"D.melanogaster","H.sapiens"};
    Orthologies test = new Orthologies(specieses);
    System.out.println(test.getOrthologsFromSpeciesByName("H.sapiens", "ITSN1", "D.melanogaster"));
  }*/

}
