package processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.Fasta;
import data.Mapping;
import data.Network;
import data.NetworkAdjList;
import data.NetworkAdjMat;
import data.Node;
import data.Sequence;
import functions.Constants;
import functions.Static;


public class Networks {

  // species -> type ("pred", "exp", "") -> Network
  private static HashMap<String, HashMap<String, Network>> networks = new HashMap<String, HashMap<String, Network>>();

  /**
   * Static method to retrieve the specified PPIN from file. Singleton design pattern.
   *
   * @param species - the species for which the PPIN is needed
   * @return - the PPIN of the specified species
   * @throws Exception
   */
  public static Network getNetwork(String species) throws Exception {
    return Networks.getNetwork(species, "", Constants.NETWORK_STRUCTURE);
  }

  /**
   * Static method to retrieve the specified PPIN from file, and returns a network of the specified form.
   *
   * @param species - the species for which the PPIN is needed
   * @param type - the type of PPIN being fetched, typically "pred" for predicted, "exp" for experimental, or "" for all interactions
   * @param form - the type of network to be returned, either "mat" for adjacency matrix or "list" for adjacency list.
   * @return - the PPIN of the specified species
   * @throws Exception
   */
  public static Network getNetwork(String species, String type, String form) throws Exception {
    if (form.equals("list")) {
      return Networks.getNetworkAdjList(species, type);
    }
    else if (form.equals("mat")) {
      return Networks.getNetworkAdjMat(species, type);
    }
    throw new Exception("Type parameter must be \"list\" or \"mat\".");
  }

  /**
   * Static method to retrieve the specified PPIN from file, and returns a network in adjacency list form.
   * Singleton design pattern.
   *
   * @param species - the species for which the PPIN is needed
   * @param type - the type of PPIN being fetched, typically "pred" for predicted, "exp" for experimental, or "" for all interactions
   * @return - the PPIN of the specified species
   * @throws Exception
   */
  public static Network getNetworkAdjList(String species, String type) throws Exception {
    if (!Networks.networks.containsKey(species) && Networks.networks.get(species).containsKey(type)) {

      Networks.networks.putIfAbsent(species, new HashMap<String, Network>());
      if (species.startsWith("test")) {
        Network network = new NetworkAdjList("testbed/network" + species.substring(4) + ".txt", species);
        Networks.networks.get(species).put("test", network);
      }
      else {
        Network network = new NetworkAdjList("Data/" + species + "/" + species + ".ppin." + Constants.NETWORK_SOURCE + "." + Constants.FILE_VERSION + (type==""?type:"." + type) + ".txt", species);
        Networks.networks.get(species).put(type, network);
      }
    }

    if (!(Networks.networks.get(species).get(type) instanceof NetworkAdjList)) {
      return new NetworkAdjList(Networks.networks.get(species).get(type));
    }

    return Networks.networks.get(species).get(type);
  }

  /**
   * Static method to retrieve the specified PPIN from file, and returns a network in adjacency matrix form.
   * Singleton design pattern.
   *
   * @param species - the species for which the PPIN is needed
   * @param type - the type of PPIN being fetched, typically "pred" for predicted, "exp" for experimental, or "" for all interactions
   * @return - the PPIN of the specified species
   * @throws Exception
   */
  public static Network getNetworkAdjMat(String species, String type) throws Exception {
    if (!Networks.networks.containsKey(species) || !Networks.networks.get(species).containsKey(type)) {

      Networks.networks.putIfAbsent(species, new HashMap<String, Network>());
      if (species.startsWith("test")) {
        Network network = new NetworkAdjMat("testbed/network" + species.substring(4) + ".txt", species);
        Networks.networks.get(species).put("test", network);
      }
      else {
        String filename = "Data/" + species + "/" + species + ".ppin." + Constants.NETWORK_SOURCE + "." + Constants.FILE_VERSION + (type==""?type:"." + type) + ".txt";
        Network network = new NetworkAdjMat(filename, species);
        Networks.networks.get(species).put(type, network);
      }
    }

    if (!(Networks.networks.get(species).get(type) instanceof NetworkAdjMat)) {
      return new NetworkAdjMat(Networks.networks.get(species).get(type));
    }

    return Networks.networks.get(species).get(type);
  }


