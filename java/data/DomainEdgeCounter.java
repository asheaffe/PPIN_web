package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import data.Fasta;
import functions.Static;
import processing.Networks;

public class DomainEdgeCounter {
  public static HashMap<String, HashMap<String, HashMap<String, HashMap<String, DomainEdgeCounter>>>> domainEdgeCounters = new HashMap<String, HashMap<String, HashMap<String, HashMap<String, DomainEdgeCounter>>>>();

  public String name;
  public String id;
  public ArrayList<Domain> domains;
  public HashSet<String> neighbours;

  public DomainEdgeCounter(String name, String id, Collection<Domain> domains, HashSet<String> neighbours) {
    this.name = name;
    this.id = id;
    this.domains = new ArrayList<Domain>(domains);
    this.neighbours = neighbours;
  }

  public static HashMap<String, DomainEdgeCounter> getDomainEdgeCounters(String species, Fasta fasta, HashMap<String, ArrayList<Domain>> protein2domains, String domainType, String domainDb) throws Exception {
    if (!DomainEdgeCounter.domainEdgeCounters.containsKey(species)) {
      DomainEdgeCounter.domainEdgeCounters.put(species, new HashMap<String, HashMap<String, HashMap<String, DomainEdgeCounter>>>());
    }
    if (!DomainEdgeCounter.domainEdgeCounters.get(species).containsKey(domainType)) {
      DomainEdgeCounter.domainEdgeCounters.get(species).put(domainType, new HashMap<String, HashMap<String, DomainEdgeCounter>>());
    }
    if (!DomainEdgeCounter.domainEdgeCounters.get(species).get(domainType).containsKey(domainDb)) {
      DomainEdgeCounter.domainEdgeCounters.get(species).get(domainType).put(domainDb, DomainEdgeCounter.generateDomainEdgeCounters(species, fasta, protein2domains, domainType, domainDb));
    }

    return DomainEdgeCounter.domainEdgeCounters.get(species).get(domainType).get(domainDb);
  }

  public static DomainEdgeCounter getDomainEdgeCounter(String species, Fasta fasta, HashMap<String, ArrayList<Domain>> protein2domains, String domainType, String domainDb, String proteinName) throws Exception {
    HashMap<String, DomainEdgeCounter> decs = DomainEdgeCounter.getDomainEdgeCounters(species, fasta, protein2domains, domainType, domainDb);
    if (decs != null) {
      return decs.get(proteinName);
    }
    else {
      return null;
    }
  }

  /**
   * Counts the number of domains, of a specified type, and interactions each protein in a network has.
   *
   * @param species
   * @param domainType - the type of domain to be counted; currently supported: sh3, bromo, eh, fha, gyf, pdz, polo, ptb, sh2, ww, ph, prm, binding, domains.
   * @param domainDb - the domain database to use for domain counts; pfam should be default
   * @return - a HashMap mapping protein names to a DomainEdgecounter for each protein
   * @throws Exception
   */
  private static HashMap<String, DomainEdgeCounter> generateDomainEdgeCounters(String species, Fasta fasta, HashMap<String, ArrayList<Domain>> protein2domains, String domainType, String domainDb) throws Exception {
    Network network = Networks.getNetwork(species);

    // Used to limit analysis to just the longest isoform of each protein
    HashMap<String, DomainEdgeCounter> result = new HashMap<String, DomainEdgeCounter>();

    // Set up filtering for certain domain types
    HashSet<String> domainFilter = Domain.getDomainFilter(domainType);

    for (String proteinName: fasta.sequenceNameOrder) {
      String name = proteinName;

      HashSet<String> neighbours = Static.namesFromNodes(network.getAdjacent(name));

      String proteinId = Static.getOneFromSet(fasta.names2ids.get(name));
      ArrayList<Domain> tempDomains = protein2domains.get(proteinId);
      ArrayList<Domain> domains = new ArrayList<Domain>();

      if (tempDomains != null) {
        for (Domain domain: tempDomains) {
          if ((domainFilter == null || domainFilter.contains(domain.name)) && (domainDb.equals("all") || domain.source.contains(domainDb))) {
            domains.add(domain);
          }
        }
      }

      result.put(name, new DomainEdgeCounter(name, proteinId, domains, neighbours));
    }

    return result;

  }
}
