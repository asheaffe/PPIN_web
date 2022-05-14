package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import data.Mapping;
import data.Sequence;
import functions.Constants;
import functions.Static;

/*
 * Takes the complete FASTA file of worm ORFs and trims it down to just the ones of interest (i.e.
 * those in our network). Also replaces the sequence name originally used in the FASTA file with the
 * gene name.
 */

public class Fasta {

  public ArrayList<String> sequenceNameOrder;
  public HashMap<String, HashSet<String>> names2ids;
  public HashMap<String, Sequence> ids2sequences;

  public Fasta() {
    this.sequenceNameOrder = new ArrayList<String>();
    this.names2ids = new HashMap<String, HashSet<String>>();
    this.ids2sequences = new HashMap<String, Sequence>();
  }

  /**
   * Reads a FASTA file, then extracts and stores all its sequence information. The entire
   * description line is taken as both the name and id.
   *
   * @param fastaFile - the FASTA file to be read
   * @throws IOException
   */
  public Fasta(String fastaFile) throws IOException {
    this(fastaFile, "\n", 0, "\n", 0, null, -1);
  }

  /**
   * Reads a FASTA file, then extracts and stores all its sequence information. The line is split
   * once according to the regex, with the first part used as both the sequence name ID and the last
   * part being sequence description.
   *
   * @param fastaFile
   * @param regex
   * @throws IOException
   */
  public Fasta(String fastaFile, String regex) throws IOException {
    this(fastaFile, regex, 0, regex, 0, regex, 1);
  }

  /**
   * Reads a FASTA file, then extracts and stores all its sequence information.
   *
   * id is mandatory. Name and description are not, and should be specified with null if not.
   *
   * @param fastaFile - the FASTA file to be read
   * @param nameRegex - a regex for extracting the sequence name out of the description line
   * @param nameIndex - index of the name in the array created by splitting the description on the nameRegex
   * @param idRegex - a regex for extracting the sequence ID out of the description line
   * @param idIndex - index of the ID in the array created by splitting the description on the idRegex
   * @param descRegex - a regex for extracting the sequence description out of the description line
   * @param descTimes - index of the ID in the array created by splitting the description on the descRegex;
   *                    the regex will be used to split descTimes times, and everything AFTER the descTimes-th time will be included as the description
   * @throws IOException
   */
  public Fasta(String fastaFile, String nameRegex, int nameIndex, String idRegex, int idIndex, String descRegex, int descTimes) throws IOException {
    this.sequenceNameOrder = new ArrayList<String>();
    this.names2ids = new HashMap<String, HashSet<String>>();
    this.ids2sequences = new HashMap<String, Sequence>();

    BufferedReader in = new BufferedReader(new FileReader(fastaFile));
    String line = Static.skipBlankCommentLines(in);

    while (line != null) {
      StringBuffer sequenceBuffer = new StringBuffer();
      line = line.substring(1); // Ignore initial > character

      String[] split = line.split(idRegex);
      String id = split[idIndex];

      // Assume no name specified; defaults to same as ID
      String name = id;
      // Assume no description specified
      String description = "";

      // If name specified...
      if (nameRegex != null) {
        split = line.split(nameRegex);
        name = split[nameIndex];
      }

      if (descRegex != null) {
        split = line.split(descRegex, descTimes + 1);
        description = split[descTimes];
      }

      line = Static.skipBlankCommentLines(in);
      do {
        sequenceBuffer.append(line);
      } while ((line = Static.skipBlankCommentLines(in)) != null && !line.startsWith(">"));

      Sequence newSequence = new Sequence(name, id, description, sequenceBuffer.toString());
      this.addSequence(name, id, newSequence);
    }

    in.close();
  }

  /**
   * Return the number of sequences in this FASTA object.
   *
   * @return - the number of sequences in this FASTA object
   */
  public int getSize() {
    int size = 0;
    for (String sequenceName : this.sequenceNameOrder) {
      size += this.names2ids.get(sequenceName).size();
    }
    return size;
  }

  /**
   * Add a FASTA sequence to this Fasta object.
   * @param name - the name of the FASTA sequence
   * @param id - the ID of the FASTA sequence ~ should be unique
   * @param sequence - the Sequence object to be added
   */
  public void addSequence(String name, String id, Sequence sequence) {
    if (!this.names2ids.containsKey(name)) {
      this.sequenceNameOrder.add(name);
      this.names2ids.put(name, new HashSet<String>(1));
    }
    this.names2ids.get(name).add(id);
    this.ids2sequences.putIfAbsent(id, sequence);


  }

