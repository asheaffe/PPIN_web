package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Domain;
import data.Fasta;
import data.Orthologies;
import data.PairOfStrings;
import functions.Constants;
import data.InterologStruct;
import functions.HumanSh3Data;
import functions.Static;
import processing.Networks;

public class JsonTest {

  // Set up filtering for certain domain types
  public static HashSet<String> domainFilter;
  static {
    try {
      JsonTest.domainFilter = Domain.getDomainFilter("PRM");
    }
    catch (IOException e) {}
  }
  public static String domainDb = "pfam";

  private static Predicate<VNode> predicted = vnode -> vnode.checkClass("predicted") || vnode.checkClass("validated");
  private static Predicate<VNode> validated = vnode -> vnode.checkClass("validated");
  private static Predicate<VNode> dtarget = vnode -> vnode.checkClass("domain-target") || vnode.checkClass("protein-target");
  private static Predicate<VNode> ptarget = vnode -> vnode.checkClass("protein-target");

  private static int X_SEP = 30;
  private static int Y_SEP = 30;

  private static int INTEROLOG_CENTRE_DIST = 25;
  private static int DOMAIN_CENTRE_DIST = 150;
  private static int DOMAIN_Y_SEP = 60;

  private static int MATCHED_X_DIST = 250;
  private static int MATCHED_Y_DIST = 50;

  private static int DOMAIN_TARGET_X_DIST = 50;
  private static int DOMAIN_TARGET_Y_DIST = 15;

  private static int TARGET_X_SEP = 30;

  public static JSONObject interactionEdge(String node1, String node2) {
    JSONObject edge = new JSONObject();

    JSONObject data = new JSONObject();
    data.put("id", node1 + "-" + node2);
    data.put("weight", 75);
    data.put("source", node1);
    data.put("target", node2);
    edge.put("data", data);

    edge.put("group", "edges");
    edge.put("removed", false);
    edge.put("selected", false);
    edge.put("selectable", true);
    edge.put("locked", false);
    edge.put("grabbable", true);
    edge.put("classes", "");

    return edge;
  }

  public static JSONObject orthologyEdge(String node1, String node2) {
    JSONObject edge = JsonTest.interactionEdge(node1, node2);
    edge.put("classes", "orthology");
    return edge;
  }

  /**
   * Helper method to turn protein names into IDs for nodes, in case two species have proteins of
   * the same name.
   *
   * @param proteinName
   * @param speciesName
   * @return
   */
  private static String proteinNameToNodeId(String proteinName, Fasta fasta) {
    String id = "";
    if (JsonTest.proteinNameToEnsemblId(proteinName, fasta) != null) {
      id = JsonTest.proteinNameToEnsemblId(proteinName, fasta);
    }
    else  {
      Static.debugOutput("Proteins not found: " + proteinName);
      id = fasta.hashCode() + ": " + proteinName;
    }
    return id;
  }

  /**
   * Helper method to match protein name to corresponding Ensembl ID, if one exists. Null otherwise.
   * (Should only occur due to manual removal of proteins from Ensembl data. Should be rectified with
   *  data overhaul.)
   *
   * @param proteinName
   * @param speciesName
   * @return
   */
  private static String proteinNameToEnsemblId(String proteinName, Fasta fasta) {
    if (fasta.names2ids.containsKey(proteinName)) {
      return Static.getOneFromSet(fasta.names2ids.get(proteinName));
    }
    return null;
  }

  /**
   * Helper method to lay out nodes in a grid shape
   *
   * @param container - JSON container for nodes
   * @param pUnmatched1 - A list
   * @param species
   * @param placedNodes
   * @param leftX
   * @param topY
   * @param sepX
   * @param sepY
   * @param numX
   * @param parent
   */
  private static void gridLayoutHelper(HashSet<VNode> placedNodes, ArrayList<VNode> pUnmatched1, int leftX, int topY, int sepX, int sepY, int numX) {
    int xcoord = leftX;
    int ycoord = topY;

    for (VNode node: pUnmatched1) {
      node.setX(xcoord);
      node.setY(ycoord);

      xcoord += sepX;

      if (xcoord >= leftX + (sepX * numX)) {
        xcoord = leftX;
        ycoord += sepY;
      }

    }
  }

  private static class HumanDataNodes {
    public HashMap<String, ArrayList<String>> domainTargets = new HashMap<String, ArrayList<String>>();
    public HashSet<String> targets = new HashSet<String>();
  }

  /**
   *
   * @param shobhitData - a mapping domain name (proteinname-domainnumber), to all the targets of
   *        that domain
   * @return
   */
  private static HumanDataNodes processHumanSH3Data(String queryProtein, String querySpecies) {
    // Init return type
    HumanDataNodes result = new HumanDataNodes();

    HashMap<String, HashMap<String, HashSet<String>>> shobhitData;
    try {
      shobhitData = HumanSh3Data.readSh3DomainFile(querySpecies);
    }
    catch (IOException ioe) {
      return result;
    }

    if (!shobhitData.containsKey(queryProtein)) {
      return result;
    }

    for (String domainCode: shobhitData.get(queryProtein).keySet()) {
      int index = domainCode.lastIndexOf('-');
      int domainNumber = Integer.parseInt(domainCode.substring(index + 1));

      String domainName = "SH3" + "-" + domainNumber;
      result.domainTargets.putIfAbsent(domainName, new ArrayList<String>());

      for (String target: shobhitData.get(queryProtein).get(domainCode)) {
        result.domainTargets.get(domainName).add(target);
        result.targets.add(target);
      }
    }

    return result;
  }

