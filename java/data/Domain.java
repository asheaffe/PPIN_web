package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import functions.Constants;
import functions.Static;
import processing.Domains;


public class Domain implements Comparable<Domain> {

  public String name;
  public int start;
  public int end;
  public String source;

  public Domain(String name, int start, int end) {
    this.name = name;
    this.start = start;
    this.end = end;
  }

  public Domain(String name, int start, int end, String source) {
    this(name, start, end);
    this.source = source;
  }

  public int length() {
    return this.end - this.start + 1;
  }

  public String toString() {
    return this.name + "," + this.start + "," + this.end + "," + this.source;
  }

  /*
   * Determines whether this Domain object is the same as another object. DOES NOT CHECK
   * Domain.source. This must be checked separately!
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Domain other = (Domain) obj;
    return this.name.equals(other.name) && this.start == other.start && this.end == other.end;
  }

  @Override
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + (this.name != null ? this.name.hashCode() : 0);
    hash = hash * 31 + this.start;
    hash = hash * 31 + this.end;
    return hash;
  }

  @Override
  public int compareTo(Domain other) {
    return this.start - other.start;
  }

  private static HashSet<String> SH3_MATCH;
  /**
   * Initialize/return a set of strings that represent Interpro labels for SH3 domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro SH3
   *         domain entry
   */
  public static HashSet<String> sh3Match() {
    if (Domain.SH3_MATCH == null) {
      Domain.SH3_MATCH = new HashSet<String>();
      Domain.SH3_MATCH.add("SH3_domain"); // IPR001452
      Domain.SH3_MATCH.add("SH3_2"); // IPR011511
      Domain.SH3_MATCH.add("SH3-like_bac-type"); // IPR003646

//      Domain.SH3_MATCH.add("DUF1058"); // IPR010466 - Bacteria only. Not actually relevant in our specieses. A family. Good-ish HMM match.
//
//      Domain.SH3_MATCH.add("Spectrin_alpha_SH3"); // IPR013315 - Gone from Interpro. PRINTS only.
//      Domain.SH3_MATCH.add("SH3b2-type_SH3"); // IPR026864 - Bacteria only. SH3_7 in Pfam, but not part of SH3 clan??? Good-ish HMM similarity.
//
//      Domain.SH3_MATCH.add("CAP-Gly_domain"); // IPR000938 - Many examples. HMM doesn't really resemble SH3, just a LOT of glycines. Literature suggests novelness.
//      Domain.SH3_MATCH.add("DUF3104"); // IPR021453 - Mostly bacteria only. A family HMM bears some resemblance.
//      Domain.SH3_MATCH.add("DUF4453"); // IPR027920 - Bacteria only. HMM doesn't really resemble SH3.
//      Domain.SH3_MATCH.add("hSH3"); // IPR029294 - HMM doesn't really resemble SH3. Pfam summary suggests different behaviour.
//      Domain.SH3_MATCH.add("PhnA_C"); // IPR013988 - Mostly just E. coli. HMM actually resembles SH3.
//      Domain.SH3_MATCH.add("GW_dom"); // IPR025987 - GW domains, "divergent members of the SH3 family.... unlikely to mimic SH3 domains functionally, as their potential peptide-binding sites are destroyed or blocked."
      }
    return Domain.SH3_MATCH;
  }

  private static HashSet<String> BROMO_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for bromo domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro bromo
   *         domain entry
   */
  public static HashSet<String> bromoMatch() {
    if (Domain.BROMO_MATCH == null) {
      Domain.BROMO_MATCH = new HashSet<String>();
      Domain.BROMO_MATCH.add("Bromodomain"); // IPR001487
      Domain.BROMO_MATCH.add("Rsc1/Rsc2_Bromo"); // IPR035700
    }
    return Domain.BROMO_MATCH;
  }

  private static HashSet<String> EH_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for EH domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro EH
   *         domain entry
   */
  public static HashSet<String> ehMatch() {
    if (Domain.EH_MATCH == null) {
      Domain.EH_MATCH = new HashSet<String>();
      Domain.EH_MATCH.add("EH_dom"); // IPR000261
    }
    return Domain.EH_MATCH;
  }

  private static HashSet<String> FHA_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for FHA domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro FHA
   *         domain entry
   */
  public static HashSet<String> fhaMatch() {
    if (Domain.FHA_MATCH == null) {
      Domain.FHA_MATCH = new HashSet<String>();
      Domain.FHA_MATCH.add("FHA_dom"); // IPR000253
      Domain.FHA_MATCH.add("YscD_cytoplasmic_dom"); // IPR032030
    }
    return Domain.FHA_MATCH;
  }