  /**
   * Remove any sequences in this Fasta object that are associated with the given sequence name.
   * @param name - the name of the FASTA sequence(s) to be removed
   */
  public void removeSequences(String name) {
    for (String id : this.names2ids.get(name)) {
      this.ids2sequences.remove(id);
    }
    this.sequenceNameOrder.remove(name);
    this.names2ids.remove(name);
  }

  /**
   * Return whether this Fasta object contains a FASTA sequence with the associated ID.
   */
  public boolean containsSequenceId(String id) {
    return this.ids2sequences.containsKey(id);
  }

  /**
   * Return whether this Fasta object contains any FASTA sequences with the associated names.
   */
  public boolean containsSequenceByName(String name) {
    return this.names2ids.containsKey(name);
  }

  /**
   * Get the FASTA sequence in this Fasta object with the corresponding id.
   *
   * @param id - the id of the FASTA sequence to be fetched
   * @return - the desired FASTA sequence or null
   */
  public Sequence getSequence(String id) {
    return this.ids2sequences.get(id);
  }

  /**
   * Get the FASTA sequences in this Fasta object with the corresponding name.
   *
   * @param name - the name of the FASTA sequences to be fetched
   * @return - a set of the corresponding FASTA sequences or null
   */
  public HashSet<Sequence> getSequences(String name) {
    HashSet<Sequence> sequences = new HashSet<Sequence>();
    for (String sequenceId : this.names2ids.get(name)) {
      sequences.add(this.ids2sequences.get(sequenceId));
    }

    if (sequences.size() > 0) {
      return sequences;
    }
    return null;
  }

  /**
   * Removes any entries in this Fasta object that are not found in the given network. (By name.)
   * @param network - network whose nodes are to be kept in this Fasta object
   */
  public void filterFastaByNetwork(NetworkAdjList network) {
    ArrayList<String> namesToConsider = new ArrayList<String>(this.sequenceNameOrder);
    for (String proteinName : namesToConsider) {
      if (!network.containsNode(proteinName)) {
        this.removeSequences(proteinName);
      }
    }
  }

  /**
   * Writes the sequences in this Fasta object to outputFile. Overloaded.
   *
   * @param outputFile - the file to which the FASTA sequences should be written
   * @throws IOException
   */
  public void toFile(String outputFile) throws IOException {
    this.toFile(outputFile, false);
  }

  /**
   * Writes the sequences in this Fasta object to outputFile.
   *
   * @param outputFile - the file to which the FASTA sequences should be written
   * @param sortOutput - indicates whether the sequences should be sorted by their names or use
   *        their default ordering (input order)
   * @throws IOException
   */
  public void toFile(String outputFile, boolean sortOutput) throws IOException {
    BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
    StringBuffer output = new StringBuffer();

    ArrayList<String> sequenceNames = this.sequenceNameOrder;
    if (sortOutput) {
      Collections.sort(sequenceNames);
    }

    for (String sequenceName : sequenceNames) {
      ArrayList<String> sequenceIds = new ArrayList<String>(this.names2ids.get(sequenceName));
      Collections.sort(sequenceIds);

      for (String sequenceId : sequenceIds) {

        Sequence sequence = this.ids2sequences.get(sequenceId);

        output.append(">" + sequence.getName());

        if (sequence.getId().length() > 0) {
          output.append("\t" + sequence.getId());
        }

        if (sequence.getDescription().length() > 0) {
          output.append("\t" + sequence.getDescription());
        }

        output.append("\n");

        for (int i = 0; i < sequence.getLength(); i += 60) {
          output.append(sequence.getSequence().substring(i, Math.min(i + 60, sequence.getLength())) + "\n");
        }
      }
    }

    out.write(output.toString());
    out.close();
  }

  /**
   * Writes the sequences in this Fasta object into a number of files in the outputDirectory, with
   * one FASTA sequence per file.
   *
   * @param outputDirectory - the directory to which the files are to be written. Should exist and
   *        be empty.
   * @throws IOException
   */
  public void toFiles(String outputDirectory) throws IOException {
    for (String sequenceName : this.sequenceNameOrder) {
      ArrayList<String> sequenceIds = new ArrayList<String>(this.names2ids.get(sequenceName));
      Collections.sort(sequenceIds);

      for (String sequenceId : sequenceIds) {

        String fileName = sequenceId.replaceAll("[ \t]+", "_");
        fileName = fileName.replaceAll("[<>:\"/\\\\|?\\*]", "");
        fileName = fileName.replaceAll("Domain_", "").replaceAll("#", "");

        BufferedWriter out = new BufferedWriter(new FileWriter(outputDirectory + "/" + fileName + ".fasta"));
        StringBuffer output = new StringBuffer();
        Sequence sequence = this.ids2sequences.get(sequenceName);

        if (sequence.getDescription().length() > 0) {
          output.append(">" + sequence.getName() + " | " + sequence.getDescription() + "\n");
        } else {
          output.append(">" + sequence.getName() + "\n");
        }

        for (int i = 0; i < sequence.getLength(); i += 60) {
          output.append(sequence.getSequence().substring(i, Math.min(i + 60, sequence.getLength())) + "\n");
        }

        out.write(output.toString());
        out.close();
      }
    }
  }

