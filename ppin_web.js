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
      { data: { id: 'n0' }},
      { data: { id: 'n1' }},
      { data: { id: 'n2' }},
      { data: { id: 'n3' }},
      { data: { id: 'n4' }}
    ],
    edges: [
      { data: { source: 'n1', target: 'n0' } },
      {data: {source: 'n2', target: 'n0'}},
      {data: {source: 'n3', target: 'n0'}},
      {data: {source: 'n4', target: 'n3'}},
      {data: {source: 'n0', target: 'n4'}}
    ]
  },

  layout: {
    name: 'circle',
  }
}); // cy init
