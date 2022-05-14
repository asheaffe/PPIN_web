package data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import data.Node;
import data.PairOfStrings;

public abstract class Network {

  protected String commentHeader;
  protected String name;
  protected String species;

  public Network(String name) {
    this.commentHeader = "";
    this.name = name;
    this.species = name;
  }

  public abstract Network clone();

  /**
   * Returns whether this Network contains the given Node
   *
   * @param node - the node whose membership is being queried
   * @return
   */
  public abstract boolean containsNode(Node node);

  /**
   * Returns whether this Network contains a Node of the given name
   *
   * @param name - the name of the Node(s) being queried
   * @return
   */
  public boolean containsNode(String name) {
    return this.containsNode(name, this.species);
  }

  /**
   * Returns whether this Network contains a Node with the given name and species.
   *
   * @param name
   * @param species
   * @return
   */
  public boolean containsNode(String name, String species) {
    return this.containsNode(new Node(name, species));
  }

  /**
   * Returns the Node in this network that is the same as the provided Node.
   * @param vertex
   * @return
   */
  public Node getNode(Node vertex) {
    return vertex;
  }

  /**
   * Returns the node in the network with the given name and the species of this network.
   * Will arbitrarily return one if there are multiple such nodes.
   *
   * @param name - name of the node to be returned
   * @return - the node in this network of the given name; null otherwise.
   */
  public Node getNode(String name) {
    return this.getNode(new Node(name, this.species));
  }

  /**
   * Returns the node in the network with the given name and species.
   *
   * @param name - name of the node to be returned
   * @param species - species of the node to be returned
   * @return - the node in this network of the given name and species; null otherwise.
   */
  public Node getNode(String name, String species) {
    return this.getNode(new Node(name, species));
  }

  /**
   * Gets a the Nodes of this Network object, by reference.
   * @return
   */
  public abstract Set<Node> getNodes();

  /**
   * Adds the given Node to this network.
   *
   * @param node - the Node to be added
   * @throws Exception
   */
  public abstract void addNode(Node node) throws Exception;

  /**
   * Adds a new Node with the given name to this network. The Node's species will be initialized to
   * the network's species.
   *
   * @param name - the name of the Node to be added.
   * @throws Exception
   */
  public void addNode(String name) throws Exception {
    if (this.containsNode(name, this.species)) {
      throw new Exception("Cannot add new node " + new Node(name, this.species).toString()
          + " to network. Already exists.");
    }

    this.addNode(new Node(name, this.species));
  }

  /**
   * Adds a new Node with the given name and species to this network.
   *
   * @param name - the name of the Node to be added
   * @param species - the species of the Node to be added
   * @throws Exception
   */
  public void addNode(String name, String species) throws Exception {
    if (this.containsNode(name, species)) {
      throw new Exception("Cannot add new node with name " + name + " and species " + species
          + " to network. Already exists.");
    }
    this.addNode(new Node(name, species));
  }

  /**
   * Remove the specified Node from the network.
   * @param node - the Node to be removed
   * @throws Exception - if the Node does not exist in the network
   */
  public abstract void removeNode(Node node) throws Exception;

  /**
   * Remove the node with the specified name from the network. The default species of the network is used.
   * @param name - the name of the node to be removed
   * @throws Exception - if the node does not exist in the network
   */
  public void removeNode(String name) throws Exception {
    this.removeNode(new Node(name, this.species));
  }

  /**
   * Remove the node with the specified name and species from the network.
   * @param name - the name of the node to be removed
   * @param species - the species of the node to be removed
   * @throws Exception - if the node does not exist in the network
   */
  public void removeNode(String name, String species) throws Exception {
    this.removeNode(new Node(name, species));
  }

  public abstract Set<PairOfStrings> getEdges();
  /**
   * Adds an edge between the two Nodes in this Network. (Note, the Nodes provided need only be
   * identical to the Nodes in this Network, not necessarily the same Nodes.)
   *
   * @param node1 - the first Node in the edge
   * @param node2 - the second Node in the edge
   * @throws Exception
   */
  public abstract void addEdge(Node node1, Node node2) throws Exception;