  /**
   * Reads in iRefIndex downloads and processes them to only include interactions within the species
   * of interest. Used to pre-process iRefIndex downloads prior to network creation.
   *
   * @param inputFile - Filename for the raw MITAB download of IRefIndex
   * @param outputFile - Output file for IRefIndex interactions
   * @param taxid - TaxID of the species of interest
   * @throws IOException
   */
  public static void keepIRefLinesMatching(String inputFile, String outputFile, String taxid) throws IOException {
    String line = "";

    BufferedReader in = new BufferedReader(new FileReader(inputFile));
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

    // Header line
    // out.write(in.readLine() + "\n");

    while ((line = Static.skipCommentLines(in)) != null) {
      String[] split = line.split("\t");

      if (split[10].equalsIgnoreCase(taxid) && (split[9].equalsIgnoreCase(taxid) || split[9].equalsIgnoreCase("-") || split[9].equalsIgnoreCase("taxid:0(-)"))) {
        // out.write(line + "\n");
      }
      else {
        System.out.println(split[9] + "\t" + split[10]);
      }
    }

    in.close();


    out.close();
  }

  /**
   * Helper function to collect all the interaction description terms that descend from physical
   * interactions in the MI ontology.
   *
   * Uses helper method getMIDescendants()
   *
   * Input file: mi.owl.txt
   *
   * @param predicted - indicates whether predicted interactions should be included or not
   * @return - a set of all the MI:XXXX terms that represent physical interactions
   * @throws IOException
   */
  private static MIStruct gatherPhysicalInteractionTerms(boolean predicted) throws IOException {
    HashSet<String> terms = new HashSet<String>();
    terms.add("MI:0218"); // Physical interaction
    terms.add("MI:0915"); // Physical association
    if (predicted) {
      terms.add("MI:1110");
    }

    return Networks.getMIDescendants(terms);
  }

  /**
   * Helper function to collect all the interaction detection method terms that represent predicted interactions
   * in the MI ontology.
   *
   * Uses helper method getMIDescendants()
   *
   * Input file: mi.owl.txt
   *
   * @param unspecified - whether interactions with unspecified detection methods should also be counted as predicted
   * @return - a set of all the MI:XXXX terms that represent predicted protein-protein interactions
   * @throws IOException
   */
  private static MIStruct gatherPredictedInteractionTerms(boolean unspecified) throws IOException {
    HashSet<String> terms = new HashSet<String>();

    terms.add("MI:0362"); // Inference
    terms.add("MI:0063"); // Interaction Prediction

    MIStruct result = Networks.getMIDescendants(terms);

    if (unspecified) {
      result.descendants.add("MI:0686"); // Unspecified method
      result.descendants.add("MI:0001"); // Interaction detection method
      result.descendants.add("-");
    }

    return result;
  }

  /**
   * Helper function to collect all the interaction detection method terms that represent experimentally validated interactions
   * in the MI ontology.
   *
   * Uses helper method getMIDescendants()
   *
   * Input file: mi.owl.txt
   *
   * @param unspecified - whether interactions with unspecified detection methods should also be counted as predicted
   * @return - a set of all the MI:XXXX terms that represent validated protein-protein interactions
   * @throws IOException
   */
  private static MIStruct gatherValidatedInteractionTerms(boolean unspecified) throws IOException {
    HashSet<String> terms = new HashSet<String>();
    terms.add("MI:0045"); // Experimental Interaction Detection

    MIStruct result = Networks.getMIDescendants(terms);

    if (unspecified) {
      result.descendants.add("MI:0686"); // Unspecified method
      result.descendants.add("MI:0001"); // Interaction detection method
      result.descendants.add("-");
    }

    return result;
  }

