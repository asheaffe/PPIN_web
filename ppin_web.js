/**
  * @file
  * Uses cystoscape.js to draw a graph based on data provided in a given json file
  *
  */

document.getElementById('myFile').addEventListener('change', loadFile);


function loadFile(event) {
  /**
    * Loads the chosen json file and runs cytoscape based on the formatting within in
    *
    * @param event signifies a button click
    */
  f = event.target.files[0];

  fr = new FileReader();
  fr.addEventListener("load", e => {
    runCytoscape(fr.result);
  });

  fr.readAsText(f);
}

function runCytoscape(data) {
  /**
    * Runs cytoscape.js given the data passed as input
    *
    * @param data signifies the data from the json file that is used for cytoscape formatting
    */
  var cy = window.cy = cytoscape({
    container: document.getElementById('cy'),

    boxSelectionEnabled: false,
    autounselectify: true,

    // styling for cytoscape.js graph
    style: cytoscape.stylesheet()
      .selector('node')
        .css({
          'label': 'data(name)',
          'border-color': 'black',
          'border-width': 2,
          'height': 80,
          'width': 80
        })
      .selector('edge')
        .css({
          'curve-style': 'haystack',
          'width': 6
        })
      .selector(":parent")
        .css({
          'text-valign': 'top',
          'text-halign': 'center'
        })
      .selector("node.container")
        .css({
          'border-width': 2,
          'border-color': 'black'
        })
      .selector('node.species1')
        .css({
          "background-color": 'blue'
        })
      .selector('node.species2')
        .css({
          "background-color": "red"
        })
      .selector('node.aligned')
        .css({
          'background-color': 'green'
        })
      .selector("node.query")
        .css({
          'shape': 'triangle'
        })
      .selector("node.ortho")
        .css({
          'background-color': "yellow"
        })
      .selector('edge.ortho')
        .css({
          'line-color': 'grey',
          'line-border': 'dashed'
        })
      .selector("edge.nOrtho")
        .css({
          "line-color": "black"
        })
      .selector('edge.species1')
        .css({
          'line-color': 'blue'
        })
      .selector('edge.species2')
        .css({
          'line-color': 'red'
        })
      .selector('edge.aligned')
        .css({
          'line-color': 'green'
        }),

    elements: JSON.parse(data),

    layout: {
      name: 'preset',
      fit: 'true'
    },

    ready: function () {

      cy = this;

      // only the nodes within the container nodes are in grid format
      // unaligned species 1
      var nodes_s1 = cy.$(function(element, i) {
        return element.hasClass('species1') && element.hasClass('unaligned') && !(element.hasClass('query'));
      })

      var hSize = Math.ceil(Math.sqrt(nodes_s1.size()));
      var vSize = Math.ceil(nodes_s1.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = -400 - (80*hSize);    // horizontal distance
      var vDist = -(80*vSize) * 0.5;    // vertical distance

      // move the query relative to the rest of the species1 nodes
      var s1_query = cy.$(function(element, i) {
        return element.hasClass('species1') && element.hasClass('query');
      })

      // change the query node position based on the position of the species1 node 'box'
      s1_query.position('x', -300);
      s1_query.position('y', 0);

      // format layout for species 1 nodes
      var layout = nodes_s1.layout({
        name: 'grid',
        nodeDimensionsIncludeLabels: true,
        fit: false,
        padding: 2,
        avoidOverlapPadding: 3,
        rows: hSize,
        cols: vSize,
        boundingBox: {x1: hDist, y1: vDist, w: 2, h: 2},
        sort: function (a, b) {
          return a.connectedEdges().classes().toString().localeCompare(b.connectedEdges().classes().toString());
        }
      });

      layout.run();
      nodes_s1.forEach(function (element, i) {
        element.move({parent: 'species1'});
      });

      // unaligned species 2
      var nodes_s2 = cy.$(function(element, i) {
        return element.hasClass('species2') && element.hasClass('unaligned') && !(element.hasClass('query'));
      })

      var hSize = Math.ceil(Math.sqrt(nodes_s2.size()));
      var vSize = Math.ceil(nodes_s2.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = 400 + hSize*80;
      var vDist = -(80*vSize) * 0.5;

      // move the query relative to the rest of the species2 nodes
      var s2_query = cy.$(function(element, i) {
        return element.hasClass('species2') && element.hasClass('query');
      })

      // change the query node position based on the position of the species2 node 'box'
      s2_query.position('x', 300);
      s2_query.position('y', 0);

      // format layout for species 2 nodes
      var layout = nodes_s2.layout({
        name: 'grid',
        nodeDimensionsIncludeLabels: true,
        fit: false,
        padding: 2,
        avoidOverlapPadding: 3,
        rows: hSize,
        cols: vSize,
        boundingBox: {x1: hDist, y1: vDist, w: 30, h: 30},
        sort: function (a, b) {
          return a.connectedEdges().classes().toString().localeCompare(b.connectedEdges().classes().toString());
        }
      });
      layout.run();
      nodes_s2.forEach(function (element, i) {
        element.move({parent: 'species2'});
      });

      // aligned non-orthology
      var nodes2 = cy.$(function(element, i) {
        return element.hasClass('aligned') && element.hasClass('nOrtho');
      })
      var hSize = Math.ceil(Math.sqrt(nodes2.size()));
      var vSize = Math.ceil(nodes2.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = -((hSize*80) + 40);
      var vDist = (80) + 40;

      // format layout for aligned non-orthologous nodes
      var layout = nodes2.layout({
        name: 'grid',
        fit: false,
        padding: 2,
        avoidOverlapPadding: 3,
        rows: hSize,
        cols: vSize,
        boundingBox: {x1: vDist, y1: hDist, w: 30, h: 30},
        sort: function (a, b) {
          return a.connectedEdges().classes().toString().localeCompare(b.connectedEdges().classes().toString());
        }
      });
      layout.run();
      nodes2.forEach(function (element, i) {
        element.move({parent: 'aligned non-ortho'});
      });

      // aligned orthology
      var nodes3 = cy.$(function(element, i) {
        return element.hasClass('aligned') && element.hasClass('ortho');
      })
      var hSize = Math.ceil(Math.sqrt(nodes3.size()));
      var vSize = Math.ceil(nodes3.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = -((hSize*80) + 40);
      var vDist = -((vSize*80) + 40);

      // format layout for orthologous nodes
      var layout = nodes3.layout({
        name: 'grid',
        fit: false,
        padding: 2,
        avoidOverlapPadding: 3,
        rows: hSize,
        cols: vSize,
        boundingBox: {x1: vDist, y1: hDist, w: 30, h: 30},
        sort: function (a, b) {
          return a.connectedEdges().classes().toString().localeCompare(b.connectedEdges().classes().toString());
        }
      });
      layout.run();
      nodes3.forEach(function (element, i) {
        element.move({parent: 'ortho'});
      });

      // node dropdown menu options
      // options to be assigned to an instance later.. ?
      var options = {
        // List of initial menu items
        menuItems: [
          {
            // protein name(s), does nothing when clicked
            id: 'protein_name',
            content: 'content here',
            tooltipText: 'Protein Name(s)',
            selector: 'node.protein, node.query, node.aligned, node.species1, node.species2'
          },
          {
            // click for ensembl link for first protein
            id: 'ensembl_link1',
            content: "ensembl species 1",
            tooltipText: "Visit Ensembl page",
            selector: "node.protein, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data('e_id').startsWith("N/A")) {

              } else {
                window.open("http://www.ensembl.org/id/" + event.target.data("e_id"));
              }
            }
          },
          {
            // click for ensembl link for second protein
            id: 'ensembl_link2',
            content: "ensembl species 2",
            tooltipText: "Visit Ensembl page",
            selector: "node.protein, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data("e_id2") === undefined) {

              } else {
                window.open("http://www.ensembl.org/id/" + event.target.data("e_id2"));
              }
            }
          },
          {
            // ncbi link for first protein
            id: "ncbi_link1",
            content: "ncbi species 1",
            tooltipText: "Visit NCBI page",
            selector: "node.position, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data("ncbi") === undefined) {

              } else {
                window.open("https://www.ncbi.nlm.nih.gov/gene/" + event.target.data("ncbi"));
              }
            }
          },
          {
            // ncbi link for second protein
            id: "ncbi_link2",
            content: "ncbi species 2",
            tooltipText: "Visit NCBI page",
            selector: "node.position, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data("ncbi2") === undefined) {

              } else {
                window.open("https://www.ncbi.nlm.nih.gov/gene/" + event.target.data("ncbi2"))
              }
            }
          },
          {
            // uniprot link for first protein
            id: "uniprot_link1",
            content: "uniprot species 1",
            tooltipText: "Visit Uniprot page",
            selector: "node.position, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data("uniprot") === undefined) {

              } else {
                window.open("https://www.uniprot.org/uniprotkb/" + event.target.data("uniprot"));
              }
            }
          },
          {
            // uniprot link for second protein
            id: "uniprot_link2",
            content: "uniprot species 2",
            tooltipText: "Visit Uniprot page",
            selector: "node.position, node.query, node.aligned, node.species1, node.species2",
            onClickFunction: function (event) {
              if (event.target.data("uniprot2") === undefined) {

              } else {
                window.open("https://www.uniprot.org/uniprotkb/" + event.target.data("uniprot2"));
              }
            }
          }
        ]
      };
      // instance of dropdown menu with options as defined above
      var instance = cy.contextMenus(options);

      cy.on("cxttapstart", "node", function (event) {


        // format protein names
        var node_name = event.target.data("name");
        var name_arr = node_name.split(",");

        //console.log(name_arr);

        instance.disableMenuItem("protein_name");
        document.getElementById("protein_name").innerHTML = node_name;

        // ensembl id species 1
        if (event.target.data("e_id") === undefined) {
          instance.hideMenuItem("ensembl_link1");
        } else {
          instance.showMenuItem("ensembl_link1");
          document.getElementById("ensembl_link1").innerHTML = "Ensembl (" + name_arr[0] + ")";
        }

        // ensembl id species 2
        if (event.target.data("e_id2") === undefined) {
          instance.hideMenuItem("ensembl_link2");
          document.getElementById("ensembl_link1").innerHTML = "ensembl";
        } else {
          instance.showMenuItem("ensembl_link2");
          document.getElementById("ensembl_link2").innerHTML = "Ensembl (" + name_arr[1] + ")";
        }

        // ncbi id species 1
        if (event.target.data("ncbi") === undefined) {
          instance.hideMenuItem("ncbi_link1");
        } else {
          instance.showMenuItem("ncbi_link1");
          document.getElementById("ncbi_link1").innerHTML = "NCBI (" + name_arr[0] + ")";
        }

        // ncbi id species 2
        if (event.target.data("ncbi2") === undefined) {
          instance.hideMenuItem("ncbi_link2");
          document.getElementById("ncbi_link1").innerHTML = "ncbi";
        } else {
          instance.showMenuItem("ncbi_link2");
          document.getElementById("ncbi_link2").innerHTML = "NCBI (" + name_arr[1] + ")";
        }

        // uniprot id species 1
        if (event.target.data("uniprot") === undefined) {
          instance.hideMenuItem("uniprot_link1");
        } else {
          instance.showMenuItem("uniprot_link1");
          document.getElementById("uniprot_link1").innerHTML = "Uniprot (" + name_arr[0] + ")";
        }

        // uniprot id species 2
        if (event.target.data("uniprot2") === undefined) {
          instance.hideMenuItem("uniprot_link2");
          document.getElementById("uniprot_link1").innerHTML = "uniprot";
        } else {
          instance.showMenuItem("uniprot_link2");
          document.getElementById("uniprot_link2").innerHTML = "Uniprot (" + name_arr[1] + ")";
        }

      });

      // implementing jscolor to change the color scheme of network
      updateSpecies1Color = function (jscolor) {
        cy.startBatch();
        // change the color of the species 1 nodes
        cy.style().selector('node.species1').css({
          'background-color': jscolor.toString()
        }).update();

        // change the color of the species 1 edges
        cy.style().selector('edge.species1').css({
          'line-color': jscolor.toString()
        }).update();

        var temp_jscolor = 'color:' + jscolor.toString();

        // change the color of the text in the legend
        document.getElementById('s1').style = temp_jscolor;

        cy.endBatch();
      };

      // implement jscolor to change the legend color for species 2
      updateSpecies2Color = function (jscolor) {
        cy.startBatch();

        // change color of species 2 nodes
        cy.style().selector('node.species2').css({
          'background-color': jscolor.toString()
        }).update();

        // change the color of species 2 edges
        cy.style().selector('edge.species2').css({
          'line-color': jscolor.toString()
        }).update();

        var temp_jscolor = 'color: ' + jscolor.toString();

        // change the color within the legend text
        document.getElementById('s2').style = temp_jscolor
        cy.endBatch();
      };

      // implement jscolor to change the legend color for the aligned proteins
      updateAlignedColor = function(jscolor) {
        cy.startBatch();

        // change color of aligned nodes
        cy.style().selector('node.nOrtho').css({
          'background-color': jscolor.toString()
        }).update();

        // change the color of aligned edges
        cy.style().selector('edge.aligned').css({
          'line-color': jscolor.toString()
        }).update();

        var temp_jscolor = 'color: ' + jscolor.toString();

        // change the color within the legend text
        document.getElementById('aligned').style = temp_jscolor;

        cy.endBatch();
      };

      updateOrthoColor = function(jscolor) {
        cy.startBatch();

        // change color of orthologous nodes
        cy.style().selector('node.ortho').css({
          'background-color': jscolor.toString()
        }).update();

        // change the color of orthologous edges
        cy.style().selector('edge.ortho').css({
          'line-color': jscolor.toString()
        }).update();

        var temp_jscolor = 'color: ' + jscolor.toString();

        // change the color within the legend text
        document.getElementById('ortho').style = temp_jscolor;

        cy.endBatch();
      };

