# Protein-Protein Interaction Network Visualizer
A web tool for visualizing Protein-Protein Interaction Networks using cytoscape.js

# How to run:
```
1. Open a command prompt.
2. Navigate to the PPIN_web directory.
3. If using Python 3, enter:
     python3 -m http.server
   If using Python 2, enter:
     python -m SimpleHTTPServer
4. Open a web browser and access at http://localhost:8000/web/demo.html
5. Open a POINT JSON file using the available dialog.
     - Only a_demo_update.json is the only json file that will work
     - Any of the other json files are for future testing and will not be formatted correctly
```

# Current Functionalities
```
- Toggle between orthology view and alignment view using 'Select JSON'
- A legend indicates which colors correspond to a given type of node (Orthology status and Alignment status)
- Orthology/Alignment views reflect data populated from the backend
```

# Future Functionalities
```
- Copy data within the sidebar with a single button click
- Have the webpage reflect real alignment data
```

# Current Bugs
```
- Control/Information windows overlap when more than one is opened at a time
- 'Copy/Paste' tab from the dropdown is not functional
- 'Change Color Scheme' tab will open but does not have any effect on the color scheme
- The items in 'Legend' only change color to correspond with node colors after an instance of that kind of node is loaded
- Node colors are difficult to differentiate between one another--colors need to be more contrasting
- Node dropdown menus are not functional
```