  private static ArrayList<Domain> processDomain(String queryProtein1, Fasta fasta1, String species1) throws Exception {
    ArrayList<Domain> result = new ArrayList<Domain>();
    String enspId1 = Static.getOneFromSet(fasta1.names2ids.get(queryProtein1));

    HashMap<String, ArrayList<Domain>> protein2domains1 = Domain.readDomainsFile("data/" + species1 + "/" + species1 + ".protein_domains.all.processed." + Constants.FILE_VERSION + ".txt");

    for (int i = 0; i < protein2domains1.get(enspId1).size(); i++) {
      Domain domain = protein2domains1.get(enspId1).get(i);
      if ((JsonTest.domainFilter == null || JsonTest.domainFilter.contains(domain.name)) && (JsonTest.domainDb.equals("all") || domain.source.contains(JsonTest.domainDb))) {
        result.add(domain);
      }
    }

    return result;
  }

  public static void domainLayout(String filename, JSONArray container, String queryProtein1, String species1, String queryProtein2, String species2) throws Exception {
    Orthologies orthologies = Orthologies.getOrthologies(species1, species2);

    // Get corresponding ENSP IDs from FASTA file.
//    Fasta fasta1 = new Fasta("data/" + species1 + "/" + species1 + ".pep." + Constants.NETWORK_SOURCE + "." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);
//    Fasta fasta2 = new Fasta("data/" + species2 + "/" + species2 + ".pep." + Constants.NETWORK_SOURCE + "." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);
  Fasta fasta1 = new Fasta("data/" + species1 + "/" + species1 + ".pep.longest." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);
  Fasta fasta2 = new Fasta("data/" + species2 + "/" + species2 + ".pep.longest." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);


    String queryProteinId1 = JsonTest.proteinNameToNodeId(queryProtein1, fasta1);
    String queryProteinId2 = JsonTest.proteinNameToNodeId(queryProtein2, fasta2);


    // Domain data processing
    HumanDataNodes shobhitData1 = JsonTest.processHumanSH3Data(queryProtein1, species1);
    HumanDataNodes shobhitData2 = JsonTest.processHumanSH3Data(queryProtein2, species2);
    ArrayList<Domain> domains1 = JsonTest.processDomain(queryProtein1, fasta1, species1);
    ArrayList<Domain> domains2 = JsonTest.processDomain(queryProtein2, fasta2, species2);

    HashSet<String> neighbours1 = new HashSet<String>(shobhitData1.targets);
    HashSet<String> neighbours2 = new HashSet<String>(shobhitData2.targets);

    HashSet<String> qNeighbours1 = Static.namesFromNodes(Networks.getNetwork(species1).getAdjacent(queryProtein1));
    HashSet<String> qNeighbours2 = Static.namesFromNodes(Networks.getNetwork(species2).getAdjacent(queryProtein2));

    HashSet<String> vNeighbours1 = Static.namesFromNodes(Networks.getNetwork(species1, "exp", Constants.NETWORK_STRUCTURE).getAdjacent(queryProtein1));
    HashSet<String> vNeighbours2 = Static.namesFromNodes(Networks.getNetwork(species2, "exp", Constants.NETWORK_STRUCTURE).getAdjacent(queryProtein2));

    neighbours1.addAll(qNeighbours1);
    neighbours2.addAll(qNeighbours2);

    // Ignore self-loops.
    neighbours1.remove(queryProtein1);
    neighbours2.remove(queryProtein2);

    // Data structure for result
    InterologStruct interologStruct = new InterologStruct();

    for (String neighbour1: neighbours1) {
      HashSet<String> orthoNeighbours1 = orthologies.getOrthologsFromSpeciesByName(species1, neighbour1, species2);
      if (orthoNeighbours1.size() == 0) {
        interologStruct.unmatchable1.add(neighbour1);
        continue;
      }
      Set<String> matches = Static.intersectionOfTwoSets(orthoNeighbours1, neighbours2);
      if (matches.size() > 0) {
        for (String match: matches) {
          interologStruct.interologs.add(new PairOfStrings(neighbour1, match));
          interologStruct.matched1.add(neighbour1);
          interologStruct.matched2.add(match);
        }
      }
      else {
        interologStruct.unmatched1.add(neighbour1);
      }

    }

    for (String neighbour2: neighbours2) {
      HashSet<String> orthoNeighbours2 = orthologies.getOrthologsFromSpeciesByName(species2, neighbour2, species1);
      if (orthoNeighbours2.size() == 0) {
        interologStruct.unmatchable2.add(neighbour2);
        continue;
      }
      Set<String> matches = Static.intersectionOfTwoSets(orthoNeighbours2, neighbours1);
      if (matches.size() > 0) {

      }
      else {
        interologStruct.unmatched2.add(neighbour2);
      }
    }

    // Initializing nodes and edges
    HashSet<VEdge> orthoEdges = new HashSet<VEdge>();

    HashMap<String, VNode> allVNodes = new HashMap<String, VNode>();
    HashSet<VNode> placedNodes = new HashSet<VNode>();
    HashMap<PairOfStrings, VEdge> allVEdges = new HashMap<PairOfStrings, VEdge>();

    // Create container protein nodes
    allVNodes.put(queryProteinId1, new VNode(queryProteinId1, queryProtein1, 0, 0, "species1 queryProtein"));
    allVNodes.put(queryProteinId2, new VNode(queryProteinId2, queryProtein2, 0, 0, "species2 queryProtein"));

    String classes1 = "species1 matched protein interolog";
    String classes2 = "species2 matched protein interolog";
    for (String targetProtein: interologStruct.matched1) {
      String id1 = JsonTest.proteinNameToNodeId(targetProtein, fasta1);
      VNode newNode = new VNode(id1, targetProtein, 0, 0, classes1);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein1, fasta1), id1);