  private static HashSet<String> GYF_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for GYF domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro GYF
   *         domain entry
   */
  public static HashSet<String> gyfMatch() {
    if (Domain.GYF_MATCH == null) {
      Domain.GYF_MATCH = new HashSet<String>();
      Domain.GYF_MATCH.add("GYF"); // IPR003169
    }
    return Domain.GYF_MATCH;
  }

  private static HashSet<String> PDZ_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for PDZ domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro PDZ
   *         domain entry
   */
  public static HashSet<String> pdzMatch() {
    if (Domain.PDZ_MATCH == null) {
      Domain.PDZ_MATCH = new HashSet<String>();
      Domain.PDZ_MATCH.add("PDZ"); // IPR001478
      Domain.PDZ_MATCH.add("PDZ-like_dom"); // IPR025926 - only one in yeast. scant information
      Domain.PDZ_MATCH.add("GRASP55/65_PDZ"); // IPR024958
      Domain.PDZ_MATCH.add("Tricorn_PDZ"); // IPR029414
    }
    return Domain.PDZ_MATCH;
  }

  private static HashSet<String> POLO_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for POLO box domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro POLO box
   *         domain entry
   */
  public static HashSet<String> poloMatch() {
    if (Domain.POLO_MATCH == null) {
      Domain.POLO_MATCH = new HashSet<String>();
      Domain.POLO_MATCH.add("POLO_box_dom"); // IPR000959
      Domain.POLO_MATCH.add("POLO_box_1"); // IPR033701
      Domain.POLO_MATCH.add("POLO_box_Plk4_C"); // IPR033696
      Domain.POLO_MATCH.add("POLO_box_2"); // IPR033695
    }
    return Domain.POLO_MATCH;
  }

  private static HashSet<String> PTB_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for PTB domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro PTB
   *         domain entry
   */
  public static HashSet<String> ptbMatch() {
    if (Domain.PTB_MATCH == null) {
      Domain.PTB_MATCH = new HashSet<String>();
      Domain.PTB_MATCH.add("PTB/PI_dom"); // IPR006020
    }
    return Domain.PTB_MATCH;
  }

  private static HashSet<String> SH2_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for SH2 domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro SH2
   *         domain entry
   */
  public static HashSet<String> sh2Match() {
    if (Domain.SH2_MATCH == null) {
      Domain.SH2_MATCH = new HashSet<String>();
      Domain.SH2_MATCH.add("SH2"); // IPR000980
    }
    return Domain.SH2_MATCH;
  }

  private static HashSet<String> WW_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for WW domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro WW
   *         domain entry
   */
  public static HashSet<String> wwMatch() {
    if (Domain.WW_MATCH == null) {
      Domain.WW_MATCH = new HashSet<String>();
      Domain.WW_MATCH.add("WW_dom"); // IPR001202
    }
    return Domain.WW_MATCH;
  }

  private static HashSet<String> PH_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for PH domains.
   * This list is NOT up-to-date. Thre are a LOT of PH domain variants. Stop using.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro PH
   *         domain entry
   */
  public static HashSet<String> phMatch() {
    if (Domain.PH_MATCH == null) {
      Domain.PH_MATCH = new HashSet<String>();
      Domain.PH_MATCH.add("PH_domain"); // IPR001849
      Domain.PH_MATCH.add("PH_dom-Mcp5-type"); // IPR024774
      Domain.PH_MATCH.add("PH_dom-spectrin-type"); // IPR001605
      Domain.PH_MATCH.add("DBS_PH"); // IPR035534
    }
    return Domain.PH_MATCH;
  }


  private static HashSet<String> PRM_MATCH;

  /**
   * Initialize/return a set of strings that represent Interpro labels for peptide-recognition
   * module domains.
   *
   * @return - a HashSet of strings, each of which are the short description for an Interpro PRM
   *         entry
   */
  public static HashSet<String> prmMatch() {
    if (Domain.PRM_MATCH == null) {
      Domain.PRM_MATCH = new HashSet<String>();
      Domain.PRM_MATCH.addAll(Domain.sh3Match());
      Domain.PRM_MATCH.addAll(Domain.bromoMatch());
      Domain.PRM_MATCH.addAll(Domain.ehMatch());
      Domain.PRM_MATCH.addAll(Domain.fhaMatch());
      Domain.PRM_MATCH.addAll(Domain.gyfMatch());
      Domain.PRM_MATCH.addAll(Domain.pdzMatch());
      Domain.PRM_MATCH.addAll(Domain.poloMatch());
      Domain.PRM_MATCH.addAll(Domain.ptbMatch());
      Domain.PRM_MATCH.addAll(Domain.sh2Match());
      Domain.PRM_MATCH.addAll(Domain.wwMatch());
    }
    return Domain.PRM_MATCH;
  }

