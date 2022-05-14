package data;


import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import data.Node;
import data.PairOfStrings;
import functions.Static;
import processing.Networks;

/**
 * Data object to collect and store information about all the interologs between two networks.
 * @author Brian
 *
 */
public class Interolog {

  private static HashMap<String, HashMap<String, Interolog>> interologSingleton ;

  /**
   * Accessor to singleton pattern container for interolog data between from one species to the other, using default networks.
   * Interolog data structure is unidirectional!
   * @param species1
   * @param species2
   * @return
   * @throws Exception
   */
  public static Interolog getInterologs(String species1, String species2) throws Exception {
    if (interologSingleton == null) {
      interologSingleton = new HashMap<String, HashMap<String, Interolog>>();
    }
    if (interologSingleton.get(species1) == null) {
      interologSingleton.put(species1, new HashMap<String, Interolog>());
    }
    if (interologSingleton.get(species2) == null) {
      interologSingleton.put(species2, new HashMap<String, Interolog>());
    }
    if (interologSingleton.get(species1).get(species2) == null) {
      interologSingleton.get(species1).put(species2, Interolog.findInterologs(species1, species2));
    }
    return interologSingleton.get(species1).get(species2);
  }

  public HashMap<PairOfStrings, HashSet<PairOfStrings>> interologs; // Maps edges in network1 to their interologs in network2
  public HashMap<PairOfStrings, HashSet<PairOfStrings>> notInterologs; // Maps edges in network1 to their non-interacting orthologs in network2


  public Interolog() {
    this.interologs = new HashMap<PairOfStrings, HashSet<PairOfStrings>>();
    this.notInterologs = new HashMap<PairOfStrings, HashSet<PairOfStrings>>();
  }

  /**
   * Gathers the interolog data from between the PPINs of two species. Uses default networks loaded from file.
   * For custom networks, use findInterologs(Network, Network).
   *
   * Retrieves orthology data from Ortholog.getOrthologies(), using the species attributes of the two networks.
   * @param network1 - the first PPIN.
   * @param network2 - the second PPIN.
   * @return
   * @throws Exception
   */
  public static Interolog findInterologs(String species1, String species2) throws Exception {
    Network network1 = Networks.getNetwork(species1);
    Network network2 = Networks.getNetwork(species2);

    return Interolog.findInterologs(network1, network2);
  }

  /**
   * Gathers the interolog data from between two given PPINs. (PPIN node names should their protein names.)
   *
   * Retrieves orthology data from Ortholog.getOrthologies(), using the species attributes of the two networks.
   * @param network1 - the first PPIN.
   * @param network2 - the second PPIN.
   * @return
   * @throws Exception
   */
  public static Interolog findInterologs(Network network1, Network network2) throws Exception {
    Interolog result = new Interolog();

    String species1 = network1.getSpecies();
    String species2 = network2.getSpecies();

    Orthologies orthologies = Orthologies.getOrthologies(species1, species2);

    Static.debugOutput("Interolog.findInterologs() working on " + network1.getName() + " & " + network2.getName() + ". (" + new Date().toString() + ").");

    HashSet<String> networkNames2 = Static.namesFromNodes(network2.getNodes());

    // Loop through all nodes in the first species with orthologs in the second species
    for (Node node1: network1.getNodes()) {
      String protein1 = node1.name;

      // Get all the orthologs of the current protein that exist in network2.
      HashSet<String> orthoProteins1 = orthologies.getOrthologsFromSpeciesByName(species1, protein1, species2);
      orthoProteins1.retainAll(networkNames2);

      // Move onto next protein if no such orthologs exist
      if (orthoProteins1.size() == 0) {
        continue;
      }

      // Loop through all neighbours with orthologs in the second species
      for (Node node2: network1.getAdjacent(node1)) {
        String protein2 = node2.name;

        // Alphabetical order to avoid double-counting interactions.
        if (protein1.compareTo(protein2) >= 0) {
          continue;
        }

        // Get all the orthologs of the current neighbour that exist in network2.
        HashSet<String> orthoProteins2 = orthologies.getOrthologsFromSpeciesByName(species1, protein2, species2);
        orthoProteins2.retainAll(networkNames2);

        // Move onto next protein if no such orthologs exist.
        if (orthoProteins2.size() == 0) {
          continue;
        }

        // This is an edge that could be an interolog
        PairOfStrings firstPair = new PairOfStrings(protein1, protein2);

        // Loop through interolog partners on the other side
        for (String orthoProtein1: orthoProteins1) {
          for (String orthoProtein2: orthoProteins2) {
            // This is the other half of the interolog
            PairOfStrings secondPair = new PairOfStrings(orthoProtein1, orthoProtein2);

            // Reorder edge pair to ensure they're in alphabetical order. Unnecessary?
            if (orthoProtein1.compareTo(orthoProtein2) > 0) {
              secondPair = new PairOfStrings(orthoProtein2, orthoProtein1);
            }

            // Check to see if other half are actually adjacent (true interolog)
            if (network2.areAdjacent(orthoProtein1, orthoProtein2)) {
              result.interologs.putIfAbsent(firstPair, new HashSet<PairOfStrings>());
              result.interologs.get(firstPair).add(secondPair);
            }
            else {
              result.notInterologs.putIfAbsent(firstPair, new HashSet<PairOfStrings>());
              result.notInterologs.get(firstPair).add(secondPair);
            }
          }
        }
      }
    }

    Static.debugOutput("Interolog.findInterologs() finishing on " + network1.getName() + " & " + network2.getName() + ". (" + new Date().toString() + ").");
    return result;
  }

  public static void main(String[] args) throws Exception {
    Static.debug = true;

    Interolog test = Interolog.findInterologs("C.elegans", "D.melanogaster");
    System.out.println(test.interologs);
    System.out.println(test.notInterologs);

  }
}