      allVNodes.put(id1, newNode);
      if (qNeighbours1.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours1.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData1.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species1.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours1.contains(targetProtein) && shobhitData1.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    for (String targetProtein: interologStruct.matched2) {
      String id2 = JsonTest.proteinNameToNodeId(targetProtein, fasta2);
      VNode newNode = new VNode(id2, targetProtein, 0, 0, classes2);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein2, fasta2), id2);

      allVNodes.put(id2, newNode);
      if (qNeighbours2.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours2.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData2.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species2.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours2.contains(targetProtein) && shobhitData2.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    String oClass = "orthology";
    // Convert interologs into orthology edges for the graph
    for (PairOfStrings interolog: interologStruct.interologs) {
      String id1 = JsonTest.proteinNameToNodeId(interolog.getString1(), fasta1);
      String id2 = JsonTest.proteinNameToNodeId(interolog.getString2(), fasta2);

      VEdge orthology = new VEdge(id1, id2, oClass);
      orthoEdges.add(orthology);

      allVNodes.get(id1).addPartner(id2);
      allVNodes.get(id2).addPartner(id1);
    }

    classes1 = "species1 unmatched protein";
    classes2 = "species2 unmatched protein";
    for (String targetProtein: interologStruct.unmatched1) {
      String id1 = JsonTest.proteinNameToNodeId(targetProtein, fasta1);
      VNode newNode = new VNode(id1, targetProtein, 0, 0, classes1);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein1, fasta1), id1);

      allVNodes.put(id1, newNode);
      if (qNeighbours1.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours1.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData1.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species1.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours1.contains(targetProtein) && shobhitData1.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    for (String targetProtein: interologStruct.unmatched2) {
      String id2 = JsonTest.proteinNameToNodeId(targetProtein, fasta2);
      VNode newNode = new VNode(id2, targetProtein, 0, 0, classes2);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein2, fasta2), id2);

      allVNodes.put(id2, newNode);
      if (qNeighbours2.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours2.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData2.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species2.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours2.contains(targetProtein) && shobhitData2.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    classes1 = "species1 unmatchable protein";
    classes2 = "species2 unmatchable protein";
    for (String targetProtein: interologStruct.unmatchable1) {
      String id1 = JsonTest.proteinNameToNodeId(targetProtein, fasta1);
      VNode newNode = new VNode(id1, targetProtein, 0, 0, classes1);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein1, fasta1), id1);

