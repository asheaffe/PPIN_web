package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import data.Node;
import java.io.BufferedReader;
import java.io.FileReader;


public class EdgeList {

  public HashMap<Node, HashSet<Node>> edges;
  private String filename;
  private int size = 0;

  public EdgeList() {
    this.edges = new HashMap<Node, HashSet<Node>>();
  }

  public EdgeList(String filename, String species) throws Exception {
    this();
    this.filename = filename;
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line = "";

    while ((line = in.readLine()) != null) {
      String[] entries = line.split("\t");
      /**
       * if (entries[0].matches(".*,.*,.*,.*")) { entries[0] = entries[0].split(",")[0] + "," +
       * entries[0].split(",")[2] + "," + entries[0].split(",")[3]; } if
       * (entries[1].matches(".*,.*,.*,.*")) { entries[1] = entries[1].split(",")[0] + "," +
       * entries[1].split(",")[2] + "," + entries[1].split(",")[3]; }
       */
      this.addEdge(new Node(entries[0], species), new Node(entries[1], species));
    }

    in.close();
  }

  /**
   * Create a copy of an existing EdgeList.
   *
   * @param orig - the original EdgeList
   */
  public EdgeList(EdgeList orig) {
    this();
    this.filename = orig.filename;
    for (Node node1: orig.edges.keySet()) {
      for (Node node2: orig.edges.get(node1)) {
        this.addEdge(node1, node2);
      }
    }
  }

  /**
   *
   * @param node1
   * @param node2
   */
  public void addEdge(Node node1, Node node2) {
    if (!this.areAdjacent(node1, node2)) {
      this.edges.putIfAbsent(node1, new HashSet<Node>());
      this.edges.putIfAbsent(node2, new HashSet<Node>());

      this.edges.get(node1).add(node2);
      this.edges.get(node2).add(node1);
      this.size++;
    }
  }


  /**
   * Removes a specified edge from this Edgelist. Bi-directional.
   * @param node1 - one of the two endpoints of the edge to be removed
   * @param node2 - the other endpoint of the edge to be removed
   * @throws Exception - if the two nodes are not connected
   */
  public void removeEdge(Node node1, Node node2) throws Exception {
    if (!this.edges.containsKey(node1) || !this.edges.containsKey(node2) || !this.areAdjacent(node1, node2)) {
      throw new Exception("Can't remove non-existent edge!");
    }

    this.edges.get(node1).remove(node2);
    this.edges.get(node2).remove(node1);

    this.size--;
  }

  /**
   * Remove a node, and all its edges, from this edgelist.
   * @param node
   * @throws Exception
   */
  public void removeNode(Node node) throws Exception {
    if (!this.edges.containsKey(node)) {
      throw new Exception("Can't remove non-existent node from edge set!");
    }

    for (Node neighbour: new ArrayList<Node>(this.edges.get(node))) {
      this.removeEdge(node, neighbour);
    }
    this.edges.remove(node);
  }

  public boolean areAdjacent(Node node1, Node node2) {
    return this.edges.containsKey(node1) && this.edges.get(node1).contains(node2);
  }

  /**
   * Gets the Nodes in this EdgeList adjacent to the specified Node.
   *
   * @param node
   * @return
   */
  public Set<Node> getAdjacent(Node node) {
    if (this.edges.containsKey(node)) {
      return Collections.unmodifiableSet(this.edges.get(node));
    }
    return Collections.emptySet();
  }

  /**
   * Gets the degree of the specified Node in this EdgeList. Just counts the #edges - will include
   * any self-loops.
   *
   * @param node
   * @return
   */
  public int getDegree(Node node) {
    return this.edges.get(node).size();
  }


  /**
   * Gets the number of edges in this EdgeList.
   *
   * @return
   */
  public int getSize() {
    return this.size;
  }

  public Set<Node> getNodes() {
    return Collections.unmodifiableSet(this.edges.keySet());
  }

  public Map<Node, HashSet<Node>> getEdges() {
    return Collections.unmodifiableMap(this.edges);
  }

}
