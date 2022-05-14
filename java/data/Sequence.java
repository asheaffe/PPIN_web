package data;

import java.util.HashSet;
import java.util.Set;

public class Sequence {

	private String name;
	private String id;
	private String description;
	private String sequence;

	public Sequence(String name, String id, String description, String sequence) {
		this.setName(name);
		this.setId(id);
		this.setDescription(description);
		this.setSequence(sequence);
	}

	public Sequence(Sequence sequence) {
		this.setName(sequence.getName());
		this.setId(sequence.getId());
		this.setDescription(sequence.getDescription());
		this.setSequence(sequence.getSequence());
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 *
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 *
	 * @param sequence
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 *
	 * @return
	 */
	public int getLength() {
		return this.sequence.length();
	}

	/**
	 * Compares two sequences to see if they're the same. Ignores name and description (use equals for that).
	 * @param other - other Sequence to be compared to
	 * @return - True if the two sequences are the same string; False otherwise
	 */
	public boolean isEquivalent(Sequence other) {
		return this.sequence.equals(other.sequence);
	}

	/**
	 * Get the string-rep of the sequences of a set of Sequence objects
	 * @param sequences - the Sequence objects whose string-rep sequences are to be retrieved
	 * @return
	 */
	public static Set<String> extractSequences(Set<Sequence> sequences) {
		HashSet<String> result = new HashSet<String>();

		for (Sequence sequence: sequences) {
			result.add(sequence.getSequence());
		}

		return result;
	}

	@Override
	public int hashCode() {
		return (this.name + this.id + this.description + this.sequence).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (this == other) {
			return true;
		}

		if (!(other instanceof Sequence)) {
			return false;
		}

		Sequence otherSequence = (Sequence)other;

		return this.name.equals(otherSequence.name) && this.id.equals(otherSequence.id) &&
				this.description.equals(otherSequence.description) && this.sequence.equals(otherSequence.sequence);
	}

	public String toString() {
		return this.name + " - " + this.id + " - " + this.description + "\n" + this.sequence;
	}
}