  /**
   * Helper function to collect all the interaction detection method terms in the MI ontology.
   *
   * Uses helper method getMIDescendants()
   *
   * Input file: mi.owl.txt
   *
   * @param unspecified - whether interactions with unspecified detection methods should also be counted as predicted
   * @return - a set of all the MI:XXXX terms that represent physical interactions
   * @throws IOException
   */
  private static MIStruct gatherDetectedInteractionTerms(boolean unspecified) throws IOException {
    MIStruct result = Networks.gatherPredictedInteractionTerms(false);
    result.descendants.addAll(Networks.gatherValidatedInteractionTerms(false).descendants);

    if (unspecified) {
      result.descendants.add("MI:0686"); // Unspecified method
      result.descendants.add("MI:0001"); // Interaction detection method
      result.descendants.add("-");
    }

    return result;
  }

  private static class MIStruct {
    HashSet<String> descendants = new HashSet<String>();
    HashMap<String, String> names = new HashMap<String, String>();
  }


  /**
   * Parse a molecular interactions ontology file, and get all the descendents of the given terms
   * @param starterTerms - a set of MI terms for which all descendants are to be found
   * @return
   * @throws IOException
   */
  private static MIStruct getMIDescendants(HashSet<String> starterTerms) throws IOException {
    HashMap<String, HashSet<String>> isA = new HashMap<String, HashSet<String>>();

    MIStruct result = new MIStruct();
    result.descendants.addAll(starterTerms);

    // Read in the MI ontology.
    BufferedReader in = new BufferedReader(new FileReader("data/mi.owl.txt"));
    String line = "";
    String newTerm = null;

    while ((line = in.readLine()) != null) {
      if (line.startsWith("id:")) {
        newTerm = line.trim().substring("id:".length()).trim();
      }
      else if (line.startsWith("name")) {
        result.names.put(newTerm, line.trim().substring("name:".length()).trim());
      }
      else if (line.startsWith("is_a: ")) {
        String parent = line.substring("is_a: ".length()).trim();
        parent = parent.substring(0, parent.indexOf("!") - 1);
        isA.putIfAbsent(newTerm, new HashSet<String>());
        isA.get(newTerm).add(parent);
      }
    }

    in.close();

    // Boolean to keep looping through the ontology until the custom result set remains unchanged through an entire loop
    boolean keepGoing = true;
    while (keepGoing) {
      keepGoing = false;
      ArrayList<String> remove = new ArrayList<String>();
      for (String term: isA.keySet()) {
        for (String parent: isA.get(term)) {
          if (result.descendants.contains(parent)) {
            result.descendants.add(term);
            remove.add(term);
            keepGoing = true;
          }
        }
      }

      // Remove all the newly found descendant terms from the remaining terms to be searched
      for (String term: remove) {
        isA.remove(term);
      }
    }

    return result;
  }

  private static String extractMICode(String iRefField) {
    int index = iRefField.lastIndexOf("MI:");
    if (index == -1) {
      return "-";
    }
    return iRefField.substring(index, index + 7);
  }

