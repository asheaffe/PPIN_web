package functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import data.Fasta;
import data.Mapping;
import data.NetworkAdjList;
import data.Orthologies;
import functions.Constants;
import functions.Static;
import processing.Networks;

public class HumanSh3Data {

  /**
   * Processes SH3 binding data.
   *
   * Input files: H.sapiens/H.sapiens.ensembl2uniprot.ensembl.???.txt, H.sapiens/H.sapiens.pep.processed.fasta, H.sapiens/OUT/*.res,
   *              S.cerevisiae/Raw_Yeast_Datatable.txt, C.elegans/wormY2H-SH3network-BrianLaw.txt
   * Output files:   sh3/???.network.txt, sh3/???.domains.txt
   *
   * @throws Exception
   */
  public static void processHumanSH3Data() throws Exception {
    Mapping names2uniprot = new Mapping("H.sapiens/H.sapiens.ensembl2uniprot.ensembl" + Constants.FILE_VERSION + ".txt", "\t", 0, 4);
    names2uniprot.addMapping(new Mapping("H.sapiens/H.sapiens.ensembl2uniprot.ensembl" + Constants.FILE_VERSION + ".txt", "\t", 0, 5));
    Fasta fasta = new Fasta("H.sapiens/H.sapiens.pep.processed." + Constants.FILE_VERSION + ".fasta", "[ \t]+", 0, "[ \t]+", 1, "[ \t]+", 2);

    // Network output
    NetworkAdjList humanNetwork = new NetworkAdjList("H.sapiens");
    // Text output
    StringBuffer output = new StringBuffer();
    HashMap<String, HashMap<String, ArrayList<String>>> connections = new HashMap<String, HashMap<String, ArrayList<String>>>();

    File directory = new File("sh3/OUT");
    File files[] = directory.listFiles();
    for (File f: files) {
      BufferedReader in = new BufferedReader(new FileReader(f.getAbsoluteFile()));
      String domainNumber = f.getName().substring(f.getName().indexOf('-') + 1, f.getName().indexOf('_'));
      String line = in.readLine(); // Skip initial header line

      while ((line = Static.skipBlankCommentLines(in)) != null) {
        String[] split = line.split("\t");
        String uniprot1 = split[0];
        String uniprot2 = split[1];
        double probability = Double.parseDouble(split[7]);

        Set<String> names1 = names2uniprot.getBackward(uniprot1);
        Set<String> names2 = names2uniprot.getBackward(uniprot2);

        if (names1 == null || names2 == null) {
          continue;
        }

        for (String protein1: names1) {
          if (!fasta.containsSequenceByName(protein1)) {
            continue;
          }
          if (!humanNetwork.containsNode(protein1)) {
            humanNetwork.addNode(protein1);
          }
          connections.putIfAbsent(protein1, new HashMap<String, ArrayList<String>>());
          connections.get(protein1).putIfAbsent(domainNumber, new ArrayList<String>());

          for (String protein2: names2) {
            if (!fasta.sequenceNameOrder.contains(protein2) || probability < 0.95) {
              // if (!fasta.sequenceNames.contains(protein2) ||
              // !realHumanNetwork.areAdjacent(protein1, protein2)) {
              continue;
            }
            if (!humanNetwork.containsNode(protein2)) {
              humanNetwork.addNode(protein2);
            }
            humanNetwork.addEdge(protein1, protein2);
            connections.get(protein1).get(domainNumber).add(protein2);
          }
        }
      }

      in.close();
    }

    // Post-processing to remove empty domains and empty proteins
    ArrayList<String> proteins = new ArrayList<String>(connections.keySet());
    for (String protein1: proteins) {
      ArrayList<String> domains = new ArrayList<String>(connections.get(protein1).keySet());
      for (String domainNumber: domains) {
        if (connections.get(protein1).get(domainNumber).size() == 0) {
          connections.get(protein1).remove(domainNumber);
        }
      }
      if (connections.get(protein1).size() == 0) {
        connections.remove(protein1);
      }
    }

    humanNetwork.writeToFile("sh3/H.sapiens.network.txt");
    ArrayList<String> proteinOrder = new ArrayList<String>(connections.keySet());
    Collections.sort(proteinOrder);
    for (String protein1: proteinOrder) {
      output.append("! " + protein1 + "\n");
      ArrayList<String> domainOrder = new ArrayList<String>(connections.get(protein1).keySet());
      Collections.sort(domainOrder);
      for (String domainNumber: domainOrder) {
        String outputLine = protein1 + "-" + domainNumber + "\t";
        ArrayList<String> neighbourOrder = connections.get(protein1).get(domainNumber);
        Collections.sort(neighbourOrder);
        for (String protein2: neighbourOrder) {
          outputLine = outputLine + protein2 + ", ";
        }
        outputLine = outputLine.substring(0, outputLine.length() - 2) + "\n";
        output.append(outputLine);
      }


    }

    Static.writeOutputToFile("sh3/H.sapiens.domains.txt", output);

    // Network output
    NetworkAdjList yeastNetwork = new NetworkAdjList("S.cerevisiae");
    // Text output
    output = new StringBuffer();
    connections = new HashMap<String, HashMap<String, ArrayList<String>>>();

    BufferedReader in = new BufferedReader(new FileReader("sh3/Raw_Yeast_Datatable.txt"));
    String line = "";

    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split(" ");
      String protein1 = split[0];
      String domainNumber = "1";
      if (protein1.contains("-")) {
        protein1 = split[0].substring(0, split[0].indexOf('-'));
        domainNumber = split[0].substring(split[0].indexOf('-') + 1);
      }
      String protein2 = split[1];

      if (!yeastNetwork.containsNode(protein1)) {
        yeastNetwork.addNode(protein1);
      }
      connections.putIfAbsent(protein1, new HashMap<String, ArrayList<String>>());
      connections.get(protein1).putIfAbsent(domainNumber, new ArrayList<String>());

      if (!yeastNetwork.containsNode(protein2)) {
        yeastNetwork.addNode(protein2);
      }
      yeastNetwork.addEdge(protein1, protein2);
      connections.get(protein1).get(domainNumber).add(protein2);
    }

