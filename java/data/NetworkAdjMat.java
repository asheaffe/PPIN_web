package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import functions.Static;
import processing.Networks;

public class NetworkAdjMat extends Network {

  ArrayList<Node> nodes; // Ordered list of nodes
  boolean[][] edges; // 2D matrix of edges
  HashMap<Node, Integer> nodesToIndex; // Fast mapping of nodes to array indexes
  int numEdges = 0;

  int[] degrees; // array of node degrees, stored for fast access

  public NetworkAdjMat(String name) {
    super(name);
    this.nodesToIndex = new HashMap<Node, Integer>();
    this.edges = new boolean[0][0];
    this.degrees = new int[0];
  }

  /**
   * Creates a new network of the specified species, by reading the tab-delimited adjacency list from
   * fileName. The network's name will default to the same as the species.
   * @param fileName - the file name from which the network whould be read
   * @param species - the species for which the network is being constructed
   * @throws Exception
   */
  public NetworkAdjMat(String fileName, String species) throws Exception {
    this(species);

    BufferedReader in = new BufferedReader(new FileReader(fileName));
    String line = "";

    // Assemble the comment header for the network
    while ((line = Static.skipBlankLines(in)) != null && line.startsWith("!")) {
      this.commentHeader += line;
    }

    HashSet<String> proteins = new HashSet<String>();
    LinkedList<String> edges = new LinkedList<String>();

    do {
      String[] split = line.split("\t");
      for (int i = 0; i < split.length; i++) {
        proteins.add(split[i]);
      }

      edges.add(line);
    } while ((line = Static.skipBlankCommentLines(in)) != null);

    this.nodesToIndex = new HashMap<Node, Integer>((int) Math.ceil(proteins.size() / 0.75));
    this.nodes = new ArrayList<Node>(proteins.size());
    this.edges = new boolean[proteins.size() * 2][proteins.size() * 2];
    this.degrees = new int[proteins.size() * 2];

    for (String proteinName: proteins) {
      Node newNode = new Node(proteinName, this.species);
      this.nodes.add(newNode);
      this.nodesToIndex.put(newNode, this.nodesToIndex.size());
    }

    for (String edge: edges) {
      String[] split = edge.split("\t");
      Node node1 = new Node(split[0], this.species);
      Node node2 = new Node(split[1], this.species);
      this.addEdge(node1, node2);
    }
    this.numEdges = edges.size();

    in.close();
  }

  /**
   * Creates a copy of the given network. The new network is identical to the old network, but is unlinked
   * and its name is prepended with "Copy of ".
   * @param network - the network to be copied
   * @throws Exception
   */
  public NetworkAdjMat(Network network) throws Exception {
    super("Copy of " + network.getName());
    this.nodesToIndex = new HashMap<Node, Integer>((int) Math.ceil(network.getNumVertices() / 0.75));
    this.nodes = new ArrayList<Node>(network.getNumVertices());
    for (Node node: network.getNodes()) {
      this.nodesToIndex.put(node, this.nodesToIndex.size());
      this.nodes.add(node);
    }
    this.edges = new boolean[this.nodes.size() * 2][this.nodes.size() * 2];
    for (Node node: network.getNodes()) {
      for (Node neighbour: network.getAdjacent(node)) {
        this.addEdge(node, neighbour);
      }
    }
    this.commentHeader = network.getCommentHeader();
    this.species = network.getSpecies();
  }

  public NetworkAdjMat clone() {
    NetworkAdjMat newNetwork = new NetworkAdjMat("Copy of " + this.name);
    newNetwork.nodesToIndex = new HashMap<Node, Integer>(this.nodesToIndex);
    newNetwork.nodes = new ArrayList<Node>(this.nodes);
    newNetwork.edges = new boolean[this.edges.length][this.edges.length];
    newNetwork.degrees = new int[this.edges.length];
    for (int i = 0; i < this.edges.length; i++) {
      System.arraycopy(this.edges[i], 0, newNetwork.edges[i], 0, this.edges.length);
    }
    System.arraycopy(this.degrees, 0, newNetwork.degrees, 0, this.degrees.length);
    newNetwork.numEdges = this.numEdges;
    newNetwork.commentHeader = this.commentHeader;
    newNetwork.species = this.species;
    return newNetwork;
  }

  @Override
  public boolean containsNode(Node node) {
    return this.nodesToIndex.containsKey(node);
  }

  @Override
  public Set<Node> getNodes() {
    return Collections.unmodifiableSet(this.nodesToIndex.keySet());
  }