  /**
   * Reads in a network file downloaded from iRefIndex, and converts it into a Network object.
   * (Proteins are identified by name.)
   *
   * Input files: ???.pep.processed.##.fasta, ??.ensembl##.fasta, ??.irefindex.raw.txt
   *
   * @param species - the species for which the Network is being built
   * @param interactionTypes - the interactionTypes to be counted; usually generated from
   *        Networks.gatherPhysicalInteractionTerms()
   * @param experimentalDetectionTypes - the detection methods to NOT be counted; usually generated from
   *        Networks.gatherPredictedInteractionTerms()
   * @return
   * @throws Exception
   */
  public static NetworkAdjList convertIRefFile(String species, MIStruct interactionTypes, MIStruct experimentalDetectionTypes) throws Exception {
    // Create a Mapping object to map iRefIndex IDs to protein names.
    Mapping mapper = Mapping.masterNameMapping(species);
    // Mapping mapper = Mapping.masterEnsemblpMapping(species) // Can swap this in for Ensembl IDs
    // instead of protein names

    Fasta fasta = new Fasta("data/" + species + "/" + species + ".pep.processed." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);

    Mapping proteinChecker = Mapping.getNames2Ids(species);

    BufferedReader in = new BufferedReader(new FileReader("data/" + species + "/" + species + ".irefindex.raw.txt"));

    // Create an empty network
    NetworkAdjList network = new NetworkAdjList(species);
    // Data structure to temporarily store complex interactions
    HashMap<String, HashSet<String>> complexes = new HashMap<String, HashSet<String>>();

    // Statistic-counting variables
    int totalLines = 0;
    int missedLines = 0;
    int wrongTypeLines = 0;
    int predictedLines = 0;
    HashMap<String, Integer> missed = new HashMap<String, Integer>();

    String line = in.readLine(); // Skip header line


    HashMap<String, Integer> badDetectionMICount = new HashMap<String, Integer>();

    while ((line = Static.skipBlankCommentLines(in)) != null) {
      totalLines++;
      String[] split = line.split("\t");
      String idText1 = split[2].trim();
      String idText2 = split[3].trim();
      String aliasText1 = split[4];
      String aliasText2 = split[5];

      String detectionType = Networks.extractMICode(split[6]);
      String interactionType = Networks.extractMICode(split[11]);

      // No interaction type identified. Skip.
      if (interactionType.trim().equals("-")) {
        wrongTypeLines++;
        continue;
      }

      // If interaction type is not a desired type, skip.
      if (!interactionTypes.descendants.contains(interactionType)) {
        wrongTypeLines++;
        continue;
      }

      if (!experimentalDetectionTypes.descendants.contains(detectionType)) {
        badDetectionMICount.put(detectionType, badDetectionMICount.getOrDefault(detectionType, 0) + 1);
        predictedLines++;
        continue;
      }

      HashSet<String> proteins1 = null;
      HashSet<String> proteins2 = null;
      boolean complex = false;

      // Process the entry in column 1. If it's a complex:
      if (line.startsWith("complex")) {
        complexes.putIfAbsent(split[0], new HashSet<String>());
        complex = true;
      }
      // If column 1 entry is a protein:
      else {
        proteins1 = Networks.getProteinNameFromIRefText(idText1, aliasText1, mapper);

        // No matching Ensembl protein found
        if (proteins1 == null || proteins1.size() == 0) {
          missed.put(split[0], missed.getOrDefault(split[0], 0) + 1);
        }
        else {
          // QC for protein entries
          HashSet<String> proteins1a = new HashSet<String>(proteins1);
          HashMap<Set<String>, String> sequenceMap = new HashMap<Set<String>, String>();

          for (String protein1: proteins1a) {
            // Remove any "proteins" that aren't actually proteins by checking for a protein Id
            if (proteinChecker.getForward(protein1) == null) {
              proteins1.remove(protein1);
              continue;
            }
            if (!fasta.containsSequenceByName(protein1)) {
              proteins1.remove(protein1);
              continue;
            }

            Set<String> sequences = Sequence.extractSequences(fasta.getSequences(protein1));
            // Now ensure that protein IDs aren't duplicated (for when multiple genes encode the
            // same protein)
            if (sequenceMap.containsKey(sequences)) {
              if (protein1.compareToIgnoreCase(sequenceMap.get(sequences)) < 0) {
                proteins1.remove(sequenceMap.get(sequences));
                sequenceMap.put(sequences, protein1);
              }
              else {
                proteins1.remove(protein1);
              }
            }
            else {
              sequenceMap.put(sequences, protein1);
            }
          }
        }
      }

      // Process the entry in column 2
      proteins2 = Networks.getProteinNameFromIRefText(idText2, aliasText2, mapper);

      // No matching Ensembl protein found
      if (proteins2 == null || proteins2.size() == 0) {
        missed.put(split[1], missed.getOrDefault(split[1], 0) + 1);
      }
      else {
        // QC for protein entries
        HashSet<String> proteins2a = new HashSet<String>(proteins2);
        HashMap<Set<String>, String> sequenceMap = new HashMap<Set<String>, String>();

        for (String protein2: proteins2a) {
          // Remove any "proteins" that aren't actually proteins by checking for a protein Id
          if (proteinChecker.getForward(protein2) == null) {
            proteins2.remove(protein2);
            continue;
          }
          if (!fasta.containsSequenceByName(protein2)) {
            proteins2.remove(protein2);
            continue;
          }

          Set<String> sequences = Sequence.extractSequences(fasta.getSequences(protein2));
          // Now ensure that protein IDs aren't duplicated (for when multiple genes encode the same
          // protein)
          if (sequenceMap.containsKey(sequences)) {
            if (protein2.compareToIgnoreCase(sequenceMap.get(sequences)) < 0) {
              proteins2.remove(sequenceMap.get(sequences));
              sequenceMap.put(sequences, protein2);
            }
            else {
              proteins2.remove(protein2);
            }
          }
          else {
            sequenceMap.put(sequences, protein2);
          }
        }
      }

      // Upper-case everything
      HashSet<String> tempProteins = new HashSet<String>();
      if (proteins1 != null && proteins1.size() != 0) {
        for (String protein: proteins1) {
          tempProteins.add(protein.toUpperCase());
        }
        proteins1 = tempProteins;
        tempProteins = new HashSet<String>();
      }
      if (proteins2 != null && proteins2.size() != 0) {
        for (String protein: proteins2) {
          tempProteins.add(protein.toUpperCase());
        }
        proteins2 = tempProteins;
      }

      // Dealing with a complex. Add all related proteins to the complex for later processing.
      if (complex && (proteins2 != null && proteins2.size() != 0)) {
        for (String protein2: proteins2) {
          complexes.get(split[0]).add(protein2);
        }
      }
      // Not dealing with a complex, and matching names found for both proteins
      else if (proteins1 != null && proteins1.size() != 0 && proteins2 != null && proteins2.size() != 0) {
        // Add vertices to network if needed.
        for (String protein1: proteins1) {
          Node newNode = new Node(protein1, species);
          if (!network.containsNode(newNode)) {
            network.addNode(protein1, species);
          }
        }
        for (String protein2: proteins2) {
          Node newNode = new Node(protein2, species);
          if (!network.containsNode(newNode)) {
            network.addNode(protein2, species);
          }
        }
        // Add edges to network. Vertices guaranteed to exist in network.
        for (String protein1: proteins1) {
          for (String protein2: proteins2) {
            network.addEdge(protein1, species, protein2, species);
          }
        }
      }
      // Line is missed. Tally for statistics.
      else {
        missedLines++;
      }
    }

    // Process complexes.
    for (HashSet<String> complex: complexes.values()) {
      // Ignore complexes of size 1. (WTF irefindex??)
      if (complex.size() == 1) {
        continue;
      }
      ArrayList<String> complexProteins = new ArrayList<String>(complex);


      // Add every vertex in the complex to the network
      for (String protein1: complexProteins) {
        Node newNode = new Node(protein1, species);
        if (!network.containsNode(newNode)) {
          network.addNode(protein1, species);
        }
      }

      // Double-loop through vertices and add edges to network
      for (int i = 0; i < complexProteins.size(); i++) {
        String protein1 = complexProteins.get(i);
        for (int j = i + 1; j < complexProteins.size(); j++) {
          String protein2 = complexProteins.get(j);
          if (!protein1.equalsIgnoreCase(protein2)) {
            network.addEdge(protein1, species, protein2, species);
          }
        }
      }
    }

    in.close();

    String commentHeader =
        "! " + species + " PPIN, from iRefIndex, generated " + new Date().toString() + "\n" + "! " + network.getNumVertices() + " vertices, " + network.getNumEdges() + " edges" + "\n" + "! Missed " + missedLines + " ("
            + Static.fourDecimals.format((double) missedLines / (double) totalLines * 100) + "%)" + " Wrong type " + wrongTypeLines + " (" + Static.fourDecimals.format((double) wrongTypeLines / (double) totalLines * 100) + "%) "
            + "Wrong detection " + predictedLines + " (" + Static.fourDecimals.format((double) predictedLines / (double) totalLines * 100) + "%) "
            + "out of " + totalLines + " lines.\n";

    ArrayList<String> idOrder = new ArrayList<String>(missed.keySet());
    Collections.sort(idOrder);
    for (String id: idOrder) {
      commentHeader += "! " + id + " missed " + missed.get(id) + " times.\n";
    }

    network.setComment(commentHeader);

    ArrayList<String> miCodes = Static.setToOrderedList(badDetectionMICount.keySet());
    for (String miCode: miCodes) {
      Static.debugOutput(miCode + "\t" + experimentalDetectionTypes.names.get(miCode) + "\t" + badDetectionMICount.get(miCode));
    }

    return network;
  }

