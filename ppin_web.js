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
      // buttons open corresponding windows
      openItem("#b1", "#button1");
      openItem("#b2", "#button2");
      openItem("#b3", "#button3");

      // only the nodes within the container nodes are in grid format
      // unaligned species 1
      var nodes = cy.$(function(element, i) {
        return element.hasClass('species1');
      })

      var hSize = Math.ceil(Math.sqrt(nodes.size()));
      var vSize = Math.ceil(nodes.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = (80) + 40;
      var vDist = (80) + 40;

      var layout = nodes.layout({
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
      nodes.forEach(function (element, i) {
        element.move({parent: 'unaligned1'});
      });

      // unaligned species 2
      var nodes = cy.$(function(element, i) {
        return element.hasClass('species2');
      })

      var hSize = Math.ceil(Math.sqrt(nodes.size()));
      var vSize = Math.ceil(nodes.size() / hSize);

      // hDist and vDist use the size of the nodes and the number of
      // rows and columns to calculate distance from the query
      var hDist = (80) + 40;
      var vDist = -((vSize*80) + 40);

      var layout = nodes.layout({
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
      nodes.forEach(function (element, i) {
        element.move({parent: 'unaligned2'});
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

    }
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
  $(win).resizable();
}

// opens the div and gives it the ability to drag and resize
function openItem(button, win) {
  $(button).click(function(){
    $(win).toggle();
    dragItem(win);
    resizeItem(win);
  });
}


/*
let test = cy.$('#n0');
let neighborhood = test.neighborhood().filter('node');
for (let neighbor of neighborhood) {
  console.log(neighbor.data('id'));
} */
