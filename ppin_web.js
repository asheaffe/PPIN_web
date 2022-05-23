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
        'border-color': '#000',
        'background-color': 'red',
        'border-width': 3,
        'border-opacity': 0.5
      })
    .selector('edge')
      .css({
        'curve-style': 'bezier',
        'width': 6,
        'line-color': '#ffaaaa'
      })
    .selector('$node > node')
      .css({
        'padding-top': '1px',
        'padding-left': '1px',
        'padding-bottom': '1px',
        'padding-right': '1px',
        'text-valign': 'top',
        'text-halign': 'center',
        'font-size': 14,
        'background-color': '#eee'
      }),

  elements: {
    nodes: [
      { data: { id: 'g1n0'}},
      { data: { id: 'g1n1' }},
      { data: { id: 'g1n2' }},
      { data: { id: 'g1n3' }},
      { data: { id: 'g1n4' }},

      { data: { id: 'g2n0'}},
      { data: { id: 'g2n1' }},
      { data: { id: 'g2n2' }},
      { data: { id: 'g2n3' }}
    ],
    edges: [
      {data: {source: 'g1n1', target: 'g1n0'}},
      {data: {source: 'g1n2', target: 'g1n0'}},
      {data: {source: 'g1n3', target: 'g1n0'}},
      {data: {source: 'g1n4', target: 'g1n3'}},
      {data: {source: 'g1n0', target: 'g1n4'}},

      {data: {source: 'g2n0', target: 'g2n1'}},
      {data: {source: 'g2n1', target: 'g2n2'}},
      {data: {source: 'g2n3', target: 'g2n0'}},

      {data: {source: 'g1n0', target: 'g2n0'}}
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
// makes elements in div id "draggable" to be draggable
function dragItem(win) {
  $(win)
    .draggable({
      containment: "#cy",
      scroll: false
  });
}

function resizeItem(win) {
  $(win).resizable();
}

function openItem(button, win) {
  $(button).click(function(){
    $(win).show();
    dragItem(win);
    resizeItem(win);
  });
}
