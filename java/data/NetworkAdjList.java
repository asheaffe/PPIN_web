package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import functions.Static;

public class NetworkAdjList extends Network {

  private HashSet<Node> nodes;
//  private HashMap<String, Node> nameSpeciesToNode;
  public EdgeList edges;

  public NetworkAdjList(String name) {
    super(name);
    this.nodes = new HashSet<Node>();
    this.edges = new EdgeList();
  }

  /**
   * Creates a new network of the specified species, by reading the tab-delimited adjacency list from
   * fileName. The network's name will default to the same as the species.
   * @param fileName - the file name from which the network whould be read
   * @param species - the species for which the network is being constructed
   * @throws Exception
   */
  public NetworkAdjList(String fileName, String species) throws Exception {
    this(species);

    BufferedReader in = new BufferedReader(new FileReader(fileName));
    String line = "";

    // Assemble the comment header for the network
    while ((line = Static.skipBlankLines(in)) != null && line.startsWith("!")) {
      this.commentHeader += line;
    }

    do {
      String[] split = line.split("\t");
      for (int i = 0; i < split.length; i++) {
        Node newNode = new Node(split[i], this.species);
        if (!this.containsNode(newNode)) {
          this.addNode(newNode);
        }
      }

      this.addEdge(new Node(split[0], this.species), new Node(split[1], this.species));
    } while ((line = Static.skipBlankCommentLines(in)) != null);

    in.close();
  }

  /**
   * Creates a copy of the given network. The new network is identical to the old network, but is unlinked
   * and its name is prepended with "Copy of ".
   * @param network - the network to be copied
   */
  public NetworkAdjList(Network network) {
    super("Copy of " + network.getName());
    this.nodes = new HashSet<Node>(network.getNodes());
    this.edges = new EdgeList();
    for (Node node: this.nodes) {
      Set<Node> neighbours = network.getAdjacent(node);
      for (Node neighbour: neighbours) {
        this.edges.addEdge(node, neighbour);
      }
    }
    this.commentHeader = network.getCommentHeader();
    this.species = network.getSpecies();
  }

  public NetworkAdjList clone() {
    NetworkAdjList newNetwork = new NetworkAdjList("Copy of " + this.name);
    newNetwork.nodes = new HashSet<Node>(this.nodes);
    newNetwork.edges = new EdgeList(this.edges);
    newNetwork.commentHeader = this.commentHeader;
    newNetwork.species = this.species;
    return newNetwork;
  }

  /**
   * Returns whether this Network contains the given Node
   *
   * @param node - the node whose membership is being queried
   * @return
   */
  public boolean containsNode(Node node) {
    return this.nodes.contains(node);
  }

  /**
   * Returns the Node in this network that is the same as the provided Node.
   * @param vertex
   * @return
   */
  public Node getNode(Node vertex) {
    return vertex; // Testing.
//    for (Node node: this.nodes) {
//      if (node.equals(vertex)) {
//        return node;
//      }
//    }
//    return null;
  }


  /**
   * Gets a the Nodes of this Network object, by reference.
   * @return
   */
  public HashSet<Node> getNodes() {
    return new HashSet<Node>(this.nodes);
  }

  /**
   * Adds the given Node to this network.
   *
   * @param node - the Node to be added
   * @throws Exception
   */
  public void addNode(Node node) throws Exception {
    if (this.containsNode(node)) {
      throw new Exception("Cannot add node " + node.toString() + " to network. Already exists.");
    }
    this.nodes.add(node);
  }


  /**
   * Remove the specified Node from the network.
   * @param node - the Node to be removed
   * @throws Exception - if the Node does not exist in the network
   */
  public void removeNode(Node node) throws Exception {
    if (!this.containsNode(node)) {
      throw new Exception("Cannot remove node " + node.toString() + " from network " + this.toString() + " Does not exist.");
    }
    this.nodes.remove(node);
    this.edges.removeNode(node);
  }

  @Override
  public Set<PairOfStrings> getEdges() {
    HashSet<PairOfStrings> result = new HashSet<PairOfStrings>((int) Math.ceil(this.edges.getSize() * 0.75));
    ArrayList<Node> nodeList = new ArrayList<Node>(this.nodes);
    Collections.sort(nodeList);
    for (int i=0; i<nodeList.size(); i++) {
      for (int j=i; j<nodeList.size(); j++) {
        if (this.edges.areAdjacent(nodeList.get(i), nodeList.get(j))) {
          result.add(new PairOfStrings(nodeList.get(i).name, nodeList.get(j).name));
        }
      }
    }
    return result;
  }

