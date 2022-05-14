package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

public class VEdge extends JSONObject implements JSONString {

  private HashSet<String> classes = new HashSet<String>();

  public VEdge(String id1, String id2) {
    this.put("data", new JSONObject());
    this.setSource(id1);
    this.setTarget(id2);
    this.setWeight(75);

    this.put("group", "edges");
    this.put("removed", false);
    this.put("selected", false);
    this.put("selectable", true);
    this.put("locked", false);
    this.put("grabbable", true);
  }

  public VEdge(String id1, String id2, Collection<String> classes) {
    this(id1, id2);
    this.setClasses(classes);
  }

  public VEdge(String id1, String id2, String classes) {
    this(id1, id2);
    this.setClasses(classes);
  }

  public void setSource(String source) {
    this.getJSONObject("data").put("source", source);
  }

  public void setTarget(String target) {
    this.getJSONObject("data").put("target", target);
  }

  public void setWeight(int weight) {
    this.getJSONObject("data").put("weight", weight);
  }

  public String getSource() {
    return this.getJSONObject("data").getString("source");
  }

  public String getTarget() {
    return this.getJSONObject("data").getString("target");
  }

  public int getWeight() {
    return this.getJSONObject("data").getInt("weight");
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

  public String toString() {
    if (this.classes.isEmpty()) {
      this.remove("classes");
    }
    else {
      ArrayList<String> sortedClasses = new ArrayList<String>(this.getClasses());
      Collections.sort(sortedClasses);
      this.put("classes", String.join(" ", sortedClasses));
    }
    this.getJSONObject("data").put("id", this.getSource() + "--" + this.getTarget());
    return super.toString();
  }

  public int hashCode() {
    final int prime = 31;
    int hash = 1;
    hash = prime * hash + ((this.getSource() == null) ? 0 : this.getSource().hashCode());
    hash = prime * hash + ((this.getTarget() == null) ? 0 : this.getTarget().hashCode());
//    hash = prime * hash + this.getWeight();
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
    if (!(obj instanceof VEdge)) {
      return false;
    }
    VEdge other = (VEdge) obj;
    if (this.getSource() == null) {
      if (other.getSource() != null) {
        return false;
      }
    }
    else if (!this.getSource().equals(other.getSource())) {
      return false;
    }
    if (this.getTarget() == null) {
      if (other.getTarget() != null) {
        return false;
      }
    }
    else if (!this.getTarget().equals(other.getTarget())) {
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
    if (this.getWeight() != other.getWeight()) {
      return false;
    }
    return true;
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


  public static void main(String[] args) {
  }

}