      allVNodes.put(id1, newNode);
      if (qNeighbours1.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours1.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData1.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species1.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours1.contains(targetProtein) && shobhitData1.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    for (String targetProtein: interologStruct.unmatchable2) {
      String id2 = JsonTest.proteinNameToNodeId(targetProtein, fasta2);
      VNode newNode = new VNode(id2, targetProtein, 0, 0, classes2);
      VEdge newEdge = new VEdge(JsonTest.proteinNameToNodeId(queryProtein2, fasta2), id2);

      allVNodes.put(id2, newNode);
      if (qNeighbours2.contains(targetProtein)) {
        newNode.addClass("protein-target");
        if (vNeighbours2.contains(targetProtein)) {
          newNode.addClass("validated");
        }
        else {
          newNode.addClass("predicted");
        }
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
      if (shobhitData2.targets.contains(targetProtein)) {
        newNode.addClasses("domain-target");
        if (species2.equals("H.sapiens")) {
          newNode.addClass("predicted");
        }
        else {
          newNode.addClass("validated");
        }
      }
      if (qNeighbours2.contains(targetProtein) && shobhitData2.targets.contains(targetProtein)) {
        newEdge.addClass("no-domain-show");
      }
    }

    for (String id: allVNodes.keySet()) {
      VNode vnode = allVNodes.get(id);
      try {
        if (vnode.checkClass("species1")) {
          vnode.getJSONObject("data").put("length", fasta1.ids2sequences.get(id).getLength());
        }
        else {
          vnode.getJSONObject("data").put("length", fasta2.ids2sequences.get(id).getLength());
        }
      }
      catch (NullPointerException npe) {
        vnode.getJSONObject("data").put("length", "???");
      }

      try {
        ArrayList<String> neighbours;
        if (vnode.checkClass("species1")) {
          neighbours = new ArrayList<String>(Static.namesFromNodes(Networks.getNetwork(species1).getAdjacent(vnode.getName())));
        }
        else {
          neighbours = new ArrayList<String>(Static.namesFromNodes(Networks.getNetwork(species2).getAdjacent(vnode.getName())));
        }
        Collections.sort(neighbours);
        List<String> shorterNeighbours = neighbours.subList(0, Math.min(neighbours.size(), 10));
        vnode.getJSONObject("data").put("num_neighbours", neighbours.size());
        vnode.getJSONObject("data").put("neighbours", String.join(", ", shorterNeighbours));
        if (neighbours.size() > 10) {
          vnode.getJSONObject("data").put("neighbours", vnode.getJSONObject("data").get("neighbours") + "...");
        }
      }
      catch (NullPointerException npe) {
        vnode.getJSONObject("data").put("length", "???");
      }
    }

    // At this point, all the relevant nodes are gathered. Now a matter of arranging nodes and edges, plus gathering domain edges.
    HashSet<VNode> pMatched1 = new HashSet<VNode>();
    HashSet<VNode> pMatched2 = new HashSet<VNode>();
    ArrayList<VNode> pUnmatched1 = new ArrayList<VNode>();
    ArrayList<VNode> pUnmatched2 = new ArrayList<VNode>();
    ArrayList<VNode> pUnmatchable1 = new ArrayList<VNode>();
    ArrayList<VNode> pUnmatchable2 = new ArrayList<VNode>();
    HashSet<VNode> dMatched1 = new HashSet<VNode>();
    HashSet<VNode> dMatched2 = new HashSet<VNode>();
    ArrayList<VNode> dUnmatched1 = new ArrayList<VNode>();
    ArrayList<VNode> dUnmatched2 = new ArrayList<VNode>();
    ArrayList<VNode> dUnmatchable1 = new ArrayList<VNode>();
    ArrayList<VNode> dUnmatchable2 = new ArrayList<VNode>();

    for (VNode vnode: allVNodes.values()) {
      if (vnode.checkClass("matched")) {
        if (vnode.checkClass("species1")) {
          if (vnode.checkClass("domain-target")) {
            dMatched1.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pMatched1.add(vnode);
          }
        }
        else if (vnode.checkClass("species2")) {
          if (vnode.checkClass("domain-target")) {
            dMatched2.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pMatched2.add(vnode);
          }
        }
      }
      else if (vnode.checkClass("unmatched")) {
        if (vnode.checkClass("species1")) {
          if (vnode.checkClass("domain-target")) {
            dUnmatched1.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pUnmatched1.add(vnode);
          }
        }
        else if (vnode.checkClass("species2")) {
          if (vnode.checkClass("domain-target")) {
            dUnmatched2.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pUnmatched2.add(vnode);
          }
        }
      }
      else if (vnode.checkClass("unmatchable")) {
        if (vnode.checkClass("species1")) {
          if (vnode.checkClass("domain-target")) {
            dUnmatchable1.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pUnmatchable1.add(vnode);
          }
        }
        else if (vnode.checkClass("species2")) {
          if (vnode.checkClass("domain-target")) {
            dUnmatchable2.add(vnode);
          }
          else if (vnode.checkClass("protein-target")) {
            pUnmatchable2.add(vnode);
          }
        }
      }
    }

    Collections.sort(pUnmatched1);
    Collections.sort(pUnmatched2);
    Collections.sort(pUnmatchable1);
    Collections.sort(pUnmatchable2);

    // Protein-level interologs
    ArrayList<ArrayList<VNode>> interologRows = new ArrayList<ArrayList<VNode>>();
    for (PairOfStrings interolog: interologStruct.interologs) {
      ArrayList<VNode> newRow = new ArrayList<VNode>(2);
      VNode vnode1 = allVNodes.get(JsonTest.proteinNameToNodeId(interolog.getString1(), fasta1));
      VNode vnode2 = allVNodes.get(JsonTest.proteinNameToNodeId(interolog.getString2(), fasta2));

      boolean placeVnode1 = vnode1.checkClass("protein-target") && !vnode1.checkClass("domain-target") && !placedNodes.contains(vnode1);
      boolean placeVnode2 = vnode2.checkClass("protein-target") && !vnode2.checkClass("domain-target") && !placedNodes.contains(vnode2);

      if (!placeVnode1 && !placeVnode2) {
        continue;
      }

      if (placeVnode1 && placeVnode2) {
        newRow.add(vnode1);
        newRow.add(vnode2);
        placedNodes.add(vnode1);
        placedNodes.add(vnode2);
      }
      else if (placeVnode1) {
        newRow.add(vnode1);
        newRow.add(null);
        placedNodes.add(vnode1);
      }
      else if (placeVnode2) {
        newRow.add(null);
        newRow.add(vnode2);
        placedNodes.add(vnode2);
      }

      interologRows.add(newRow);
    }


    if (interologRows.size() > 0) {
      container.put(new VNode("Interologs", "Interologs", 0, 0, "container"));

      int ycoord = -Y_SEP * interologRows.size();
      for (ArrayList<VNode> row: interologRows) {

        if (row.get(0) != null) {
          row.get(0).setX(-INTEROLOG_CENTRE_DIST);
          row.get(0).setY(ycoord);
          row.get(0).setParent("Interologs");
          placedNodes.add(row.get(0));
        }
        if (row.get(1) != null) {
          row.get(1).setX(INTEROLOG_CENTRE_DIST);
          row.get(1).setY(ycoord);
          row.get(1).setParent("Interologs");
          placedNodes.add(row.get(1));
        }
        if (row.get(0) != null || row.get(1) != null) {
          ycoord += Y_SEP;
        }
      }

    }

    for (VEdge orthoEdge: orthoEdges) {
      container.put(orthoEdge);
    }

    // Place domain nodes
    int y = 0;
    int lowestY = 0;
    HashMap<String, Integer> domainCount = new HashMap<String, Integer>();

    for (int i=0; i<domains1.size(); i++) {
      Domain domain = domains1.get(i);
      String domainType = Domain.niceifyName(domain.name);
      domainCount.put(domainType, domainCount.getOrDefault(domainType, 0) + 1);
      VNode domainNode = VNode.childNode(queryProteinId1 + "-domain-" + i, domainType + "-" + domainCount.get(domainType), -DOMAIN_CENTRE_DIST, y, "domain species1", queryProteinId1);
      allVNodes.put(domainNode.getId(), domainNode);

      if (domainType.equals("SH3") && shobhitData1.domainTargets.containsKey("SH3-" + domainCount.get(domainType))) {
        int matchedX = -DOMAIN_CENTRE_DIST + DOMAIN_TARGET_X_DIST;
        int unmatchedX = -DOMAIN_CENTRE_DIST - DOMAIN_TARGET_X_DIST;
        int unmatchableX = -DOMAIN_CENTRE_DIST - DOMAIN_TARGET_X_DIST;

        ArrayList<String> targets = new ArrayList<String>(shobhitData1.domainTargets.get("SH3-" + domainCount.get(domainType)));
        Collections.sort(targets);

        for (String target: targets) {
          VNode targetNode = allVNodes.get(JsonTest.proteinNameToNodeId(target, fasta1));
          if (!placedNodes.contains(targetNode)) {
            if (dMatched1.contains(targetNode)) {
              targetNode.setX(matchedX);
              targetNode.setY(y);
              targetNode.setParent("Interologs");
              placedNodes.add(targetNode);
              matchedX += TARGET_X_SEP;
            }
            else if (dUnmatched1.contains(targetNode)) {
              targetNode.setX(unmatchedX);
              targetNode.setY(y - DOMAIN_TARGET_Y_DIST);
              placedNodes.add(targetNode);
              unmatchedX -= TARGET_X_SEP;
            }
            else if (dUnmatchable1.contains(targetNode)) {
              targetNode.setX(unmatchableX);
              targetNode.setY(y + DOMAIN_TARGET_Y_DIST);
              placedNodes.add(targetNode);
              unmatchableX -= TARGET_X_SEP;
            }
          }
          VEdge newEdge = new VEdge(targetNode.getId(), queryProteinId1 + "-domain-" + i);
          allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
        }
      }

      y += DOMAIN_Y_SEP;
      if (i != 0) {
        VEdge newEdge = new VEdge(queryProteinId1 + "-domain-" +  (i-1), queryProteinId1 + "-domain-" + i, "protein_sequence");
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
    }

    container.put(VNode.childNode("n-term-1", "N", -DOMAIN_CENTRE_DIST, -25, "terminus species1", queryProteinId1));
    container.put(VNode.childNode("c-term-1", "C", -DOMAIN_CENTRE_DIST, y - DOMAIN_Y_SEP + 25, "terminus species1", queryProteinId1));
    VEdge termEdge;
    if (domains1.size() == 0) {
      termEdge = new VEdge("n-term-1", "c-term-1", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
    }
    else {
      termEdge = new VEdge(queryProteinId1 + "-domain-0", "n-term-1", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
      termEdge = new VEdge(queryProteinId1 + "-domain-" + (domains1.size() - 1), "c-term-1", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
    }

    lowestY = y;
    y = 0;
    domainCount = new HashMap<String, Integer>();

    for (int i = 0; i < domains2.size(); i++) {
      Domain domain = domains2.get(i);
      String domainType = Domain.niceifyName(domain.name);
      domainCount.put(domainType, domainCount.getOrDefault(domainType, 0) + 1);
      VNode domainNode = VNode.childNode(queryProteinId2 + "-domain-" + i, domainType + "-" + domainCount.get(domainType), DOMAIN_CENTRE_DIST, y, "domain species2", queryProteinId2);
      allVNodes.put(domainNode.getId(), domainNode);

      if (domainType.equals("SH3") && shobhitData2.domainTargets.containsKey("SH3-" + domainCount.get(domainType))) {
        int matchedX = DOMAIN_CENTRE_DIST - DOMAIN_TARGET_X_DIST;
        int unmatchedX = DOMAIN_CENTRE_DIST + DOMAIN_TARGET_X_DIST;
        int unmatchableX = DOMAIN_CENTRE_DIST + DOMAIN_TARGET_X_DIST;

        ArrayList<String> targets = new ArrayList<String>(shobhitData2.domainTargets.get("SH3-" + domainCount.get(domainType)));
        Collections.sort(targets);

        for (String target: targets) {
          VNode targetNode = allVNodes.get(JsonTest.proteinNameToNodeId(target, fasta2));
          if (!placedNodes.contains(targetNode)) {
            if (dMatched2.contains(targetNode)) {
              targetNode.setX(matchedX);
              targetNode.setY(y);
              targetNode.setParent("Interologs");
              placedNodes.add(targetNode);
              matchedX -= TARGET_X_SEP;
            }
            else if (dUnmatched2.contains(targetNode)) {
              targetNode.setX(unmatchedX);
              targetNode.setY(y - DOMAIN_TARGET_Y_DIST);
              placedNodes.add(targetNode);
              unmatchedX += TARGET_X_SEP;
            }
            else if (dUnmatchable2.contains(targetNode)) {
              targetNode.setX(unmatchableX);
              targetNode.setY(y + DOMAIN_TARGET_Y_DIST);
              placedNodes.add(targetNode);
              unmatchableX += TARGET_X_SEP;
            }
          }
          VEdge newEdge = new VEdge(targetNode.getId(), queryProteinId2 + "-domain-" + i);
          allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);

        }
      }

      y += DOMAIN_Y_SEP;

      if (i != 0) {
        VEdge newEdge = new VEdge(queryProteinId2 + "-domain-" +  (i-1), queryProteinId2 + "-domain-" + i, "protein_sequence");
        allVEdges.put(new PairOfStrings(newEdge.getSource(), newEdge.getTarget()), newEdge);
      }
    }


    if (domains2.size() == 0) {
      container.put(VNode.childNode("n-term-2", "N", DOMAIN_CENTRE_DIST, 50, "terminus species2", queryProteinId2));
      container.put(VNode.childNode("c-term-2", "C", DOMAIN_CENTRE_DIST, -50, "terminus species2", queryProteinId2));
      termEdge = new VEdge("n-term-2", "c-term-2", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
    }
    else {
      container.put(VNode.childNode("n-term-2", "N", DOMAIN_CENTRE_DIST, -25, "terminus species2", queryProteinId2));
      container.put(VNode.childNode("c-term-2", "C", DOMAIN_CENTRE_DIST, y - DOMAIN_Y_SEP + 25, "terminus species2", queryProteinId2));
      termEdge = new VEdge(queryProteinId2 + "-domain-0", "n-term-2", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
      termEdge = new VEdge(queryProteinId2 + "-domain-" + (domains2.size() - 1), "c-term-2", "protein_sequence");
      allVEdges.put(new PairOfStrings(termEdge.getSource(), termEdge.getTarget()), termEdge);
    }



    lowestY = Math.max(y, lowestY);

    // Unmatched 1
    int unmatched1Size = (int) Math.floor(Math.sqrt(pUnmatched1.size())) + 1;

    if (unmatched1Size > 0) {
      container.put(new VNode("Unmatched 1", "Unmatched 1", 0, 0, "container"));

      JsonTest.gridLayoutHelper(placedNodes, pUnmatched1, -MATCHED_X_DIST - (X_SEP * (unmatched1Size - 1)), -MATCHED_Y_DIST - (Y_SEP * (unmatched1Size - 1)), X_SEP, Y_SEP, unmatched1Size);

      for (VNode node: pUnmatched1) {
        node.setParent("Unmatched 1");
        //container.put(JsonTest.interactionEdge(JsonTest.proteinNameToNodeId(queryProtein1, fasta1), node.getId()));
      }
    }

    // Unmatchable 1
    int unmatchable1Size = (int) Math.floor(Math.sqrt(pUnmatchable1.size())) + 1;

    if (unmatchable1Size > 0) {
      container.put(new VNode("Unmatchable 1", "Unmatchable 1", 0, 0, "container"));

      JsonTest.gridLayoutHelper(placedNodes, pUnmatchable1, -MATCHED_X_DIST - (X_SEP * (unmatched1Size - 1)), lowestY + MATCHED_Y_DIST, X_SEP, Y_SEP, unmatchable1Size);

      for (VNode node: pUnmatchable1) {
        node.setParent("Unmatchable 1");
        //container.put(JsonTest.interactionEdge(JsonTest.proteinNameToNodeId(queryProtein1, fasta1), node.getId()));
      }
    }

    y = 0;
    domainCount = new HashMap<String, Integer>();



    // Unmatched 2
    int unmatched2Size = (int) Math.floor(Math.sqrt(pUnmatched2.size())) + 1;

    if (unmatched2Size > 0) {
      container.put(new VNode("Unmatched 2", "Unmatched 2", 0, 0, "container"));

      JsonTest.gridLayoutHelper(placedNodes, pUnmatched2, MATCHED_X_DIST, -MATCHED_Y_DIST - (Y_SEP * (unmatched2Size - 1)), X_SEP, Y_SEP, unmatched2Size);

      for (VNode node: pUnmatched2) {
        node.setParent("Unmatched 2");
        //container.put(JsonTest.interactionEdge(JsonTest.proteinNameToNodeId(queryProtein2, fasta2), node.getId()));
      }
    }

    // Unmatchable 2
    int unmatchable2Size = (int) Math.floor(Math.sqrt(pUnmatchable2.size())) + 1;

    if (unmatchable2Size > 0) {
      container.put(new VNode("Unmatchable 2", "Unmatchable 2", 0, 0, "container"));

      JsonTest.gridLayoutHelper(placedNodes, pUnmatchable2, MATCHED_X_DIST, lowestY + MATCHED_Y_DIST, X_SEP, Y_SEP, unmatchable2Size);

      for (VNode node: pUnmatchable2) {
        node.setParent("Unmatchable 2");
        //container.put(JsonTest.interactionEdge(JsonTest.proteinNameToNodeId(queryProtein2, fasta2), node.getId()));
      }
    }

    for (VNode node: allVNodes.values()) {
      container.put(node);
    }
    for (VEdge edge: allVEdges.values()) {
      container.put(edge);
    }

    HashMap<String, Predicate<VNode>> modes = new HashMap<String, Predicate<VNode>>();
    modes.put("pd", JsonTest.predicted.and(JsonTest.dtarget));
    modes.put("pp", JsonTest.predicted.and(JsonTest.ptarget));
    modes.put("vd", JsonTest.validated.and(JsonTest.dtarget));
    modes.put("vp", JsonTest.validated.and(JsonTest.ptarget));

    for (String mode: modes.keySet()) {
        /* Interolog prediction processing. */
      MissingInterologStruct missingInterologStruct1 = JsonTest.missingInterologData(allVNodes, allVEdges, modes.get(mode), species1, species2, queryProtein1, queryProtein2, fasta1, fasta2, false);
      int numMissingInterologs = missingInterologStruct1.numMissingInterologs;

      VNode missingInterologs1 = VNode.secretNode(mode + "_missing_interologs1");
      missingInterologs1.getJSONObject("data").put("html", missingInterologStruct1.missingInterologString);
      container.put(missingInterologs1);

      MissingInterologStruct missingInterologStruct2 = JsonTest.missingInterologData(allVNodes, allVEdges, modes.get(mode), species1, species2, queryProtein1, queryProtein2, fasta1, fasta2, true);
      numMissingInterologs += missingInterologStruct2.numMissingInterologs;

      VNode missingInterologs2 = VNode.secretNode(mode + "_missing_interologs2");
      missingInterologs2.getJSONObject("data").put("html", missingInterologStruct2.missingInterologString);
      container.put(missingInterologs2);

      VNode stats = VNode.secretNode(mode + "_stats");
      container.put(stats);

      stats.getJSONObject("data").put("species1", species1);
      stats.getJSONObject("data").put("species2", species2);
      stats.getJSONObject("data").put("protein1", queryProtein1);
      stats.getJSONObject("data").put("protein2", queryProtein2);
      stats.getJSONObject("data").put("numInterologs", missingInterologStruct1.numFoundInterologs);  // Should be bi-directional?
      stats.getJSONObject("data").put("numMissingInterologs", numMissingInterologs);
      stats.getJSONObject("data").put("numInterologProteins1", missingInterologStruct2.numMatchedProteins);
      stats.getJSONObject("data").put("numInterologProteins2", missingInterologStruct1.numMatchedProteins);
      stats.getJSONObject("data").put("numUnmatchedProteins1", missingInterologStruct2.numUnmatchedProteins);
      stats.getJSONObject("data").put("numUnmatchedProteins2", missingInterologStruct1.numUnmatchedProteins);
      stats.getJSONObject("data").put("numUnmatchableProteins1", missingInterologStruct2.numUnmatchableProteins);
      stats.getJSONObject("data").put("numUnmatchableProteins2", missingInterologStruct1.numUnmatchableProteins);

      stats.getJSONObject("data").put("score", Static.twoDecimals.format((double)missingInterologStruct1.numFoundInterologs * 100 / (numMissingInterologs + missingInterologStruct1.numFoundInterologs)));
    }


    // Output to file
    BufferedWriter out = new BufferedWriter(new FileWriter("Web/json/" + filename));
    out.write(container.toString(2));
    out.close();
  }

  public static class MissingInterologStruct {
    public int numMissingInterologs = 0;
    public int numFoundInterologs = 0;
    public int numMatchedProteins = 0;
    public int numUnmatchedProteins = 0;
    public int numUnmatchableProteins = 0;
    public String missingInterologString = "";
    HashMap<String, ArrayList<String>> missingInterologs = new HashMap<String, ArrayList<String>>();
    HashMap<Integer, ArrayList<String>> missingInterologCounts = new HashMap<Integer, ArrayList<String>>();
  }

  public static MissingInterologStruct missingInterologData(HashMap<String, VNode> allVNodes, HashMap<PairOfStrings, VEdge> allVEdges, Predicate<VNode> filter, String oSpecies1, String oSpecies2, String oQueryProtein1, String oQueryProtein2, Fasta oFasta1, Fasta oFasta2, boolean flip) throws Exception {
    String species1 = flip ? oSpecies1: oSpecies2;
    String species2 = flip ? oSpecies2: oSpecies1;
    String queryProtein1 = flip ? oQueryProtein1: oQueryProtein2;
    String queryProtein2 = flip ? oQueryProtein2: oQueryProtein1;
    Fasta fasta1 = flip ? oFasta1: oFasta2;
    Fasta fasta2 = flip ? oFasta2: oFasta1;
    String speciesClass = flip ? "species1": "species2";

    MissingInterologStruct returnStruct = new MissingInterologStruct();

    for (VNode vnode: allVNodes.values()) {
      // Check only nodes that are neighbours of the first query protein.
      if (vnode.checkClass(speciesClass) && vnode.checkClass("protein") && vnode.getName() != queryProtein1 && filter.test(vnode)) {
        boolean matched = false;
        HashSet<String> orthoNeighbours = Orthologies.getOrthologies(species1, species2).getOrthologsFromSpeciesByName(species1, vnode.getName(), species2);
        if (orthoNeighbours.size() == 0) {
          returnStruct.numUnmatchableProteins++;
          continue;
        }
        for (String orthoNeighbour: orthoNeighbours) {
          if (allVNodes.containsKey(JsonTest.proteinNameToNodeId(orthoNeighbour, fasta2)) && filter.test(allVNodes.get(JsonTest.proteinNameToNodeId(orthoNeighbour, fasta2)))) {
            matched = true;
            returnStruct.numFoundInterologs++;
          }
          else {
            returnStruct.missingInterologs.putIfAbsent(orthoNeighbour, new ArrayList<String>());
            returnStruct.missingInterologs.get(orthoNeighbour).add(vnode.getName());
            returnStruct.numMissingInterologs++;
          }
        }

        if (matched) {
          returnStruct.numMatchedProteins++;
        }
        else {
          returnStruct.numUnmatchedProteins++;
        }
      }
    }

    for (String missingOrtholog: returnStruct.missingInterologs.keySet()) {
      returnStruct.missingInterologCounts.putIfAbsent(returnStruct.missingInterologs.get(missingOrtholog).size(), new ArrayList<String>());
      returnStruct.missingInterologCounts.get(returnStruct.missingInterologs.get(missingOrtholog).size()).add(missingOrtholog);
    }

    ArrayList<Integer> countOrder = new ArrayList<Integer>(returnStruct.missingInterologCounts.keySet());
    Collections.sort(countOrder, Collections.reverseOrder());

    StringBuilder missingInterologHTML = new StringBuilder();
    missingInterologHTML.append("<table class='missing_interologs_table'><tr><th colspan='3' id='missing_interologs_header_" + (flip?2:1) + "'>Predicted " + species2 + " " + queryProtein2 + " interactions</th></tr><tr><th># Missed Interologs</th><th>Predicted Interactor</th><th>Indicating " + queryProtein1 + " Interactors</tr>");
    for (int count: countOrder) {
      Collections.sort(returnStruct.missingInterologCounts.get(count));


      ArrayList<String> missingOrder = new ArrayList<String>(returnStruct.missingInterologCounts.get(count));
      Collections.sort(missingOrder);
      for (int i= 0; i<missingOrder.size(); i++) {
        String missingInteractor = missingOrder.get(i);
        String URLString = JsonTest.proteinNameToEnsemblLink(missingInteractor, fasta2);
        missingInterologHTML.append("<tr><td>" + (i==0?count:"") + "</td><td>" + URLString + missingInteractor + ((URLString.length()>0)?"</a>":"") + "</td><td>");

        Collections.sort(returnStruct.missingInterologs.get(missingInteractor));
        StringBuilder builder2 = new StringBuilder();
        for (String indicatingInteractor: returnStruct.missingInterologs.get(missingInteractor)) {
          String URLString2 = JsonTest.proteinNameToEnsemblLink(indicatingInteractor, fasta1);
          builder2.append(URLString2 + indicatingInteractor + ((URLString2.length()>0)?"</a>":"") + ", ");
        }
        missingInterologHTML.append(builder2.toString().substring(0, builder2.toString().length() - 2));
        missingInterologHTML.append("</td></tr>");
      }
    }
    missingInterologHTML.append("</table>");

    returnStruct.missingInterologString = missingInterologHTML.toString();

    return returnStruct;
  }

  public static String proteinNameToEnsemblLink(String protein, Fasta fasta) {
    return JsonTest.proteinNameToEnsemblId(protein, fasta)!=null?"<a href='http://www.ensembl.org/id/" + JsonTest.proteinNameToEnsemblId(protein, fasta) + "' target='_blank'>":"";

  }

  public static void main(String[] args) throws Exception {
    JSONArray container = new JSONArray();

//    String querySpecies1 = args[0];
//    String queryProtein1 = args[1];
//    String querySpecies2 = args[2];
//    String queryProtein2 = args[3];

//    String queryProtein1 = "EDE1";
//    String querySpecies1 = "S.cerevisiae";
//    String queryProtein2 = "ITSN-2";
//    String querySpecies2 = "C.elegans";

//    String queryProtein1 = "ITSN-1";
//    String querySpecies1 = "C.elegans";
//    String queryProtein2 = "ITSN1";
//    String querySpecies2 = "H.sapiens";

//    String queryProtein1 = "EDE1";
//    String querySpecies1 = "S.cerevisiae";
//    String queryProtein2 = "ITSN2";
//    String querySpecies2 = "H.sapiens";

//    String queryProtein1 = "DAP160";
//    String querySpecies1 = "D.melanogaster";
//    String queryProtein2 = "ITSN1";
//    String querySpecies2 = "H.sapiens";

    String queryProtein1 = "ITSN1";
    String querySpecies1 = "M.musculus";
    String queryProtein2 = "ITSN1";
    String querySpecies2 = "H.sapiens";


    JsonTest.domainLayout(Constants.getSpeciesSciToComm().get(querySpecies1).substring(0, 1).toUpperCase() + Constants.getSpeciesSciToComm().get(querySpecies1).substring(1) + " "
        + queryProtein1 + " - "
        + Constants.getSpeciesSciToComm().get(querySpecies2).substring(0, 1).toUpperCase() + Constants.getSpeciesSciToComm().get(querySpecies2).substring(1) + " "
        + queryProtein2 + ".json", container, queryProtein1, querySpecies1, queryProtein2, querySpecies2);

  }
}