//      // id for container species1
//      var cont_node1 = cy.$(function(element, i){
//        return element.hasClass("container") && element.hasClass("s1");
//      })

      var json = JSON.parse(data);

      species1_id = json[1]["data"]["name"];    // holds species1 name
      species2_id = json[2]["data"]["name"]

//      id for container species2
//      var cont_node2 = cy.$(function(element, i) {
//        return element.hasClass("container") && element.hasClass("s2");
//      })

      // aligned edge composition
      var align_e = cy.$(function(element, i) {
        return element.hasClass("aligned") && element.hasClass("edge");
      })

      // grab all of the edges
      var all_edges = cy.$(function(element, i) {
        return element.hasClass("edge");
      })

      // aligned edges between orthologous nodes.
      ortho_e = nodes3.connectedEdges().filter('.aligned');

      // array of stats to be passed to the windows
      var stats = [nodes3.size(), nodes2.size(), nodes_s1.size(), nodes_s2.size(), align_e.size(), ortho_e.size(), all_edges.size()];

      // buttons open corresponding windows
      openMainItem("#b1", "#button1", 'fit-content', 0, stats, species1_id, species2_id);
      //openMainItem("#b2", "#button2", 300, 20, stats, species1_id, species2_id);
      openMainItem("#b3", "#button3", 'fit-content', 500, stats, species1_id, species2_id);

      // opens the color picker option in controls
      openColorPick("#b2_colors", "#color_pick", species1_id, species2_id);

      // collect all of the proteins in the network
      var all_nodes = cy.$(function(element, i) {
        return element.hasClass("protein");
      });

      buildTable(all_nodes);

      $("#b2_data").click(function() {
        $("#data_ctrl").toggle();

        if ($("#data_ctrl").css('display') === 'block') {
          document.getElementById("cy").style = "left:25%";
        }
        else if ($("#data_ctrl").css('display') === 'none') {
          document.getElementById("cy").style = "left:5%";
        }
      });

    } // ready

  }); // cy init

  // panzoom slider with defaults
  cy.panzoom({
    animateOnFit: function() {
      return true;
    }
  });
}