    in.close();

    // Post-processing to remove empty domains and empty proteins
    proteins = new ArrayList<String>(connections.keySet());
    for (String protein1: proteins) {
      ArrayList<String> domains = new ArrayList<String>(connections.get(protein1).keySet());
      for (String domainNumber: domains) {
        if (connections.get(protein1).get(domainNumber).size() == 0) {
          connections.get(protein1).remove(domainNumber);
        }
      }
      if (connections.get(protein1).size() == 0) {
        connections.remove(protein1);
      }
    }

    yeastNetwork.writeToFile("sh3/S.cerevisiae.network.txt");
    proteinOrder = new ArrayList<String>(connections.keySet());
    Collections.sort(proteinOrder);
    for (String protein1: proteinOrder) {
      output.append("! " + protein1 + "\n");
      ArrayList<String> domainOrder = new ArrayList<String>(connections.get(protein1).keySet());
      Collections.sort(domainOrder);
      for (String domainNumber2: domainOrder) {
        String outputLine = protein1 + "-" + domainNumber2 + "\t";
        ArrayList<String> neighbourOrder = connections.get(protein1).get(domainNumber2);
        Collections.sort(neighbourOrder);
        for (String protein2: neighbourOrder) {
          outputLine = outputLine + protein2 + ", ";
        }
        outputLine = outputLine.substring(0, outputLine.length() - 2) + "\n";
        output.append(outputLine);
      }
    }


    Static.writeOutputToFile("sh3/S.cerevisiae.domains.txt", output);

    // Network output
    NetworkAdjList wormNetwork = new NetworkAdjList("C.elegans");
    // Text output
    output = new StringBuffer();
    connections = new HashMap<String, HashMap<String, ArrayList<String>>>();

    in = new BufferedReader(new FileReader("sh3/wormY2H-SH3network-BrianLaw.txt"));
    line = "";

    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");
      String protein1 = split[1];
      String domainNumber = split[2];
      String protein2 = split[4];

      if (!wormNetwork.containsNode(protein1)) {
        wormNetwork.addNode(protein1);
      }
      connections.putIfAbsent(protein1, new HashMap<String, ArrayList<String>>());
      connections.get(protein1).putIfAbsent(domainNumber, new ArrayList<String>());

      if (!wormNetwork.containsNode(protein2)) {
        wormNetwork.addNode(protein2);
      }
      wormNetwork.addEdge(protein1, protein2);
      connections.get(protein1).get(domainNumber).add(protein2);
    }

    in.close();


    // Post-processing to remove empty domains and empty proteins
    proteins = new ArrayList<String>(connections.keySet());
    for (String protein1: proteins) {
      ArrayList<String> domains = new ArrayList<String>(connections.get(protein1).keySet());
      for (String domainNumber: domains) {
        if (connections.get(protein1).get(domainNumber).size() == 0) {
          connections.get(protein1).remove(domainNumber);
        }
      }
      if (connections.get(protein1).size() == 0) {
        connections.remove(protein1);
      }
    }


    wormNetwork.writeToFile("sh3/C.elegans.network.txt");
    proteinOrder = new ArrayList<String>(connections.keySet());
    Collections.sort(proteinOrder);
    for (String protein1: proteinOrder) {
      output.append("! " + protein1 + "\n");
      ArrayList<String> domainOrder = new ArrayList<String>(connections.get(protein1).keySet());
      Collections.sort(domainOrder);
      for (String domainNumber2: domainOrder) {
        String outputLine = protein1 + "-" + domainNumber2 + "\t";
        ArrayList<String> neighbourOrder = connections.get(protein1).get(domainNumber2);
        Collections.sort(neighbourOrder);
        for (String protein2: neighbourOrder) {
          outputLine = outputLine + protein2 + ", ";
        }
        outputLine = outputLine.substring(0, outputLine.length() - 2) + "\n";
        output.append(outputLine);
      }


    }

    Static.writeOutputToFile("sh3/C.elegans.domains.txt", output);

  }

  /**
   *
   * @param species
   * @return - a mapping of protein name, to domain name (proteinname-domainnumber), to all the targets of that domain
   * @throws IOException
   */
  public static HashMap<String, HashMap<String, HashSet<String>>> readSh3DomainFile(String species) throws IOException {
    System.out.println("TEST");
    BufferedReader in = new BufferedReader(new FileReader("data/sh3/" + species + ".domains.txt"));
    String line = "";
    String protein = null;

    HashMap<String, HashMap<String, HashSet<String>>> connections = new HashMap<String, HashMap<String, HashSet<String>>>();

    while ((line = in.readLine()) != null) {
      if (line.startsWith("!")) {
        protein = line.substring(1).trim();
        connections.putIfAbsent(protein, new HashMap<String, HashSet<String>>());
        continue;
      }

      String domain = line.split("\t")[0];
      HashSet<String> neighbours = new HashSet<String>(Arrays.asList(line.split("\t")[1].split(", ")));
      connections.get(protein).putIfAbsent(domain, neighbours);
    }

    in.close();

    return connections;
  }

}
