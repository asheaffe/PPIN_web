package functions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import data.Node;

public class Static {

	public final static NumberFormat twoDecimals = new DecimalFormat("0.00");
	public final static NumberFormat fourDecimals = new DecimalFormat("0.0000");
	public final static NumberFormat twoDigits = new DecimalFormat("00");
	public final static NumberFormat fourDigits = new DecimalFormat("0000");
	public static boolean debug = false;

	public final static  String[] ensemblSpecies2 = {
		"orycteropus afer afer", // Aardvark
		"vicugna pacos", // Alpaca
		"poecilia formosa", // Amazon molly
		"anolis carolinensis", // Anole lizard
		"dasypus novemcinctus", // Armadillo
		"melopsittacus undulatus", // Budgerigar
		"otolemur garnettii", // Bushbaby
		"ciona intestinalis", // C.intestinalis
		"ciona savignyi", // C.savignyi
		"caenorhabditis elegans", // C. elegans
		"felis catus", // Cat
		"astyanax mexicanus", // Cave fish
		"gallus gallus", // Chicken
		"pan troglodytes", // Chimpanzee
		"cricetulus griseus", // Chinese hamster
		"pelodiscus sinensis", // Chinese softshell turtle
		"gadus morhua", // Cod
		"latimeria chalumnae", // Coelacanth
		"bos taurus", // Cow
		"macaca fascicularis", // Crab eating-macaque
		"canis lupus familiaris", // Dog
		"tursiops truncatus", // Dolphin
		"anas platyrhynchos", // Duck
		"loxodonta africana", // Elephant
		"mustela putorius furo", // Ferret
		"ficedula albicollis", // Flycatcher
		"drosophila melanogaster", // Fruitfly
		"takifugu rubripes", // Fugu
		"nomascus leucogenys", // Gibbon
		"gorilla gorilla gorilla", // Gorilla
		"cavia porcellus", // Guinea Pig
		"papio hamadryas", // Hamadryas Baboon
		"erinaceus europaeus", // Hedgehog
		"equus caballus", // Horse
		"homo sapiens", // Human
		"procavia capensis", // Hyrax
		"dipodomys ordii", // Kangaroo rat
		"petromyzon marinus", // Lamprey
		"echinops telfairi", // Lesser hedgehog tenrec
		"macaca mulatta", // Macaque
		"callithrix jacchus", // Marmoset
		"oryzias latipes", // Medaka
		"pteropus vampyrus", // Megabat
		"myotis lucifugus", // Microbat
		"mus musculus", // Mouse
		"microcebus murinus", // Mouse Lemur
		"heterocephalus glaber", // Naked mole-rat
		"papio anubis", // Olive baboon
		"monodelphis domestica", // Opossum
		"pongo abelii", // Orangutan
		"chrysemys picta bellii", // Painted Turtle
		"ailuropoda melanoleuca", // Panda
		"sus scrofa", // Pig
		"ochotona princeps", // Pika
		"xiphophorus maculatus", // Platyfish
		"ornithorhynchus anatinus", // Platypus
		"microtus ochrogaster", // Prairie vole
		"oryctolagus cuniculus", // Rabbit
		"rattus norvegicus", // Rat
		"ceratotherium simum simum", // Rhinoceros
		"saccharomyces cerevisiae", // S. cerevisiae
		"ovis aries", // Sheep
		"sorex araneus", // Shrew
		"choloepus hoffmanni", // Sloth
		"physter macrocephalus", // Sperm whale
		"lepisosteus oculatus", // Spotted gar
		"ictidomys tridecemlineatus", // Squirrel
		"saimiri boliviensis", // Squirrel monkey
		"gasterosteus aculeatus", // Stickleback
		"tarsius syrichta", // Tarsier
		"sarcophilus harrisii", // Tasmanian devil
		"tetraodon nigroviridis", // Tetraodon
		"oreochromis niloticus", // Tilapia
		"tupaia belangeri", // Tree Shrew
		"meleagris gallopavo", // Turkey
		"chlorocebus sabaeus", // Vervet-AGMV
		"macropus eugenii", // Wallaby
		"xenopus tropicalis", // Xenopus
		"taeniopygia guttata", // Zebra Finch
		"danio rerio" // Zebrafish
		};

	public final static HashSet<String> ensemblSpecies = new HashSet<String>(Arrays.asList(ensemblSpecies2));