  @Override
  public void addNode(Node node) throws Exception {
    // Need to grow the 2D array :(
    if (this.nodes.size() >= this.edges.length) {
      boolean[][] newEdges = new boolean[this.edges.length*2][this.edges.length*2];
      for (int i = 0; i < this.edges.length; i++) {
        System.arraycopy(this.edges[i], 0, newEdges[i], 0, this.edges.length);
      }

      int[] newDegrees = new int[this.degrees.length*2];
      System.arraycopy(this.degrees, 0, newDegrees, 0, this.degrees.length);

      this.edges = newEdges;
      this.degrees = newDegrees;
    }
    this.nodesToIndex.put(node, this.nodesToIndex.size());
    this.nodes.add(node);
  }

  @Override
  public void removeNode(Node node) throws Exception {
    if (!this.containsNode(node)) {
      throw new Exception("Cannot remove node " + node.toString() + " from network " + this.toString() + " Does not exist.");
    }

    int index = this.nodesToIndex.get(node);

    // If the node being removed is last in array, just shrink the array's known dimensions.
    if (index == this.nodesToIndex.size()) {
      this.numEdges -= this.getDegree(node);
      this.nodesToIndex.remove(node);
      this.nodes.remove(this.nodes.size()-1);
    }
    // Otherwise, we will swap the last node into the removed node's position
    else {
      // Find the last node in the array, so we can swap it into the empty position.
      int lastIndex = this.nodesToIndex.size()-1;
      Node lastNode = this.nodes.get(lastIndex);

      // Rewrite all the array values of the node to be deleted with those of the last node
      for (int i=0; i<this.nodesToIndex.size(); i++) {
        if (this.edges[i][index]) {
          this.degrees[i]--;
        }
        this.edges[i][index] = this.edges[i][lastIndex];
      }
      this.edges[index] = this.edges[lastIndex];
      this.degrees[index] = this.degrees[lastIndex];

      // Remove the node and overwrite the stored index of the last node.
      this.nodesToIndex.remove(node);
      this.nodesToIndex.put(lastNode, index);
      this.nodes.set(index, lastNode);
      this.nodes.remove(lastIndex);
    }
  }

  @Override
  public Set<PairOfStrings> getEdges() {
    HashSet<PairOfStrings> edges = new HashSet<PairOfStrings>(this.numEdges);
    for (int i=0; i<this.nodes.size(); i++) {
      for (int j=i; j<this.nodes.size(); j++) {
        if (this.edges[i][j]) {
          if (this.nodes.get(i).compareTo(this.nodes.get(j)) <= 0) {
            edges.add(new PairOfStrings(this.nodes.get(i).name, this.nodes.get(j).name));
          }
          else {
            edges.add(new PairOfStrings(this.nodes.get(j).name, this.nodes.get(i).name));
          }
        }
      }
    }
    return edges;
  }

  @Override
  public void addEdge(Node node1, Node node2) throws Exception {
    if (this.containsNode(node1) && this.containsNode(node2)) {
      int index1 = this.nodesToIndex.get(node1);
      int index2 = this.nodesToIndex.get(node2);
      if (!this.edges[index1][index2]) {
        this.edges[index1][index2] = true;
        this.edges[index2][index1] = true;
        this.numEdges++;
        this.degrees[index1]++;
        if (index1 != index2) {
          this.degrees[index2]++;
        }
      }
    }
    else {
      throw new Exception("Can't add edge between non-existent vertices!");
    }
  }

  @Override
  public void removeEdge(Node node1, Node node2) throws Exception {
    if (this.containsNode(node1) && this.containsNode(node2)) {
      int index1 = this.nodesToIndex.get(node1);
      int index2 = this.nodesToIndex.get(node2);
      if (this.edges[index1][index2]) {
        this.edges[index1][index2] = false;
        this.edges[index2][index1] = false;
        this.numEdges--;
        this.degrees[index1]--;
        if (index1 != index2) {
          this.degrees[index2]--;
        }
      }
    }
    else {
      throw new Exception("Can't remove edge between non-existent vertices!");
    }
  }

  @Override
  public int getNumVertices() {
    return this.nodesToIndex.size();
  }

  @Override
  public int getNumEdges() {
    return this.numEdges;
  }

  @Override
  public Set<Node> getAdjacent(Node node) {
    if (this.containsNode(node)) {
      HashSet<Node> result = new HashSet<Node>();
      for (Node possibleNeighbour: this.nodesToIndex.keySet()) {
        if (this.edges[this.nodesToIndex.get(possibleNeighbour)][this.nodesToIndex.get(node)]) {
          result.add(possibleNeighbour);
        }
      }
      return result;
    }
    else {
      return Collections.emptySet();
    }
  }

  @Override
  public int getDegree(Node node) {
    return this.degrees[this.nodesToIndex.get(node)];
  }

  @Override
  public boolean areAdjacent(Node node1, Node node2) {
    return this.edges[this.nodesToIndex.get(node1)][this.nodesToIndex.get(node2)];
  }

  public static void main(String[] args) throws Exception {
    Network network = Networks.getNetwork("S.cerevisiae");

    System.out.println(network.getAdjacent(new Node("AAD14", "S.cerevisiae")));
  }
}