// plugin initialization

// dropdown menu plugin


/**
  * Copies the text that within the data sidebar
  *
  */
function copyText() {
  var txt = document.getElementById("data_tbl").innerHTML;

  // edit the raw HTML string
  txt = txt.replace(/<tr>/g, '');
  txt = txt.replace(/<th>/g, '');
  txt = txt.replace(new RegExp("</th>", 'g'), '\t');
  txt = txt.replace(new RegExp("</tr>", 'g'), '\n');
  txt = txt.replace(/<td>/g, '');
  txt = txt.replace(new RegExp("</td>", 'g'), '\t');

  navigator.clipboard.writeText(txt);

  alert("Copied: " + txt);
}

// function for making a data table
function buildTable(proteins) {
  // building the sidebar data table
  // developed from demo on delftstack.com
  var table = document.getElementById('data_tbl');

  // create a table row for the header
  var header_row = document.createElement('tr');

  // create rows for each data section
  var heading1 = document.createElement('th');
  heading1.innerHTML = "Protein";
  var heading2 = document.createElement('th');
  heading2.innerHTML = "Species1";
  var heading3 = document.createElement('th');
  heading3.innerHTML = "Species2";
  var heading4 = document.createElement('th');
  heading4.innerHTML = "Aligned";
  var heading5 = document.createElement('th');
  heading5.innerHTML = "Orthologous";

  // append the heading to the header row
  header_row.appendChild(heading1);
  header_row.appendChild(heading2);
  header_row.appendChild(heading3);
  header_row.appendChild(heading4);
  header_row.appendChild(heading5);

  // append the row to the table
  table.appendChild(header_row);

  // loop through all of the nodes
  for (let i = 0; i < proteins.size(); i++) {
    var current = proteins[i].data("name");

    // make a 2D array of data
    // the purpose of having this overarching array is to alphabetize the data
    var data_arr = [];

    // temporary array that will hold both the name array and the class array
    var temp_arr = [];

    var name_arr = [];
    // separate proteins if two are present
    if (current.includes(",")) {
      name_arr = current.split(",");
    } else {
      name_arr.push(current);
    }

    // add the array of names to the 2D data array
    temp_arr.push(name_arr);

    // class array will hold boolean values indicating which type of node is present
    var class_arr = [];

    class_arr.push(proteins[i].hasClass("species1"));
    class_arr.push(proteins[i].hasClass("species2"));
    class_arr.push(proteins[i].hasClass("nOrtho"));
    class_arr.push(proteins[i].hasClass("ortho"));

    temp_arr.push(class_arr);

    data_arr.push(temp_arr);

    // loop through the temp array and make a row for each element
    for (let k = 0; k < name_arr.length; k++) {
      // create a row in the table
      var prot_row = document.createElement('tr');

      // data that will hold the protein name
      var name = document.createElement('td');
      name.innerHTML = name_arr[k];

      // data that will hold boolean value for species 1
      var s1 = document.createElement('td');
      s1.innerHTML = class_arr[0];

      // data that will hold boolean value for species 2
      var s2 = document.createElement('td');
      s2.innerHTML = class_arr[1];

      // boolean value for aligned non-orthologous proteins
      var al = document.createElement('td');
      al.innerHTML = class_arr[2];

      // boolean value for aligned orthologous proteins
      var ort = document.createElement('td');
      ort.innerHTML = class_arr[3];

      // append each piece of data to the current row in order
      prot_row.appendChild(name);
      prot_row.appendChild(s1);
      prot_row.appendChild(s2);
      prot_row.appendChild(al);
      prot_row.appendChild(ort);

      table.appendChild(prot_row);
    }

  }
}