  /**
   * Helper method that takes text from (half) an iRefIndex interaction and tries to find the name(s) of said protein.
   * Ideally, should be used with a Mapper object that can process protein names, uniprit IDs, entrez gene IDs, and Refseq
   * IDs to protein names
   *
   * @param idText
   * @param aliasText
   * @param mapper
   * @return
   */
  private static HashSet<String> getProteinNameFromIRefText(String idText, String aliasText, Mapping mapper) {
    HashSet<String> result = new HashSet<String>();

    if (aliasText.contains("hgnc:")) {

      int startPos = aliasText.indexOf("hgnc:") + "hgnc:".length();
      int endPos = aliasText.indexOf("hgnc:") + "hgnc:".length();
      endPos = aliasText.indexOf("|", endPos);
      if (endPos == -1) {
        endPos = aliasText.length();
      }
      String proteinName = aliasText.substring(startPos, endPos);
      proteinName = proteinName.replaceAll("CELE_", ""); // Stupid kludge

      if (mapper.containsForward(proteinName.toUpperCase())) {
        result.add(proteinName);
      }

    }

    if (result == null || result.size() == 0) {
      HashSet<String> uniprotIds = new HashSet<String>();
      HashSet<String> entrezgeneIds = new HashSet<String>();
      HashSet<String> refseqIds = new HashSet<String>();

      String[] split = idText.split("\\|");
      for (String entry: split) {
        //System.out.println("Entry " + entry);
        if (entry.startsWith("uniprotkb:")) {
          uniprotIds.add(entry.split(":")[1]);
        }
        if (entry.startsWith("entrezgene/locuslink:")) {
          entrezgeneIds.add(entry.split(":")[1]);
        }
        if (entry.startsWith("refseq:")) {
          refseqIds.add(entry.split(":")[1]);
        }
      }

      //System.out.println(uniprotIds + "\t" + entrezgeneIds + "\t" + refseqIds);

      if (uniprotIds.size() != 0) {
        for (String uniprotId: uniprotIds) {
          if (uniprotId.contains("-")) {
            uniprotId = uniprotId.substring(0, uniprotId.indexOf('-'));
          }

          HashSet<String> temp = mapper.getBackward(uniprotId);
          if (temp != null) {
            result.addAll(temp);
          }
        }
      }

      if (result.size() == 0 && entrezgeneIds.size() != 0) {
        for (String entrezgeneId: entrezgeneIds) {
          HashSet<String> temp = mapper.getBackward(entrezgeneId);
          if (temp != null) {
            result.addAll(temp);
          }
        }
      }

      if (result.size() == 0 && refseqIds.size() != 0) {
        for (String refseqId: refseqIds) {
          HashSet<String> temp = mapper.getBackward(refseqId);
          if (temp != null) {
            result.addAll(temp);
          }
        }
      }
    }

    return Static.stringsToUpperCase(result);
  }

