package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class VNode extends JSONObject implements Comparable<VNode>, JSONString {

  private HashSet<String> classes = new HashSet<String>();

  public VNode(String id, String name) {
    this.put("data", new JSONObject());
    this.setId(id);
    this.setName(name);
    this.setWeight(75);

    this.put("position", new JSONObject());
    this.setX(0);
    this.setY(0);

    this.put("group", "nodes");
    this.put("removed", false);
    this.put("selected", false);
    this.put("selectable", true);
    this.put("locked", false);
    this.put("grabbable", true);
  }


  public VNode(String id, String name, double x, double y) {
    this(id, name);
    this.setX(x);
    this.setY(y);
  }

  public VNode(String id, String name, int X, int Y, Collection<String> classes) {
    this(id, name, X, Y);
    this.setClasses(classes);
  }

  public VNode(String id, String name, int X, int Y, String classes) {
    this(id, name, X, Y);
    this.setClasses(classes);
  }

  public static VNode childNode(String id, String name, int X, int Y, String parent) {
    VNode result = new VNode(id, name, X, Y);

    result.getJSONObject("data").put("parent", parent);

    return result;
  }

  public static VNode childNode(String id, String name, int X, int Y, Collection<String> classes, String parent) {
    VNode result = VNode.childNode(id, name, X, Y, parent);

    result.setClasses(classes);

    return result;
  }

  public static VNode childNode(String id, String name, int X, int Y, String classes, String parent) {
    VNode result = VNode.childNode(id, name, X, Y, parent);

    result.setClasses(classes);

    return result;
  }

  public static VNode secretNode(String id) {
    VNode newNode = new VNode(id, id);

    newNode.setWeight(0);
    newNode.put("group", "nodes");
    newNode.put("removed", false);
    newNode.put("selected", false);
    newNode.put("selectable", false);
    newNode.put("locked", true);
    newNode.put("grabbable", false);

    newNode.setClass("hidden");

    return newNode;
  }

  public void setId(String id) {
    this.getJSONObject("data").put("id", id);
  }

  public void setName(String name) {
    this.getJSONObject("data").put("name", name);
  }

  public void setWeight(int weight) {
    this.getJSONObject("data").put("weight", weight);
  }

  public void setPartner(Collection<String> partnerIds) {
    this.getJSONObject("data").put("partners", partnerIds);
  }

  public void addPartner(String partnerId) {
    if (!this.getJSONObject("data").has("partners")) {
      ArrayList<String> newPartners = new ArrayList<String>();
      newPartners.add(partnerId);
      this.setPartner(newPartners);
    }
    else {
      this.getJSONObject("data").getJSONArray("partners").put(partnerId);
    }
  }

  public String getId() {
    return this.getJSONObject("data").getString("id");
  }

  public String getName() {
    return this.getJSONObject("data").getString("name");
  }

  public int getWeight() {
    return this.getJSONObject("data").getInt("weight");
  }

  public List<String> getPartners() {
    ArrayList<String> returnValue = new ArrayList<String>(this.getJSONObject("data").getJSONArray("partners").length());
    for (int i=0; i<this.getJSONObject("data").getJSONArray("partners").length(); i++) {
      returnValue.add(this.getJSONObject("data").getJSONArray("partners").getString(i));
    }

    return returnValue;
  }

  public boolean hasPartner(String partner) {
    for (int i=0; i<this.getJSONObject("data").getJSONArray("partners").length(); i++) {
      if (partner.equals(this.getJSONObject("data").getJSONArray("partners").getString(i))) {
        return true;
      }
    }
    return false;
  }

  public void setX(double x) {
    this.getJSONObject("position").put("x", x);
  }

  public void setY(double y) {
    this.getJSONObject("position").put("y", y);
  }

  public double getX() {
    return this.getJSONObject("position").getDouble("x");
  }

  public double getY() {
    return this.getJSONObject("position").getDouble("y");
  }

  public void setParent(String parent) {
    this.getJSONObject("data").put("parent", parent);
  }

  public void setParent(VNode parent) {
    this.getJSONObject("data").put("parent", parent.getId());
  }

  public String getParent() {
    if (this.getJSONObject("data").has("parent")) {
      return this.getJSONObject("data").getString("parent");
    }
    else {
      return null;
    }
  }

  public void removeParent() {
    this.getJSONObject("data").remove("parent");
  }

  public void setClasses(Collection<String> classes) {
    this.classes = new HashSet<String>(classes);
  }

  public void setClasses(String classesString) {
    String[] classes = classesString.split("[ \t]+");
    for (String nodeClass: classes) {
      this.setClass(nodeClass);
    }
  }

  public void setClass(String nodeClass) {
    this.setClass(nodeClass, true);
  }

  public void setClass(String nodeClass, boolean value) {
    if (value) {
      this.classes.add(nodeClass);
    }
    else {
      this.classes.remove(nodeClass);
    }
  }

  public Set<String> getClasses() {
    return Collections.unmodifiableSet(this.classes);
  }

  public boolean checkClass(String nodeClass) {
    return this.classes.contains(nodeClass);
  }

  public boolean addClass(String nodeClass) {
    if (this.classes.contains(nodeClass)) {
      return false;
    }
    else {
      this.classes.add(nodeClass);
      return true;
    }
  }

  public boolean addClasses(String nodeClasses) {
    return this.addClasses(Arrays.asList(nodeClasses.split("[ \t]+")));

  }

  public boolean addClasses(Collection<String> nodeClasses) {
    boolean allAdded = true;
    for (String nodeClass: nodeClasses) {
      allAdded = allAdded && this.addClass(nodeClass);
    }
    return allAdded;
  }

  public boolean removeClass(String nodeClass) {
    return this.classes.remove(nodeClass);
  }

  public boolean removeClasses(Collection<String> nodeClasses) {
    boolean allRemoved = true;
    for (String nodeClass: nodeClasses) {
      allRemoved = allRemoved && this.removeClass(nodeClass);
    }
    return allRemoved;
  }

  public boolean removeClasses(String nodeClasses) {
    return this.removeClasses(Arrays.asList(nodeClasses.split("[ \t]+")));
  }

  @Override
  public String toString(int indentFactor) {
    if (this.classes.isEmpty()) {
      this.remove("classes");
    }
    else {
      ArrayList<String> sortedClasses = new ArrayList<String>(this.getClasses());
      Collections.sort(sortedClasses);
      this.put("classes", String.join(" ", sortedClasses));
    }
    return super.toString(indentFactor);
  }

  public int hashCode() {
    final int prime = 31;
    int hash = 1;
    hash = prime * hash + ((this.getId() == null) ? 0 : this.getId().hashCode());
//    hash = prime * hash + ((this.getName() == null) ? 0 : this.getName().hashCode());
//    hash = prime * hash + this.getWeight();
//    hash = (int) (prime * hash + this.getX());
//    hash = (int) (prime * hash + this.getY());
//    hash = prime * hash + ((this.getParent() == null) ? 0 : this.getParent().hashCode());
//    hash = prime * hash + ((this.getClasses() == null) ? 0 : this.getClasses().hashCode());
    return hash;
  }


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof VNode)) {
      return false;
    }
    VNode other = (VNode) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    }
    else if (!this.getId().equals(other.getId())) {
      return false;
    }
    if (this.getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else if (!this.getName().equals(other.getName())) {
      return false;
    }
    if (classes == null) {
      if (other.classes != null) {
        return false;
      }
    }
    else if (!classes.equals(other.classes)) {
      return false;
    }
    if (this.getWeight() != other.getWeight() || this.getX() != other.getX() || this.getY() != other.getY()) {
      return false;
    }
    if (this.getParent() == null) {
      if (other.getParent() != null) {
        return false;
      }
    }
    else if (!this.getParent().equals(other.getParent())) {
      return false;
    }
        return true;
  }


  @Override
  public int compareTo(VNode o) {
    return this.getName().compareTo(o.getName());
  }

  public static void main(String[] args) {
    VNode test = VNode.childNode("ENSP345345", "ITSN1", 50, 100, "test\ttest2", "interologs");
    VNode test2 = VNode.childNode("ENSP345345", "ITSN1", 50, 100, "test\ttest2", "interologs");
    System.out.println(test.toString());

  }


  @Override
  public String toJSONString() {
    if (this.classes.isEmpty()) {
      this.remove("classes");
    }
    else {
      ArrayList<String> sortedClasses = new ArrayList<String>(this.getClasses());
      Collections.sort(sortedClasses);
      this.put("classes", String.join(" ", sortedClasses));
    }

    return super.toString(2);
  }





}