// dropdown menu for header tabs
// takes the id of the dropdown div as param
function ctrlDrop(el) {
  var temp = document.getElementById(el).style.display.toString();

  // check if the dropdown is toggled on
  if (temp === "block") {
    document.getElementById(el).style.display = "none";
  } else {
    document.getElementById(el).style.display = 'block';
  };
}

// code taken from jqueryui.com
// makes elements in div window to be draggable
function dragItem(win) {
  /**
    * Pass the id for a given window by parameter so that it will be made draggable
    * using jquery
    *
    * @param win indicates the html div id for a given window
    */
  $(win)
    .draggable({
      scroll: false
  });
}

// makes elements in div window to be resizable
function resizeItem(win) {
  /**
    * Uses div id for a given window to make that div resizeable
    *
    * @param win indicates the html div id for a given window
    */
  $(win).resizable().css({'overflow': 'hidden'});
}

// opens an item that doesnt include any data that would be included in the main items
function openColorPick(button, win, s1_name, s2_name) {
  /**
    * Makes the color picker window open when a button is clicked
    *
    * @param button is the div id for the link to be clicked that will open the window
    * @param win is the html div id that will be opened when the button is clicked
    * @param s1_name is the scientific name of species 1
    * @param s2_name is the scientific name of species 2
    */
  $(button).click(function(){
    $(win).toggle();
    //resizeItem(win);
  });

  // check if the color picker window is open
  if (win === "#color_pick") {
    document.getElementById("species1_text").innerHTML = s1_name + ": ";
    document.getElementById("species2_text").innerHTML = s2_name + ": ";
  }
}