  /**
   * Adds an edge between the two Nodes in this Network. (Note, the Nodes provided need only be
   * identical to the Nodes in this Network, not necessarily the same Nodes.)
   *
   * @param node1 - the first Node in the edge
   * @param node2 - the second Node in the edge
   * @throws Exception
   */
  public void addEdge(Node node1, Node node2) throws Exception {
    if (this.containsNode(node1) && this.containsNode(node2)) {
      this.edges.addEdge(node1, node2);
    }
    else {
      throw new Exception("Can't add edge between non-existent vertices!");
    }
  }



  /**
   * Removes an edge from this network. Bi-directional.
   *
   * @param node1 - one of the two endpoints of the edge to be removed
   * @param node2 - the other endpoint of the edge to be removed
   * @throws Exception - if the two nodes are not connected
   */
  public void removeEdge(Node node1, Node node2) throws Exception {
    if (this.containsNode(node1) && this.containsNode(node2)) {
      this.edges.removeEdge(node1, node2);
    }
    else {
      throw new Exception("Can't remove edge between non-existent vertices!");
    }
  }

  /**
   * Setter method for the commentHeader attribute
   *
   * @param comment - the new comment header
   */
  public void setComment(String comment) {
    this.commentHeader = comment;
  }

  /**
   * Getter method for the commentHeader attribute
   *
   * @return
   */
  public String getComment() {
    return this.commentHeader;
  }

  /**
   * Get the number of vertices in this Network
   *
   * @return
   */
  public int getNumVertices() {
    return this.nodes.size();
  }

  /**
   * Get the number of edges in this Network
   *
   * @return
   */
  public int getNumEdges() {
    return this.edges.getSize();
  }

  /**
   * Gets the Nodes in the Network adjacent to the specified Node.
   *
   * @param node - Node whose neighbours we want to retrieve
   * @return
   */
  public Set<Node> getAdjacent(Node node) {
    return this.edges.getAdjacent(node);
  }

  /**
   * Gets the degree of the specified Node in this Network. Just counts the #edges - will include
   * any self-loops.
   *
   * @param node
   * @return
   */
  public int getDegree(Node node) {
    return this.edges.getDegree(node);
  }

  /**
   * Check if the two specified Nodes are adjacent within the Network.
   *
   * @param node1
   * @param node2
   * @return
   */
  public boolean areAdjacent(Node node1, Node node2) {
    return this.edges.areAdjacent(node1, node2);
  }

  /**
   * @return the commentHeader
   */
  public String getCommentHeader() {
    return commentHeader;
  }

  /**
   * @param commentHeader the commentHeader to set
   */
  public void setCommentHeader(String commentHeader) {
    this.commentHeader = commentHeader;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the species
   */
  public String getSpecies() {
    return species;
  }

  /**
   * @param species the species to set
   */
  public void setSpecies(String species) {
    this.species = species;
  }

  /**
   * Write this Network to a file.
   *
   * @param filename - the filename of the network file to be written
   * @throws IOException
   */
  public void writeToFile(String filename) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(filename));
    StringBuffer output = new StringBuffer();

    output.append(this.commentHeader);
    if (!this.commentHeader.endsWith("\n")) {
      output.append("\n");
    }

    // Get all the nodes in this network. Sort them for output.
    ArrayList<Node> nodeList = new ArrayList<Node>(this.nodes);
    Collections.sort(nodeList);

    for (Node node1: nodeList) {
      ArrayList<Node> neighbourList = new ArrayList<Node>(this.edges.getAdjacent(node1));
      Collections.sort(neighbourList);

      for (Node neighbour: neighbourList) {
        // Print only if first node is alphabetically before second node. Self-loops allowed.
        if (node1.compareTo(neighbour) <= 0) {
          output.append(node1.name + "\t" + neighbour.name + "\n");
        }
      }
    }

    out.write(output.toString());

    out.close();
  }

  public static void main(String[] args) throws Exception {
    NetworkAdjList test = new NetworkAdjList("../DomainEvolution/H.sapiens/H.sapiens.ppin.txt", "H.sapiens");
    System.out.println(test.commentHeader);
    System.out.println(test.getNumVertices());
    System.out.println(test.getNumEdges());
  }

}
