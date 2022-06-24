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
      .selector("node")
        .css({
          "border-width": 2,
          "border-color": "black",
          "height": 80,
          "width": 80
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

    elements: JSON.parse(data),

    layout: {
      name: 'grid',
    },

    ready: function () {
      cy = this;
      // buttons open corresponding windows
      openItem("#b1", "#button1");
      openItem("#b2", "#button2");
      openItem("#b3", "#button3");

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
