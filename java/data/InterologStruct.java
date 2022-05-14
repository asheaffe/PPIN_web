package data;

import java.util.HashSet;

public class InterologStruct {

    public HashSet<PairOfStrings> interologs = new HashSet<PairOfStrings>();
    public HashSet<String> matched1 = new HashSet<String>();
    public HashSet<String> matched2 = new HashSet<String>();
    public HashSet<String> unmatched1 = new HashSet<String>();
    public HashSet<String> unmatched2 = new HashSet<String>();
    public HashSet<String> unmatchable1 = new HashSet<String>();
    public HashSet<String> unmatchable2 = new HashSet<String>();
}
