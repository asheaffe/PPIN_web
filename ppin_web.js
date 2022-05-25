// Base code originates from the breadthfirst layout on js.cytoscape.org

var cy = cytoscape({
  container: document.getElementById('cy'),

  boxSelectionEnabled: false,
  autounselectify: true,

  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'height': 80,
        'width': 80,
        'background-fit': 'cover',
        'border-color': 'black',
        //'background-color': 'red',
        'border-width': 3,
        'border-opacity': 0.5
      })
    .selector('edge')
      .css({
        'curve-style': 'haystack',
        'width': 6,
        'line-color': '#425f42'
      })
    .selector('node.species1')
      .css({
        "background-color": "blue"
      })
    .selector('node.species2')
      .css({
        "background-color": "red"
      })
    .selector('node.matched')
      .css({
        'border-width': 2,
        'border-color': 'black',
        'font-size': 6,
        'background-color': 'green'
      })
    .selector('node.unmatched')
      .css({
        "border-width": 2,
        "border-color": "black",
        'font-size': 6
      })
    .selector('edge.orthology')
      .css({
        'curve-style': 'haystack',
        'line-color': 'green',
        'line-style': 'dashed'
      })
    .selector('edge.species1')
      .css({
        'line-color': 'blue'
      })
    .selector('edge.species2')
      .css({
        'line-color': 'red'
      })
    .selector('edge.matched')
      .css({
        'line-color': 'green'
      }),

  elements: {
    nodes: [
      // species 1
      {
        data: {
          "num_neighbors": 2,
          "id": 'n0',
          //"length": 5,
          //"weight": 7,
          "neighbors": "n2, n4"
        },
        "selectable": true,
        "classes": "species1 matched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 1,
          "id": 'n1',
          //"length": 4,
          //"weight": 2,
          "neighbors": "n2"
        },
        "selectable": true,
        "classes": "species1 matched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 2,
          "id": 'n2',
          //"length": 7,
          //"weight": 0,
          "neighbors": "n1, n0"
        },
        "selectable": true,
        "classes": "species1 unmatched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 1,
          "id": 'n3',
          //"length": 2,
          //"weight": 6,
          "neighbors": "n4"
        },
        "selectable": true,
        "classes": "species1 unmatched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 2,
          "id": 'n4',
          //"length": 4,
          //"weight": 0,
          "neighbors": "n3, n0"
        },
        "selectable": true,
        "classes": "species1 unmatched predicted",
        "selected": false
      },

      // species2
      {
        data: {
          "num_neighbors": 3,
          "id": 'n0',
          //"length": 3,
          //"weight": 1,
          "neighbors": "n1, n7, n8"
        },
        "selectable": true,
        "classes": "species2 matched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 2,
          "id": 'n1',
          //"length": 4,
          //"weight": 0,
          "neighbors": "n0, n8"
        },
        "selectable": true,
        "classes": "species2 matched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 1,
          "id": 'n7',
          //"length": 2,
          //"weight": 4,
          "neighbors": "n0"
        },
        "selectable": true,
        "classes": "species2 unmatched predicted",
        "selected": false
      },
      {
        data: {
          "num_neighbors": 2,
          "id": 'n8',
          //"length": 8,
          //"weight": 4,
          "neighbors": "n0, n1"
        },
        "selectable": true,
        "classes": "species2 unmatched predicted",
        "selected": false
      }
    ],
    edges: [


      // edges
      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n2'
        },
        "classes": "species1 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n4'
        },
        "classes": "species1 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n1',
          "target": 'n2'
        },
        "classes": "species1 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n4'
        },
        "classes": "species1 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n3',
          "target": 'n4'
        },
        "classes": "species1 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n1'
        },
        "classes": "species2 matched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n7'
        },
        "classes": "species2 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n0',
          "target": 'n8'
        },
        "classes": "species2 unmatched",
        "selectable": "true",
        "selected": "false"
      },

      {
        data: {
          "weight": 60,
          "source": 'n1',
          "target": 'n8'
        },
        "classes": "species2 unmatched",
        "selectable": "true",
        "selected": "false"
      }
    ]
  },

  layout: {
    name: 'circle',
  },

  ready: function () {
    // buttons open corresponding windows
    openItem("#b1", "#button1");
    openItem("#b2", "#button2");
    openItem("#b3", "#button3");
  }
}); // cy init

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