// opens the div and gives it the ability to drag and resize
// param: given button, window to be opened, y-location, x-location, list of stats, names for each species
function openMainItem(button, win, top, left, stat_list, s1_name, s2_name) {
  /**
    * opens the div and gives it the ability to drag and resize
    *
    * @param button name of button that will be clicked to open window
    * @param win name of the window to be opened
    * @param top y-coordinate where the top of the window will be placed on the screen
    * @param left x-coordinate where the left of the window will be placed on the screen
    * @param stat_list list of stats to be included in the stats window (more info below)
    * @param s1_name name of species 1
    * @param s2_name name of species 2
    */
  // stat list index definitions:
  // 0: number of orthologous aligned proteins
  // 1: number of non-orthologous aligned proteins
  // 2: number of unaligned species1
  // 3: number of unaligned species2
  // 4: number of aligned edges
  // 5: number of interologs
  // 6: count of all edges

  // check if the current button is the stats button
  if (win === "#button1") {
    // calculate percentage of orthologous aligned nodes to non-ortho aligned nodes
    var per_ortho = Math.round((stat_list[0]/(stat_list[1] + stat_list[0])) * 1000) / 10;
    //console.log(stat_list[0]/(stat_list[1] + stat_list[0])); //// making sure that the output value is correct

    var output = per_ortho + "% (" + stat_list[0] + "/" + (stat_list[1]+stat_list[0]) + ") of aligned proteins are orthologous";

    document.getElementById("b1text1").innerHTML = output;

    // number of aligned species 1 proteins
    // calculate total species 1
    var s1_total = stat_list[0] + stat_list[1] + stat_list[2];

    // calc the number of aligned proteins over total
    var s1_align = (stat_list[0] + stat_list[1]) / s1_total;

    document.getElementById("b1text2").innerHTML = Math.round(s1_align * 1000) / 10 + "% (" + (stat_list[0] + stat_list[1]) + "/" + s1_total + ") of " + s1_name + " proteins are aligned";

    // number of aligned species 2 proteins
    // calculate total species 2
    var s2_total = stat_list[0] + stat_list[1] + stat_list[3];

    // calculate the number of aligned proteins over the total
    var s2_align = (stat_list[0] + stat_list[1]) / s2_total;

    document.getElementById("b1text3").innerHTML = Math.round(s2_align * 1000) / 10 + "% (" + (stat_list[0] + stat_list[1]) + "/" + s2_total + ") of " + s2_name + " proteins are aligned";

    // number of aligned edges that are orthologous
    var intero_al = (stat_list[5] / stat_list[4]);

    document.getElementById("b1text4").innerHTML = Math.round(intero_al*1000)/10 + "% (" + stat_list[5] + "/" + stat_list[4] + ") aligned edges are interologs";

    // number of orthologous edges out of all edges
    var intero_total = (stat_list[5] / stat_list[6]);

    document.getElementById("b1text5").innerHTML = Math.round(intero_total*1000)/10 + "% (" + stat_list[5] + "/" + stat_list[6] + ") of all edges are interologs";
  };

  // check if the current button is the legend button
  if (win === "#button3") {
    // change the text within the legend window
    document.getElementById("s1").innerHTML = s1_name;
    document.getElementById("s2").innerHTML = s2_name;
  }

  $(button).click(function(){
    $(win).toggle();
    console.log($(".buttons").css("display"));
    // controls when to open and close the sidebar
    if ($("#button1").css("display") === 'block' || $('#button3').css("display") === 'block' || $("#color_pick").css("display") === 'block') {
        document.getElementById('cy').style = "left:25%;";
    }
    else if ($("#button1").css("display") === "none" && $('#button3').css("display") === 'none' && $("#color_pick").css("display") === 'none') {
        document.getElementById('cy').style = "left:5%;";
    }
  });

  $(win).css({'top': top, 'left': left, 'height': 'fit-content', 'overflow': 'hidden'});

}

// stellarnav import js
jQuery(document).ready(function($) {
    jQuery('.stellarnav').stellarNav({
        theme: 'dark',
        position: 'static',
        showArrows: true,
        sticky: false,
        closeLabel: 'Close',
        scrollbarFix: false,
        menuLabel: 'Menu'
    });
});


/*
let test = cy.$('#n0');
let neighborhood = test.neighborhood().filter('node');
for (let neighbor of neighborhood) {
  console.log(neighbor.data('id'));
} */