  /**
   * Takes a Fasta object with duplicates and creates a new Fasta object without duplicates, preferentially selecting the longest
   * sequences when they share a name.
   * @return - a new Fasta object with just the longest sequence for each gene
   */
  public Fasta filterLongestSequences() {
    Fasta newFasta = new Fasta();
    HashSet<String> addedNames = new HashSet<String>();

    // Prepare output in same order
    for (String name : this.sequenceNameOrder) {
      String longestId = null;
      Sequence longestSequence = null;

      // Sort IDs before picking longest sequence, so as to break ties consistently
      ArrayList<String> idOrder = new ArrayList<String>(this.names2ids.get(name));
      Collections.sort(idOrder);

      for (String id : idOrder) {
        Sequence sequence = this.ids2sequences.get(id);
        if (longestId == null || sequence.getLength() > longestSequence.getLength()) {
          longestId = id;
          longestSequence = new Sequence(sequence);
        }
      }

      if (!addedNames.contains(name)) {
        newFasta.sequenceNameOrder.add(name);
      }
      addedNames.add(name);
      newFasta.names2ids.putIfAbsent(name, new HashSet<String>());
      newFasta.names2ids.get(name).add(longestId);
      newFasta.ids2sequences.put(longestId, longestSequence);
    }

    return newFasta;
  }



  /**
   * For every FASTA sequence in rewrittenFasta, look for a corresponding sequence in the
   * templateFasta file and use its name/id/description instead.
   *
   * @param templateFasta
   * @param rewrittenFasta
   * @param sortOutput - whether the output should have the FASTA sequences sorted by their "new"
   *        names
   * @throws IOException
   */
  public static void replaceHeaders(Fasta templateFasta, Fasta rewrittenFasta, boolean sortOutput) throws IOException {
    HashMap<String, String> ids2names = new HashMap<String, String>();
    HashMap<String, String> sequences2ids = new HashMap<String, String>();

    for (String id : templateFasta.ids2sequences.keySet()) {
      sequences2ids.put(templateFasta.ids2sequences.get(id).getSequence(), id);
    }

    for (String name : templateFasta.names2ids.keySet()) {
      for (String id : templateFasta.names2ids.get(name)) {
        ids2names.put(id, name);
      }
    }

    ArrayList<String> copyNames = new ArrayList<String>(rewrittenFasta.sequenceNameOrder);
    HashMap<String, HashSet<String>> copyNames2Ids = new HashMap<String, HashSet<String>>(rewrittenFasta.names2ids);
    HashMap<String, Sequence> copyIds2Sequences = new HashMap<String, Sequence>(rewrittenFasta.ids2sequences);

    rewrittenFasta.sequenceNameOrder = new ArrayList<String>();
    rewrittenFasta.names2ids = new HashMap<String, HashSet<String>>();
    rewrittenFasta.ids2sequences = new HashMap<String, Sequence>();

    for (String name : copyNames) {
      for (String id : copyNames2Ids.get(name)) {
        Sequence oldSequence = copyIds2Sequences.get(id);
        String newId = sequences2ids.get(oldSequence.getSequence());
        Sequence templateSequence = templateFasta.getSequence(newId);

        String newName = templateSequence.getName();
        String newDesc = templateSequence.getDescription();

        Sequence newSequence = new Sequence(newName, newId, newDesc, templateSequence.getSequence());

        rewrittenFasta.sequenceNameOrder.add(newName);
        rewrittenFasta.names2ids.putIfAbsent(newName, new HashSet<String>());
        rewrittenFasta.names2ids.get(newName).add(newId);
        rewrittenFasta.ids2sequences.put(newId, newSequence);
      }
    }

    System.out.println(rewrittenFasta.sequenceNameOrder);

    if (sortOutput) {
      Collections.sort(rewrittenFasta.sequenceNameOrder);
    }
  }
}