	/*
	 * BASIC DATA STRUCTURE AND STRING MANIPULATION FUNCTIONS
	 */

	/**
	 *
	 * @param text
	 */
	public static void debugOutput(String text) {
		if (Static.debug) {
			System.out.println(text);
		}
	}

	/**
	 * Converts an adjacency list into an XGMML file, primarily for Cytoscape import
	 * @param - the adjacency file to be imported. The xgmml file will have the same filename with the .xgmml extension
	 * @throws Exception
	 */
	public static void adjListToXGMML(String inputFile) throws Exception {
		String outputName = inputFile.substring(Math.max(0,inputFile.lastIndexOf('/')+1), inputFile.lastIndexOf('.'));

		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(inputFile.substring(0, inputFile.lastIndexOf('.')) + ".xgmml"));
		String line = "";
		StringBuffer output = new StringBuffer();
		StringBuffer output2 = new StringBuffer();

		HashMap<String, Integer> nodeMap = new HashMap<String, Integer>();
		int counter = (int) (Integer.MIN_VALUE + Math.random() * ((double)Integer.MAX_VALUE - (double)Integer.MIN_VALUE - 1000000));

		output.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		output.append("<graph label=\"" + outputName + "\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\"  directed=\"0/\">\n");

		while ((line = Static.skipCommentLines(in)) != null) {
			String[] split = line.split("[,\t]+");
			String node1 = split[0] + " (" + split[2] + "/" + split[3] + ")";
			String node2 = split[4] + " (" + split[6] + "/" + split[7] + ")";

			if (!nodeMap.containsKey(node1)) {
				nodeMap.put(node1, counter);
				counter++;
			}
			output.append("\t<node id=\"" + node1 + "\" label=\"" + node1 + "\">\n" +
					"\t\t<att name=\"label\" value=\"" + node1 + "\" />\n" +
					"\t</node>\n");

			if (!nodeMap.containsKey(node2)) {
				nodeMap.put(node2, counter);
				counter++;
			}
			output.append("\t<node id=\"" + node2 + "\" label=\"" + node2 + "\">\n" +
					"\t\t<att name=\"label\" value=\"" + node2 + "\" />\n" +
					"\t</node>\n");

			output2.append("\t<edge source=\"" + node1 + "\" target=\"" + node2 + "\" label=\"" + nodeMap.get(node1) + nodeMap.get(node2) + "\">\n" +
					"\t\t<att name=\"label\" value=\"" + node1 + " - " + node2 + "\" />\n" +
					"\t</edge>\n");
		}

		output2.append("</graph>");

		out.write(output.toString() + output2.toString());