  /**
   * Adds an edge between the Nodes in this Network with the specified names and species equal to
   * the Network's name.
   *
   * @param name1 - name of the first Node in the edge
   * @param name2 - name of the second Node in the edge
   * @throws Exception
   */
  public void addEdge(String name1, String name2) throws Exception {
    this.addEdge(this.getNode(name1), this.getNode(name2));
  }

  /**
   * Adds an edge between the Nodes in this Network with the specified names and specieses.
   *
   * @param name1 - name of the first Node in the edge
   * @param species1 - species of the first Node in the edge
   * @param name2 - name of the second Node in the edge
   * @param species2 - species of the second Node in the edge
   * @throws Exception
   */
  public void addEdge(String name1, String species1, String name2, String species2) throws Exception {
    this.addEdge(this.getNode(name1, species1), this.getNode(name2, species2));
  }

  /**
   * Removes an edge from this network. Bi-directional.
   *
   * @param node1 - one of the two endpoints of the edge to be removed
   * @param node2 - the other endpoint of the edge to be removed
   * @throws Exception - if the two nodes are not connected
   */
  public abstract void removeEdge(Node node1, Node node2) throws Exception;

  /**
   * Removes an edge between the Nodes in this Network with the specified names and species equal to
   * the Network's name.
   *
   * @param node1 - one of the two endpoints of the edge to be removed
   * @param node2 - the other endpoint of the edge to be removed
   * @throws Exception - if the two nodes are not connected
   */
  public void removeEdge(String name1, String name2) throws Exception {
    this.removeEdge(new Node(name1, this.species), new Node(name2, this.species));
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
  public abstract int getNumVertices();

  /**
   * Get the number of edges in this Network
   *
   * @return
   */
  public abstract int getNumEdges();

  /**
   * Gets a read-only set of the Nodes in the Network adjacent to the specified Node.
   *
   * @param node - Node whose neighbours we want to retrieve
   * @return
   */
  public abstract Set<Node> getAdjacent(Node node);

  /**
   * Gets the Nodes in the Network adjacent to the node with specified name and the same species as
   * this Network's name.
   *
   * @param name - name of Node whose neighbours we want to retrieve
   * @return
   */
  public Set<Node> getAdjacent(String name) {
    return this.getAdjacent(new Node(name, this.species));
  }

  /**
   * Gets the Nodes in the Network adjacent to the node with specified name and species
   *
   * @param name - name of Node whose neighbours we want to retrieve
   * @param species - species of Node whose neighbours we want to retrieve
   * @return
   */
  public Set<Node> getAdjacent(String name, String species) {
    return this.getAdjacent(new Node(name, species));
  }

  /**
   * Gets the degree of the specified Node in this Network. Just counts the #edges - will include
   * any self-loops.
   *
   * @param node
   * @return
   */
  public abstract int getDegree(Node node);

  /**
   * Gets the degree of the specified Node in this Network. Just counts the #edges - will include
   * any self-loops.
   *
   * @param name - name of Node whose neighbours we want to retrieve
   * @return
   */
  public int getDegree(String name) {
    return this.getDegree(new Node(name, this.species));
  }

  /**
   * Gets the degree of the specified Node in this Network. Just counts the #edges - will include
   * any self-loops.
   *
   * @param name - name of Node whose neighbours we want to retrieve
   * @param species - species of Node whose neighbours we want to retrieve
   * @return
   */
  public int getDegree(String name, String species) {
    return this.getDegree(new Node(name, species));
  }

  /**
   * Check if the two specified Nodes are adjacent within the Network.
   *
   * @param node1
   * @param node2
   * @return
   */
  public abstract boolean areAdjacent(Node node1, Node node2);

  /**
   * Check if the two named nodes are adjacent within the Network
   *
   * @param node1 - the name of one of the nodes
   * @param node2 - the name of the other node
   * @return
   */
  public boolean areAdjacent(String name1, String name2) {
    return this.areAdjacent(new Node(name1, this.species), new Node(name2, this.species));
  }

  /**
   * @return the commentHeader
   */
  public String getCommentHeader() {
    return this.commentHeader;
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
    return this.name;
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
    return this.species;
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
    ArrayList<Node> nodeList = new ArrayList<Node>(this.getNodes());
    Collections.sort(nodeList);

    for (Node node1: nodeList) {
      ArrayList<Node> neighbourList = new ArrayList<Node>(this.getAdjacent(node1));
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
}