  /**
   * Reads in one of the domain files downloaded from Ensembl and returns a data structure
   * containing all the data
   *
   * @param filename
   * @return - a HashMap mapping Ensembl protein IDs to a list of the Domains associated with that
   *         protein.
   * @throws IOException
   */
  public static HashMap<String, ArrayList<Domain>> readDomainsFile(String filename) throws IOException {
    HashMap<String, ArrayList<Domain>> result = new HashMap<String, ArrayList<Domain>>();
  
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String line = "";
  
    while ((line = Static.skipBlankCommentLines(in)) != null) {
      String[] split = line.split("\t");
      String proteinId = split[1];
  
      result.put(proteinId, new ArrayList<Domain>());
      for (int i = 2; i < split.length; i++) {
        String[] split2 = split[i].split(",");
  
        // No protein name. Lost Interpro term probably.
        if (split2[0].trim().length() == 0) {
          continue;
        }
  
        Domain newDomain = new Domain(split2[0], Integer.parseInt(split2[1]), Integer.parseInt(split2[2]), split2[3]);
        result.get(proteinId).add(newDomain);
      }
    }
  
    in.close();
  
    return result;
  }

  /**
   * Get the short names for the Interpro domains of choice.
   * @param domainType - the type of domain being sought. Currently supports sh3, bromo, eh, fha, gyf, pdz, polo, ptb, sh2, ww, ph, prm, binding, domains.
   * @return
   * @throws IOException
   */

  public static HashSet<String> getDomainFilter(String domainType) throws IOException {
    if (domainType.toLowerCase().equals("sh3")) {
      return Domain.sh3Match();
    }
    else if (domainType.toLowerCase().equals("bromo")) {
      return Domain.bromoMatch();
    }
    else if (domainType.toLowerCase().equals("eh")) {
      return Domain.ehMatch();
    }
    else if (domainType.toLowerCase().equals("fha")) {
      return Domain.fhaMatch();
    }
    else if (domainType.toLowerCase().equals("gyf")) {
      return Domain.gyfMatch();
    }
    else if (domainType.toLowerCase().equals("pdz")) {
      return Domain.pdzMatch();
    }
    else if (domainType.toLowerCase().equals("polo")) {
      return Domain.poloMatch();
    }
    else if (domainType.toLowerCase().equals("ptb")) {
      return Domain.ptbMatch();
    }
    else if (domainType.toLowerCase().equals("sh2")) {
      return Domain.sh2Match();
    }
    else if (domainType.toLowerCase().equals("ww")) {
      return Domain.wwMatch();
    }
    else if (domainType.toLowerCase().equals("ph")) {
      return Domain.phMatch();
    }
    else if (domainType.toLowerCase().equals("domains")) {
      return Domains.interproDomainNames();
    }
    else if (domainType.toLowerCase().equals("binding")) {
      return Domains.interproBindingDomainNames();
    }
    else if (domainType.toLowerCase().equals("prm")) {
      return Domain.prmMatch();
    }
    else if (domainType.toLowerCase().equals("all")) {
      return null;
    }
    return null;
  }

  public static String niceifyName(String name) {
    if (Domain.sh3Match().contains(name)) {
      return "SH3";
    }
    else if (Domain.bromoMatch().contains(name)) {
      return "Bromo";
    }
    else if (Domain.ehMatch().contains(name)) {
      return "EH";
    }
    else if (Domain.fhaMatch().contains(name)) {
      return "FHA";
    }
    else if (Domain.gyfMatch().contains(name)) {
      return "GYF";
    }
    else if (Domain.pdzMatch().contains(name)) {
      return "PDZ";
    }
    else if (Domain.poloMatch().contains(name)) {
      return "POLO";
    }
    else if (Domain.sh2Match().contains(name)) {
      return "SH2";
    }
    else if (Domain.wwMatch().contains(name)) {
      return "WW";
    }
    else if (Domain.phMatch().contains(name)) {
      return "PH";
    }
    return name;
  }


}
