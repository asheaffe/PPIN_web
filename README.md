# Protein-Protein Interaction Network Visualizer
A web tool for visualizing Protein-Protein Interaction Networks using cytoscape.js

# How to run:
```
1. Open a command prompt.
2. Navigate to the directory where PPIN_web was downloaded.
3. If using Python 3, enter:
     python3 -m http.server
   If using Python 2, enter:
     python -m SimpleHTTPServer
4. Open a web browser and access at http://localhost:8000/PPIN_web/demo.html
5. Open a POINT JSON file using the available dialog.
     - Only a_demo_update.json is the only json file that will work
     - Any of the other json files are for future testing and will not be formatted correctly
```

# Current Functionalities
```
- Dropdown menu available on right click for each node. Dropdown menu includes separate links to the given protein's entries in the Ensembl, NCBI, or Uniprot databases.
- Opening the Stats tab along the bottom of the webpage will open a moveable window that lists some stats that might be important to the user.
- A legend indicates which colors correspond to a given type of node (species 1, species 2, aligned and orthologous).
- Use the Controls window to open a sidebar that lists all of the protein data.
- Use the Controls window to open a color picker window to change the color scheme of the graph.
```

# Future Functionalities
```
- Copy data within the sidebar with a single button click 
- Have the webpage reflect real alignment data
```

# Current Bugs
```
- Control/Information windows overlap when more than one is opened at a time.
- 
```