		in.close();
		out.close();
	}

	/**
	 * Takes two mappings, each containing strings mapped to a single numerical
	 * value. Crosses the two mappings to create a scoring matrix between
	 * strings from different mappings, by taking the difference between their
	 * respective numerical values. - Currently uses linear scaling.
	 *
	 * @param order1
	 *            - an ordering of the strings from the first mapping
	 * @param order2
	 *            - an ordering of the strings from the second mapping
	 * @param scores1
	 *            - the first mapping of strings to numerical values
	 * @param scores2
	 *            - the second mapping of strings to numerical values
	 * @param filename
	 *            - a filename to which to output the scoring matrix
	 * @throws Exception
	 */
	public static void createScoringMatrixFromLinearData(List<String> order1, List<String> order2, HashMap<String, Double> scores1, HashMap<String, Double> scores2, String filename) throws Exception {
		//System.out.println(order1);
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		StringBuffer output = new StringBuffer();

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		Iterator<String> nodes1 = scores1.keySet().iterator();
		while (nodes1.hasNext()) {
			String node1 = nodes1.next();
			Iterator<String> nodes2 = scores2.keySet().iterator();
			while (nodes2.hasNext()) {
				String node2 = nodes2.next();
				if (!order1.contains(node1) || !order2.contains(node2)) {
					continue;
				}
				min = Math.min(min, Math.abs(scores1.get(node1)	- scores2.get(node2)));
				max = Math.max(max, Math.abs(scores1.get(node1)	- scores2.get(node2)));
			}
		}

		for (int i = 0; i < order1.size(); i++) {
			output.append("\t" + order1.get(i));
		}
		output.append("\n");

		//System.out.println(order1);

		for (int i = 0; i < order2.size(); i++) {
			output.append(order2.get(i));
			String node2 = order2.get(i);
			for (int j = 0; j < order1.size(); j++) {
				String node1 = order1.get(j);
				//System.out.println("node1: " + node1 + "\t" + scores1.get(node1));
				//System.out.println("node2: " + node2 + "\t" + scores2.get(node2));
				//System.out.println(scores1.get(node1) + "|" + scores2.get(node2));
				//System.out.println("\t" + ((Math.abs(scores1.get(node1) - scores2.get(node2)- min) / (max - min))));

				System.out.println(node1 + " | " + node2);
				output.append("\t"	+ fourDecimals.format(1 - (Math.abs(scores1.get(node1) - scores2.get(node2)) - min) / (max - min)));
			}
			output.append("\n");
		}

		//System.out.println(filename + " written!");

		out.write(output.toString());
		out.close();
	}

	/**
	 * Takes two mappings, each containing strings mapped to a single numerical
	 * value. Crosses the two mappings to create a scoring matrix between
	 * strings from different mappings, by taking the difference between their
	 * respective numerical values. - Currently uses logarithmic scaling.
	 *
	 * @param order1
	 *            - an ordering of the strings from the first mapping
	 * @param order2
	 *            - an ordering of the strings from the second mapping
	 * @param scores1
	 *            - the first mapping of strings to numerical values
	 * @param scores2
	 *            - the second mapping of strings to numerical values
	 * @param filename
	 *            - a filename to which to output the scoring matrix
	 * @throws Exception
	 */
	public static void createScoringMatrixFromLogData(List<String> order1, List<String> order2, HashMap<String, Double> scores1, HashMap<String, Double> scores2, String filename) throws Exception {
		//System.out.println(order1);
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		StringBuffer output = new StringBuffer();

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		Iterator<String> nodes1 = scores1.keySet().iterator();
		while (nodes1.hasNext()) {
			String node1 = nodes1.next();
			Iterator<String> nodes2 = scores2.keySet().iterator();
			while (nodes2.hasNext()) {
				String node2 = nodes2.next();
				if (!order1.contains(node1) || !order2.contains(node2)) {
					continue;
				}
				min = Math.min(min, Math.log(Math.abs(scores1.get(node1) - scores2.get(node2))+1));
				max = Math.max(max, Math.log(Math.abs(scores1.get(node1) - scores2.get(node2))+1));
			}
		}

		for (int i = 0; i < order1.size(); i++) {
			output.append("\t" + order1.get(i));
		}
		output.append("\n");

		//System.out.println(order1);



		for (int i = 0; i < order2.size(); i++) {
			output.append(order2.get(i));
			String node2 = order2.get(i);
			for (int j = 0; j < order1.size(); j++) {
				String node1 = order1.get(j);
				//System.out.println("node1: " + node1 + "\t" + scores1.get(node1));
				//System.out.println("node2: " + node2 + "\t" + scores2.get(node2));
				//System.out.println(scores1.get(node1) + "|" + scores2.get(node2));
				//System.out.println("\t" + ((Math.abs(scores1.get(node1) - scores2.get(node2)- min) / (max - min))));
				output.append("\t"	+ fourDecimals.format(Math.log(Math.abs(scores1.get(node1) - scores2.get(node2))+1) / (max - min)));
			}
			output.append("\n");
		}

		//System.out.println(filename + " written!");

		out.write(output.toString());
		out.close();
	}

	/**
	 * Populates two sets of nodes based on the row and column headers of a
	 * scoring matrix file
	 *
	 * @param hashSet
	 *            - empty list of nodes (domain or binding site) for species 1
	 * @param hashSet2
	 *            - empty list of nodes (domain or binding site) for species 1
	 * @param fileName
	 *            - scoring matrix file to harvest node names from
	 * @exception - inherited from file reading
	 */
	public static void populateNodeLists(HashSet<Node> hashSet,
			HashSet<Node> hashSet2, String fileName) throws Exception {
		if (hashSet.isEmpty()) {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line = "";
			String[] values = in.readLine().split("\t");
			for (int i = 1; i < values.length; i++) {
				hashSet.add(new Node(values[i], "1"));
			}
			while ((line = in.readLine()) != null) {
				hashSet2.add(new Node(line.split("\t")[0], "2"));
			}
			in.close();
		}
	}

	/**
	 * Takes in an adjacency list file and shuffles it randomly, 1000 times,
	 * ensuring no duplicates
	 *
	 * @param filename
	 *            - name of the input file
	 * @throws Exception
	 */
	public static void shuffleAdjList(String filename) throws Exception {
		ArrayList<String> col1 = new ArrayList<String>();
		ArrayList<String> col2 = new ArrayList<String>();
		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();

		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = "";

		NumberFormat formatter = new DecimalFormat("0000");

		while ((line = Static.skipCommentLines(in)) != null) {
			String[] split = line.split("\t");
			col1.add(split[0]);
			col2.add(split[1]);
			if (!map.containsKey(split[0])) {
				map.put(split[0], new HashSet<String>());
			}
			map.get(split[0]).add(split[1]);
		}
		in.close();

		for (int sims = 1; sims <= 1000; sims++) {
			for (int i = 0; i < col2.size(); i++) {
				// Try 1000 times; give up after
				for (int j = 0; j < 1000; j++) {
					// Generate a random index from the latter part of the array
					int index = (int) Math.ceil(Math.random()
							* (col2.size() - i - 1))
							+ i;
					// If swapping the current element and the element at index
					// would not duplicate edges, swap them
					if (!map.get(col1.get(index)).contains(col2.get(i))
							&& !map.get(col1.get(i)).contains(col2.get(index))) {
						map.get(col1.get(i)).remove(col2.get(i));
						map.get(col1.get(i)).add(col2.get(index));
						map.get(col1.get(index)).remove(col2.get(index));
						map.get(col1.get(index)).add(col2.get(i));
						String temp = col2.get(i);
						col2.set(i, col2.get(index));
						col2.set(index, temp);
						j = 1001;
					}
				}
			}

			BufferedWriter out = new BufferedWriter(new FileWriter("random/"
					+ filename.substring(0, filename.lastIndexOf('.'))
					+ "_randomized_" + formatter.format(sims) + ".txt"));
			StringBuffer output = new StringBuffer();

			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Iterator<String> it2 = map.get(key).iterator();
				while (it2.hasNext()) {
					String match = it2.next();
					output.append(key + "\t" + match + "\n");
				}
			}

			out.write(output.toString());
			out.close();
		}
	}

    /**
     * Gets the next non-blank line from input.
     * @param in - the BufferedReader with the file input
     * @return - the first non-comment line to be read
     * @throws IOException - inherited from BufferedReader
     */
    public static String skipBlankLines(BufferedReader in) throws IOException {
        String line = in.readLine();
        while (line != null && line.length() == 0) {
            line = in.readLine();
        }
        return line;
    }


	/**
	 * Gets the next non-comment line from input, assuming ! is the comment symbol.
	 * @param in - the BufferedReader with the file input
	 * @return - the first non-comment line to be read
	 * @throws IOException - inherited from BufferedReader
	 */
	public static String skipCommentLines(BufferedReader in) throws IOException {

		String line = in.readLine();
		while (line != null && line.length() > 0 && line.charAt(0) == '!') {
			line = in.readLine();
		}
		return line;
	}

	/**
	 * Gets the next non-blank, non-comment line from input, where ! is the comment symbol.
	 * A line with just white-space is not considered blank.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String skipBlankCommentLines(BufferedReader in) throws IOException {
		String line = Static.skipCommentLines(in);
		while (line != null && line.length() == 0) {
			line = Static.skipCommentLines(in);
		}
		return line;
	}

	/**
	 * Writes a StringBuffer's contents out to a specified filename
	 * @param filename
	 * @param output
	 * @throws IOException
	 */
	public static void writeOutputToFile(String filename, StringBuffer output) throws IOException {
	  BufferedWriter out = new BufferedWriter(new FileWriter(filename));
	  out.write(output.toString());
	  out.close();
	}

	/**
	 * Turns a Set of items into an ordered ArrayList of those items.
	 * @param set - a Set of comparable items
	 * @return - a sorted ArrayList of those items
	 */
	public static <T extends Comparable<T>> ArrayList<T> setToOrderedList(Set<T> set) {
      ArrayList<T> result = new ArrayList<T>(set);
      Collections.sort(result);
	  return result;
	}

	/**
	 * Returns a mapping of uniprot IDs to gene names, via uniprot2ensp, ensp2ensg, and then the SH3 interaction files.
	 * @param networkName
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, Set<String>> uniprot2genes(String interactionFile, String uniprot2enspFile, String domainFile) throws IOException {

		BufferedReader in = null;
		String line = "";

		// Create a mapping from uniprot to ensemblp ids
		HashMap<String, HashSet<String>> uniprot2ensembl = new HashMap<String, HashSet<String>>();
		HashMap<String, Set<String>> uniprot2names = new HashMap<String, Set<String>>();

		if (uniprot2enspFile == null) {
			return uniprot2names;
		}

		in = new BufferedReader(new FileReader(uniprot2enspFile));
		in.readLine(); // Skip first line
		while ((line = in.readLine()) != null) {
			String[] split = line.split("\t");
			if (!uniprot2ensembl.containsKey(split[0])) {
				uniprot2ensembl.put(split[0], new HashSet<String>());
			}
			uniprot2ensembl.get(split[0]).add(split[1]);
		}
		in.close();

		// Create a mapping of domain ENSGs to ENSPs
		in = new BufferedReader(new FileReader(domainFile));
		in.readLine(); // Skip first line

		HashMap<String, HashSet<String>> gsToPs = new HashMap<String, HashSet<String>>();

		while ((line = in.readLine()) != null) {
			String[] split = line.replace("*", "").split("\t");

			if (!gsToPs.containsKey(split[2])) {
				gsToPs.put(split[2], new HashSet<String>());
			}
			gsToPs.get(split[2]).add(split[3].replace("*",""));
		}

		in.close();

		// Create a mapping from ensemblp ids to gene names
		HashMap<String, String> ensembl2names = new HashMap<String, String>();
		in = new BufferedReader(new FileReader(interactionFile));
		while ((line = in.readLine()) != null) {
			String[] split = line.split("[,\t]");
			ensembl2names.put(split[1], split[0]);
			ensembl2names.put(split[5], split[4]);
		}
		in.close();

		for (String uniprot: uniprot2ensembl.keySet()) {

			for (String ensembl: uniprot2ensembl.get(uniprot)) {
				if (ensembl2names.containsKey(ensembl)) {
					if (!uniprot2names.containsKey(uniprot)) {
						uniprot2names.put(uniprot, new HashSet<String>());
					}
					uniprot2names.get(uniprot).add(ensembl2names.get(ensembl));
				}
			}
		}

		return uniprot2names;
	}

	/**
	 * Reads in a FASTA file and outputs a hashmap, mapping sequence names to sequences. Sequences are named
	 * by the first token in the header line.
	 * @param fastaFile
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> parseFastaFile(String fastaFile) throws Exception {
		return Static.parseFastaFile(fastaFile, "[ \t]+", 0);
	}

	/**
	 * Reads in a FASTA file and outputs a hashmap, mapping sequence names to sequences. Use regex and col to specify
	 * how sequences will be named - the header string will be split by regex, and the col used.
	 * @param fastaFile - the FASTA file to process
	 * @param regex - the regex used to split the header line
	 * @param col - the column (0-index) of the split header line to use as the sequence name
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> parseFastaFile(String fastaFile, String regex, int col) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(fastaFile));
		String line = "";
		HashMap<String, String> result = new HashMap<String, String>();

		String proteinName = null;
		String proteinSequence = "";
		while ((line = in.readLine()) != null) {
			if (line.startsWith(">")) {
				if (proteinName != null) {
					if (result.containsKey(proteinName)) {
						in.close();
						System.out.println(fastaFile + "\t" + proteinName + "\t" + proteinSequence);
						throw new Exception("HashMap already contains sequence named " + proteinName);
					}
					result.put(proteinName, proteinSequence);
				}
				// Reset for next protein
				proteinName = line.split(regex)[col];
				if (proteinName.startsWith(">")) {
					proteinName = proteinName.substring(1);
				}
				proteinSequence = "";
			}
			else {
				proteinSequence += line;
			}
		}

		// Final trigger at end of file
		if (result.containsKey(proteinName)) {
			in.close();
			throw new Exception("HashMap already contains sequence named " + proteinName);
		}
		result.put(proteinName, proteinSequence);

		in.close();

		return result;
	}

	/**
	 * Converts a single long sequence string into a chopped FASTA sequence (newline every 80 characters)
	 * @param sequence
	 * @return
	 */
	public static String sequenceToFasta(String sequence) {
		StringBuffer result = new StringBuffer();
		for (int i=0; i<sequence.length()/80; i++) {
			result.append(sequence.substring(i * 80, (i+1)* 80));
			result.append("\n");
		}
		result.append(sequence.substring(80 * (sequence.length()/80)));
		return result.toString();
	}

	public static String truncateString(String inputString) {
		if (inputString.length() > 32000) {
			inputString = "Truncated: " + inputString.substring(0, 32000);
		}
		return inputString;
	}

	/**
	 * Converts an adj list into a Cytoscape-importable format
	 * @throws Exception
	 */
	private static void cytoscapeFormat() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("yeast/Yeast_Adjacency_List.Merged_Final.txt"));
		StringBuffer output = new StringBuffer();
		String line = "";

		while ((line = in.readLine()) != null) {
			String[] split = line.split("[,\t]+");
			output.append(split[0] + " (" + split[2] + "/" + split[3] + ")\t" + split[4] + " (" + split[6] + "/" + split[7] + ")\n");
		}

		in.close();

		BufferedWriter out = new BufferedWriter(new FileWriter("yeast/Yeast_Adjacency_List.Merged_Final.cytoscape.txt"));
		out.write(output.toString());
		out.close();
	}

	/**
	 * Takes a set of Nodes and retrieves all their protein names
	 * @param nodes - a Set of Node objects
	 * @return - a HashSet of all the protein names corresponding to the input nodes
	 */
	public static HashSet<String> namesFromNodes(Set<Node> nodes) {
		HashSet<String> result = new HashSet<String>(nodes.size());

		for (Node node: nodes) {
			result.add(node.name);
		}

		return result;
	}

	/**
	 * Return the unabbreviated version of a species name
	 * @param species
	 * @return
	 */
	public static String speciesLongName(String species) {
		if (species.equals("H.sapiens")) {
			return "Homo sapiens";
		}
		else if (species.equals("A.thaliana")) {
			return "Arabidopsis thaliana";
		}
		else if (species.equals("C.elegans")) {
			return "Caenorhabditis elegans";
		}
		else if (species.equals("D.melanogaster")) {
			return "Drosophila melanogaster";
		}

		else if (species.equals("D.rerio")) {
			return "Danio rerio";
		}
		else if (species.equals("M.musculus")) {
			return "Mus musculus";
		}
		else if (species.equals("R.norvegicus")) {
			return "Rattus norvegicus";
		}
		else if (species.equals("S.cerevisiae")) {
			return "Saccharomyces cerevisiae";
		}
		else {
			return null;
		}
	}


	/*
	 * BASIC DATA STRUCTURE AND STRING MANIPULATION FUNCTIONS
	 */

	/**
	 *
	 * @param oldCopy
	 * @return
	 */
	public static double[][] deepCopy(double[][] oldCopy) {
		double[][] newCopy = new double[oldCopy.length][oldCopy[0].length];
		for (int i=0; i<oldCopy.length; i++) {
			for (int j=0; j<oldCopy[i].length; j++) {
				newCopy[i][j] = oldCopy[i][j];
			}
		}
		return newCopy;
	}


	/**
	 * Gets the intersection of two Sets as a separate Set
	 * @param set1
	 * @param set2
	 * @return
	 */
	public static <T> HashSet<T> intersectionOfTwoSets(Set<T> set1, Set<T> set2) {
		HashSet<T> union = new HashSet<T>(set1);
		union.retainAll(set2);
		return union;
	}

	/**
	 * Gets all the strings from a set of strings, and returns it as a single string, separated by a user-specified
	 * delineator.
	 * @param strings - the set of strings
	 * @param separator - the string used to delineate the retrieved strings
	 * @return
	 */
	public static String mergeStringsFromSet(Set<String> strings, String separator) {
		String result = "";
		for (String next: strings) {
			result += next + separator;
		}
		result = result.substring(0, result.length() - separator.length());
		return result;
	}

	/**
	 * Gets the first string from a set of strings, by alphabetical order.
	 * @param <T>
	 * @param strings - the set of strings
	 * @return - the first element from the set, as if the set was sorted
	 */
	public static <T extends Comparable<T>> T getFirstFromSet(Set<T> strings) {
		T result = null;
		for (T next: strings) {
			if (result == null || next.compareTo(result) < 0) {
				result = next;
			}
		}
		return result;
	}

	/**
	 * Gets an arbitrary element from a Set. Should only be used for one-item Sets.
	 * @param set - the Set
	 * @return - an element from the set; null if set is empty
	 */
	public static <T> T getOneFromSet(Set<T> set) {
		for (T next: set) {
			return next;
		}
		return null;
	}


	/**
	 * A helper function to facilitate construction of multimaps.
	 * @param map - A 2-way multimap.
	 * @param item1 - One of a pair of items to be mapped to be each other.
	 * @param item2 - One of a pair of items to be mapped to each other.
	 */
	public static void addTo2WayMultiMap(HashMap<String, HashSet<String>> map, String item1, String item2) {
		if (!map.containsKey(item1)) {
			map.put(item1, new HashSet<String>());
		}
		if (!map.containsKey(item2)) {
			map.put(item2, new HashSet<String>());
		}
		map.get(item1).add(item2);
		map.get(item2).add(item1);
	}

	public static <K, V> HashMap<V, HashSet<K>> reverseHashMap(HashMap<K, V> originalMap) {
	  HashMap<V, HashSet<K>> resultMap = new HashMap<V, HashSet<K>>();
	  for (K key: originalMap.keySet()) {
	    resultMap.putIfAbsent(originalMap.get(key), new HashSet<K>());
	    resultMap.get(originalMap.get(key)).add(key);
	  }
	  return resultMap;
	}

	/**
	 * Converts a set of strings to uppercase characters
	 * @param incomingStrings - set of input strings
	 * @return - a new set of the same strings, except now all upper-case
	 */
	public static HashSet<String> stringsToUpperCase(HashSet<String> incomingStrings) {
		if (incomingStrings == null) {
			return null;
		}
		HashSet<String> outgoingStrings = new HashSet<String>();

		for (String string: incomingStrings) {
			outgoingStrings.add(string.toUpperCase());
		}
		return outgoingStrings;
	}

	/**
	 * Converts a set of strings to lowercase characters
	 * @param incomingStrings - set of input strings
	 * @return - a new set of the same strings, except now all lower-case
	 */
	public static HashSet<String> stringsToLowerCase(HashSet<String> incomingStrings) {
		if (incomingStrings == null) {
			return null;
		}
		HashSet<String> outgoingStrings = new HashSet<String>();

		for (String string: incomingStrings) {
			outgoingStrings.add(string.toLowerCase());
		}
		return outgoingStrings;
	}


	public static void main(String[] args) throws Exception {
	  System.out.println("HELLO");
		//Static.cytoscapeFormat();


		//Static.shuffleAdjList("yeast/Yeast_Adjacency_List.Merged_Final.txt");

		// Converts adj matrix file into XGMML, primarily for Cytoscape Network Analyzer Batch Analysis
		//for (int i=1; i<=1; i++) {
		//	Static.adjListToXGMML("random/worm/Worm_Adjacency_List.Merged_Final_randomized_" + Static.fourDigits.format(i) + ".txt");
		//}

		//Static.clustalToFasta("fuckme.txt", "fuckme.fasta");

	}

    /**
     * Provides the NCBI taxonomy number for a specified species.
     * @param species - species name
     * @return - NCBI taxonomy ID for provided species argument
     * @throws Exception
     */
    public static int getTaxonId(String species) throws Exception {
        if (species.equalsIgnoreCase("h.sapiens") || species.equalsIgnoreCase("homo sapiens")) {
            return 9606;
        }
        else if (species.equalsIgnoreCase("d.rerio") || species.equalsIgnoreCase("danio rerio")) {
            return 7955;
        }
        else if (species.equalsIgnoreCase("d.melanogaster") || species.equalsIgnoreCase("drosophila melanogaster")) {
            return 7227;
        }
        else if (species.equalsIgnoreCase("s.cerevisiae") || species.equalsIgnoreCase("saccharomyces cerevisiae")) {
            return 4932;
        }
        else if (species.equalsIgnoreCase("c.elegans") || species.equalsIgnoreCase("caenorhabditis elegans")) {
            return 6239;
        }
        else if (species.equalsIgnoreCase("a.thaliana") || species.equalsIgnoreCase("arabidopsis thaliana")) {
            return 3702;
        }
        else if (species.equalsIgnoreCase("m.musculus") || species.equalsIgnoreCase("mus musculus")) {
            return 10090;
        }
        else if (species.equalsIgnoreCase("r.norvegicus") || species.equalsIgnoreCase("rattus norvegicus")) {
            return 10116;
        }
        else {
            throw new Exception("Invalid species name provided.");
        }
    }
}
