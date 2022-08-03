// Base code originates from the breadthfirst layout on js.cytoscape.org
document.getElementById('myFile').addEventListener('change', loadFile);

function loadFile(event) {
  f = event.target.files[0];

  fr = new FileReader();
  fr.addEventListener("load", e => {
    runCytoscape(fr.result);
  });

  fr.readAsText(f);
}

function runCytoscape(data) {
  var cy = window.cy = cytoscape({
    container: document.getElementById('cy'),

    boxSelectionEnabled: false,
    autounselectify: true,

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
          "background-color": "blue"
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
          'line-color': 'green'
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
    },

    ready: function () {

      cy = this;

      // only the nodes within the container nodes are in grid format
      // unaligned species 1
      var nodes_s1 = cy.$(function(element, i) {
        return element.hasClass('species1') && element.hasClass('unaligned');
      })

      var hSize = Math.ceil(Math.sqrt(nodes_s1.size()));
      var vSize = Math.ceil(nodes_s1.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = (80) + 40;
      var vDist = (80) + 40;

      var layout = nodes_s1.layout({
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
      nodes_s1.forEach(function (element, i) {
        element.move({parent: 'worm'});
      });

      // unaligned species 2
      var nodes_s2 = cy.$(function(element, i) {
        return element.hasClass('species2') && element.hasClass('unaligned');
      })

      var hSize = Math.ceil(Math.sqrt(nodes_s2.size()));
      var vSize = Math.ceil(nodes_s2.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = (80) + 40;
      var vDist = -((vSize*80) + 40);

      var layout = nodes_s2.layout({
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
      nodes_s2.forEach(function (element, i) {
        element.move({parent: 'yeast'});
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
        element.move({parent: 'aligned ortho'});
      });

      // node dropdown menu options
      // options to be assigned to an instance later.. ?
      var options = {
        // List of initial menu items
        menuItems: [
          {
            id: 'protein_name',
            content: 'content here',
            tooltipText: 'Protein Name(s)',
            selector: 'node.protein, node.query, node.aligned, node.species1, node.species2',
          },
          {
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


        // format protein name
        var node_name = event.target.data("name");
        var name_arr = node_name.split(",");

        instance.disableMenuItem("protein_name");
        document.getElementById("protein_name").innerHTML = node_name;

        // ensembl id species 1
        if (event.target.data("e_id") === undefined) {
          instance.hideMenuItem("ensembl_link1");
        } else {
          document.getElementById("ensembl_link1").innerHTML = "ensembl " + name_arr[0];
        }

        // ensembl id species 2
        if (event.target.data("e_id2") === undefined) {
          instance.hideMenuItem("ensembl_link2");
          document.getElementById("ensembl_link1").innerHTML = "ensembl";
        } else {
          instance.showMenuItem("ensembl_link2");
          document.getElementById("ensembl_link2").innerHTML = "ensembl" + name_arr[1];
        }

        // ncbi id species 1
        if (event.target.data("ncbi") === undefined) {
          instance.hideMenuItem("ncbi_link1");
        } else {
          document.getElementById("ncbi_link1").innerHTML = "ncbi " + name_arr[0];
        }

        // ncbi id species 2
        if (event.target.data("ncbi2") === undefined) {
          instance.hideMenuItem("ncbi_link2");
          document.getElementById("ncbi_link1").innerHTML = "ncbi";
        } else {
          document.getElementById("ncbi_link2").innerHTML = "ncbi" + name_arr[1];
        }

        // uniprot id species 1
        if (event.target.data("uniprot") === undefined) {
          instance.hideMenuItem("uniprot_link1");
        } else {
          document.getElementById("uniprot_link1").innerHTML = "uniprot " + name_arr[0];
        }

        // uniprot id species 2
        if (event.target.data("uniprot2") === undefined) {
          instance.hideMenuItem("uniprot_link2");
          document.getElementById("uniprot_link1").innerHTML = "uniprot";
        } else {
          document.getElementById("uniprot_link2").innerHTML = "uniprot" + name_arr[1];
        }

      });

      // id for container species1
      var cont_node1 = cy.$(function(element, i){
        return element.hasClass("container") && element.hasClass("s1");
      })

      species1_id = cont_node1.id();

      // id for container species2
      var cont_node2 = cy.$(function(element, i) {
        return element.hasClass("container") && element.hasClass("s2");
      })

      species2_id = cont_node2.id();

      // aligned edge composition
      var align_e = cy.$(function(element, i) {
        return element.hasClass("aligned") && element.hasClass("edge");
      })

      // array of stats to be passed to the windows
      var stats = [nodes3.size(), nodes2.size(), nodes_s1.size(), nodes_s2.size(), align_e.size()];

      // buttons open corresponding windows
      openItem("#b1", "#button1", 110, 20, stats, species1_id, species2_id);
      openItem("#b2", "#button2", 300, 20, stats, species1_id, species2_id);
      openItem("#b3", "#button3", 110, 250, stats, species1_id, species2_id);

    } // ready
  }); // cy init
}

// code taken from jqueryui.com
// makes elements in div window to be draggable
function dragItem(win) {
  $(win)
    .draggable({
      containment: "#cy",
      scroll: false
  });
}

// makes elements in div window to be resizable
function resizeItem(win) {
  $(win).resizable().css({'overflow': 'hidden'});
}

// opens the div and gives it the ability to drag and resize
// param: given button, window to be opened, y-location, x-location, list of stats, names for each species
function openItem(button, win, top, left, stat_list, s1_name, s2_name) {
  // stat list index definitions:
  // 0: number of orthologous aligned proteins
  // 1: number of non-orthologous aligned proteins
  // 2: number of unaligned species1
  // 3: number of unaligned species2
  // 4: number of aligned edges

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
  };

  // check if the current button is the legend button
  if (win === "#button3") {
    document.getElementById("s1").innerHTML = s1_name;
    document.getElementById("s2").innerHTML = s2_name;
  }

  $(button).click(function(){
    $(win).toggle();
    dragItem(win);
    resizeItem(win);
  });
  $(win).css({'top': top, 'left': left, 'width': 'fit-content', 'height': 'fit-content', 'padding': "10", 'overflow': 'hidden'});

}


/*
let test = cy.$('#n0');
let neighborhood = test.neighborhood().filter('node');
for (let neighbor of neighborhood) {
  console.log(neighbor.data('id'));
} */
