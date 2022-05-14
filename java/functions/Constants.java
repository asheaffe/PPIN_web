package functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import data.Orthologies;

public class Constants {
  public static String NETWORK_SOURCE = "iref"; //"iref" "biogrid"
  public static String NETWORK_STRUCTURE = "mat"; // "list" "mat";
  public static int FILE_VERSION = 89;

//  public static List<String> REMOVE_THESE = Arrays.asList(new String[]{"UBC", "UBI-4"});

  public static String[] SPECIESES = {"H.sapiens", "M.musculus", "R.norvegicus", "D.rerio", "D.melanogaster", "C.elegans", "S.cerevisiae"};
  // String[] specieses = {"S.cerevisiae"};

  public static String[] SPECIESES2 = {"H.sapiens", "M.musculus", "D.melanogaster", "C.elegans", "S.cerevisiae"};
  // String[] specieses2 = {"H.sapiens","S.cerevisiae"};

  private static ArrayList<String> speciesPairs = null;
  private static ArrayList<String> speciesPairs2 = null;

  private static HashMap<String, String> speciesSciToComm = null;
  private static HashMap<String, String> speciesCommToSci = null;

  public static String[] DOMAIN_TYPES = {"domains", "binding", "sh3", "bromo", "eh", "fha", "gyf", "pdz", "polo", "ptb", "sh2", "ww", "prm"};  // excludes ph
  // String[] domainTypes = {"sh3"};

  public static ArrayList<String> getSpeciesPairs() {
    if (Constants.speciesPairs == null) {
      Constants.speciesPairs = new ArrayList<String>(Constants.SPECIESES.length * (Constants.SPECIESES.length - 1) / 2);;
      for (int i = 0; i < Constants.SPECIESES.length; i++) {
        for (int j = i + 1; j < Constants.SPECIESES.length; j++) {
          Constants.speciesPairs.add(Constants.SPECIESES[Constants.SPECIESES.length - i - 1] + "," + Constants.SPECIESES[Constants.SPECIESES.length - j - 1]);
        }
      }
    }
    return Constants.speciesPairs;
  }

  public static ArrayList<String> getSpeciesPairs2() {
    if (Constants.speciesPairs2 == null) {
      Constants.speciesPairs2 = new ArrayList<String>(Constants.SPECIESES2.length * (Constants.SPECIESES2.length - 1) / 2);;
      for (int i = 0; i < Constants.SPECIESES2.length; i++) {
        for (int j = i + 1; j < Constants.SPECIESES2.length; j++) {
          Constants.speciesPairs2.add(Constants.SPECIESES2[Constants.SPECIESES2.length - i - 1] + "," + Constants.SPECIESES2[Constants.SPECIESES2.length - j - 1]);
        }
      }
    }
    return Constants.speciesPairs2;
  }

  public static HashMap<String, String> getSpeciesSciToComm() {
    if (Constants.speciesSciToComm == null) {
      Constants.speciesSciToComm = new HashMap<String, String>();
      Constants.speciesSciToComm.put("H.sapiens", "human");
      Constants.speciesSciToComm.put("M.musculus", "mouse");
      Constants.speciesSciToComm.put("R.norvegicus", "rat");
      Constants.speciesSciToComm.put("D.rerio", "zebrafish");
      Constants.speciesSciToComm.put("D.melanogaster", "fruitfly");
      Constants.speciesSciToComm.put("C.elegans", "worm");
      Constants.speciesSciToComm.put("S.cerevisiae", "yeast");
    }
    return Constants.speciesSciToComm;
  }

  public static HashMap<String, String> getSpeciesCommToSci() {
    if (Constants.speciesCommToSci == null) {
      Constants.speciesCommToSci = new HashMap<String, String>();
      Constants.speciesCommToSci.put("human", "H.sapiens");
      Constants.speciesCommToSci.put("mouse", "M.musculus");
      Constants.speciesCommToSci.put("rat", "R.norvegicus");
      Constants.speciesCommToSci.put("zebrafish", "D.rerio");
      Constants.speciesCommToSci.put("fruitfly", "D.melanogaster");
      Constants.speciesCommToSci.put("worm", "C.elegans");
      Constants.speciesCommToSci.put("yeast", "S.cerevisiae");
    }
    return Constants.speciesCommToSci;
  }

}
