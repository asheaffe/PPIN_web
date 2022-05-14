package data;


public class Node implements Comparable<Node> {

	public final String name;
	public final String species;
	
	public Node(String name, String species) {
		this.name = name;
		this.species = species;
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
    if (!(obj instanceof Node)) {
      return false;
    }
    Node other = (Node) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (species == null) {
      if (other.species != null) {
        return false;
      }
    }
    else if (!species.equals(other.species)) {
      return false;
    }
    return true;
  }



  public int compareTo(Node other) {
		if (!this.species.equalsIgnoreCase(other.species)) {
			return this.species.compareTo(other.species);
		}
		return this.name.compareTo(other.name);
	}
	
	/* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((species == null) ? 0 : species.hashCode());
    return result;
  }
	
	/**
	 * Gets the protein name of this NON-protein node. (Name is of the form ProteinName,startIndex,endIndex).
	 * @return
	 * @throws Exception
	 */
	public String getProteinName() throws Exception {
		if (this.name.indexOf(',') == -1) {
			throw new Exception("Can't get the protein name of " + this.toString());
		}
		return this.name.substring(0, this.name.indexOf(','));
	}
	
	public String toString() {
		return "Species " + this.species + ": " + this.name;
	}

	
	
}