  public static NetworkAdjList convertBioGridFile(String species, HashSet<String> interactionTypes, HashSet<String> detectionMethods) throws Exception {
    // Create a Mapping object to map EntrezGene #s to protein names.
    Mapping mapper = new Mapping(species + "/" + species + ".ensembl2entrezgene.ensembl" + Constants.FILE_VERSION + ".txt", "\t", 0, 4);

    Fasta fasta = new Fasta(species + "/" + species + ".pep.processed." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);

    Mapping proteinChecker = new Mapping(species + "/" + species + ".ensembl" + Constants.FILE_VERSION + ".txt", "\t", 0, 3);

    BufferedReader in = new BufferedReader(new FileReader(species + "/" + species + ".biogrid.3.4.150.txt"));

    NetworkAdjList network = new NetworkAdjList(species);

    // Statistic-counting variables
    int totalLines = 0;
    int missedLines = 0;
    int wrongTypeLines = 0;
    int predictedLines = 0;
    HashMap<String, Integer> missed = new HashMap<String, Integer>();

    String line = in.readLine(); // Skip header line

    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");

      // Only consider intra-species interactions
      if (!split[9].equals(split[10])) {
        continue;
      }

      String entrez1 = split[0].split(":")[1];
      String entrez2 = split[1].split(":")[1];
      String interactionType = Networks.extractMICode(split[11]);
      String detectionType = Networks.extractMICode(split[6]);

      totalLines++;

      // No interaction type identified. Skip.
      if (interactionType.trim().equals("-")) {
        //wrongTypeLines++;
        continue;
      }

      // If interaction type is not a desired type, skip.
      if (!interactionTypes.contains(interactionType)) {
        wrongTypeLines++;
        continue;
      }

      if (detectionMethods.contains(detectionType)) {
        predictedLines++;
        continue;
      }


      HashSet<String> proteins1 = mapper.getBackward(entrez1);

      // No matching Ensembl protein found
      if (proteins1 == null || proteins1.size() == 0) {
        missed.put(split[0], missed.getOrDefault(split[0], 0) + 1);
      }
      else {
        // QC for protein entries
        for (String protein1: new HashSet<String>(proteins1)) {
          HashMap<Set<String>, String> sequenceMap = new HashMap<Set<String>, String>();

          // Remove any "proteins" that aren't actually proteins by checking for a protein Id and sequence
          if (proteinChecker.getForward(protein1) == null) {
            proteins1.remove(protein1);
            continue;
          }
          if (!fasta.containsSequenceByName(protein1)) {
            proteins1.remove(protein1);
            continue;
          }

          // Now ensure that protein aren't duplicated (for when multiple genes encode the same protein)
          // by comparing sequences
          Set<String> sequences = Sequence.extractSequences(fasta.getSequences(protein1));

          if (sequenceMap.containsKey(sequences)) {
            if (protein1.compareToIgnoreCase(sequenceMap.get(sequences)) < 0) {
              proteins1.remove(sequenceMap.get(sequences));
              sequenceMap.put(sequences, protein1);
            }
            else {
              proteins1.remove(protein1);
            }
          }
          else {
            sequenceMap.put(sequences, protein1);
          }
        }
      }

      // Process the entry in column 2
      HashSet<String> proteins2 = mapper.getBackward(entrez2);

      // No matching Ensembl protein found
      if (proteins2 == null || proteins2.size() == 0) {
        missed.put(split[1], missed.getOrDefault(split[1], 0) + 1);
      }
      else {
        // QC for protein entries
        for (String protein2: new HashSet<String>(proteins2)) {
          HashMap<Set<String>, String> sequenceMap = new HashMap<Set<String>, String>();

          // Remove any "proteins" that aren't actually proteins by checking for a protein Id
          if (proteinChecker.getForward(protein2) == null) {
            proteins2.remove(protein2);
            continue;
          }
          if (!fasta.containsSequenceByName(protein2)) {
            proteins2.remove(protein2);
            continue;
          }

          Set<String> sequences = Sequence.extractSequences(fasta.getSequences(protein2));
          // Now ensure that protein IDs aren't duplicated (for when multiple genes encode the same
          // protein)
          if (sequenceMap.containsKey(sequences)) {
            if (protein2.compareToIgnoreCase(sequenceMap.get(sequences)) < 0) {
              proteins2.remove(sequenceMap.get(sequences));
              sequenceMap.put(sequences, protein2);
            }
            else {
              proteins2.remove(protein2);
            }
          }
          else {
            sequenceMap.put(sequences, protein2);
          }
        }
      }

      // Upper-case everything
      HashSet<String> tempProteins = new HashSet<String>();
      if (proteins1 != null && proteins1.size() != 0) {
        for (String protein: proteins1) {
          tempProteins.add(protein.toUpperCase());
        }
        proteins1 = tempProteins;
        tempProteins = new HashSet<String>();
      }
      if (proteins2 != null && proteins2.size() != 0) {
        for (String protein: proteins2) {
          tempProteins.add(protein.toUpperCase());
        }
        proteins2 = tempProteins;
      }

      if (proteins1 != null && proteins1.size() != 0 && proteins2 != null && proteins2.size() != 0) {
        // Add vertices to network if needed.
        for (String protein1: proteins1) {
          Node newNode = new Node(protein1, species);
          if (!network.containsNode(newNode)) {
            network.addNode(protein1, species);
          }
        }

        for (String protein2: proteins2) {
          Node newNode = new Node(protein2, species);
          if (!network.containsNode(newNode)) {
            network.addNode(protein2, species);
          }
        }

        // Add edges to network. Vertices guaranteed to exist in network.
        for (String protein1: proteins1) {
          for (String protein2: proteins2) {
            network.addEdge(protein1, species, protein2, species);
          }
        }
      }
      // Line is missed. Tally for statistics.
      else {
        missedLines++;
      }
    }
    in.close();

