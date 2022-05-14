package processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import data.Fasta;
import data.Mapping;
import data.Sequence;
import functions.Constants;
import functions.Static;

public class Fastas {


  /**
   * Processes a raw Ensembl peptide fasta download. Strips out some "bad" genes, inserts gene
   * names.
   * Input files: data/??//???.pep.all.##.fasta, data/???/???.ensembl.##.txt
   * Output files: data/???.pep.processed.##.fasta
   *
   * @param species
   * @throws Exception
   */
  public static void processFastaFile(String species) throws Exception {
    Fasta fasta = new Fasta("data/" + species + "/" + species + ".pep.all." + Constants.FILE_VERSION + ".fasta", " ", 0, " ", 0, " ", 1);
    Mapping id2name = new Mapping("data/" + species + "/" + species + ".ensembl" + Constants.FILE_VERSION + ".txt", "\t", 3, 0);

    ArrayList<String> idOrder = new ArrayList<String>(fasta.ids2sequences.keySet());
    Collections.sort(idOrder);

    HashSet<String> newNames = new HashSet<String>();

    for (String id : idOrder) {
      Sequence sequence = fasta.ids2sequences.get(id);
      String description = sequence.getDescription();
      String newName = id;
      if (description.contains(":MT:") || description.contains(":MtDNA:") || description.contains(":dmel_mitochondrion_genome:") || description.contains(":Mito:")) {
        fasta.names2ids.remove(sequence.getName());
        fasta.ids2sequences.remove(id);
        continue;
      }

      try {
        newName = Static.getFirstFromSet(id2name.getForward(id)).toUpperCase();
      } catch (NullPointerException e) {
        Static.debugOutput("Couldn't process protein " + id);
      }
      sequence.setName(newName);

      newNames.add(newName);

      HashSet<String> ids = fasta.names2ids.get(id);
      fasta.names2ids.remove(id);
      fasta.names2ids.putIfAbsent(newName, new HashSet<String>());
      fasta.names2ids.get(newName).addAll(ids);
    }

    fasta.sequenceNameOrder = new ArrayList<String>(newNames);

    fasta.toFile("data/" + species + "/" + species + ".pep.processed." + Constants.FILE_VERSION + ".fasta", true);
  }



/*  public static void main(String[] args) throws Exception {
    for (String species: Constants.SPECIESES2) {
      Fastas.processFastaFile(species);

      Fasta fasta = new Fasta("data/" + species + "/" + species + ".pep.processed." + Constants.FILE_VERSION + ".fasta", "(  )|\t", 0, "(  )|\t", 1, "(  )|\t", 2);
      fasta.filterLongestSequences().toFile("data/" + species + "/" + species + ".pep.longest." + Constants.FILE_VERSION + ".fasta");
    }
  }*/
}
