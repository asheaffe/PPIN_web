# POINT
Protein Ortholog Interaction Neighbourhood Tool is an HTML/JS tool to visualize pairs of proteins and the orthology relationships between their interactomic neighbours.

## Getting Started

Due to web browser security restrictions, POINT must be deployed over a web server. 

### Prerequisites

POINT can be used in any modern web browser, though it has only been tested extensively in Firefox.

Java 8 is required to execute included POINT source code to generate files for specific proteins not included in the examples.

Due to web browser security restrictions, POINT must be deployed over a web server. If you do not have a web server already set up, you can set one up easily using Python. You can download and install Python, 2 or 3, at http://www.python.org.

### Instructions

Utilizing POINT:

```
1. Open a command prompt.
2. Navigate to the directory where POINT was downloaded.
3. If using Python 3, enter: 
     python3 -m http.server
   If using Python 2, enter:
     python -m SimpleHTTPServer
4. Open a web browser and access POINT at http://localhost:8000/web/test.html
5. Open a POINT JSON file using the available dialog.
```
To generate new POINT JSON files:

```
1. Compile the Java files included in /java
2. Run core.JsonTest with four arguments: 
     species-of-interest-1 protein-of-interest-1 species-of-interest-2 protein-of-interest-2
3. The output JSON file will be placed in web/json/
```

## Data Sources

Included with POINT are the following data files:

1. Network data from [iRefIndex](http://irefindex.org/wiki/index.php?title=iRefIndex) v15.
2. Domain, sequence, orthology, and identifier data from [Ensembl](http://www.ensembl.org) v89.
3. Orthology data from [Homologene](https://www.ncbi.nlm.nih.gov/homologene) and [OrthoMCL](https://orthomcl.org/orthomcl/) downloaded c. May 2017.
4. The [Molecular Interactions Controlled Ontology](https://www.ebi.ac.uk/ols/ontologies/mi) downloaded c. May 2017.
5. SH3 interaction data from the [Bader Lab](http://www.baderlab.org).

Users may wish to update these data sources at their own discretion. There is source code provided to utilize BioGRID network data in Networks.java, which can be toggled in Constants.java.

## Built With

* [Cytoscape.js](http://js.cytoscape.org/)
* [Cytoscape Context Menus](https://github.com/iVis-at-Bilkent/cytoscape.js-context-menus)
* [JSColor](http://jscolor.com) - Modified under the [GNU Public License 3.0](http://www.gnu.org/licenses/gpl-3.0.txt) from version 2.0.5, c. 2018.

## Authors

* **Brian Law** - Primary developer
* **Gary D. Bader** - Supervisor

## License

This project is made available under the [GNU Public License 3.0](http://www.gnu.org/licenses/gpl-3.0.txt).