    String commentHeader =
        "! " + species + " PPIN, from Biogrid, generated " + new Date().toString() + "\n" + "! " + network.getNumVertices() + " vertices, " + network.getNumEdges() + " edges" + "\n" + "! Missed " + missedLines + " ("
            + Static.fourDecimals.format((double) missedLines / (double) totalLines * 100) + "%);" + " Wrong type " + wrongTypeLines + " (" + Static.fourDecimals.format((double) wrongTypeLines / (double) totalLines * 100) + "%); "
            + "Predicted " + predictedLines + " (" + Static.fourDecimals.format((double) predictedLines / (double) totalLines * 100) + "%) "
            + " out of " + totalLines + " lines.\n";

    ArrayList<String> idOrder = new ArrayList<String>(missed.keySet());
    Collections.sort(idOrder);
    for (String id: idOrder) {
      commentHeader += "! " + id + " missed " + missed.get(id) + " times.\n";
    }

    network.setComment(commentHeader);

    return network;
  }


/*  public static void main(String[] args) throws Exception {
    Static.debug = true;

    //String[] specieses = Constants.SPECIESES2;
    String[] specieses = {"H.sapiens"};

    // Networks.missingInterologAnalysis("S.cerevisiae", "C.elegans", null, "");

    *//** Write PPIN files **//*
    *//** From iRefIndex **//*

    MIStruct interactionTypes = Networks.gatherPhysicalInteractionTerms(true);
    MIStruct allDetectionTypes = Networks.gatherDetectedInteractionTerms(true);
    MIStruct experimentalDetectionTypes = Networks.gatherValidatedInteractionTerms(false);

    Static.debugOutput(Static.setToOrderedList(allDetectionTypes.descendants).toString());

    for (String species: specieses) {
      System.out.println(species);
      Network test;
      test = Networks.convertIRefFile(species, interactionTypes, experimentalDetectionTypes);
    }
  }*/
}
