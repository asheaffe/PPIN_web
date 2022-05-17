// photos from flickr with creative commons license

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
        'background-color': 'black',
        'border-width': 3,
        'border-opacity': 0.5
      })
    .selector('edge')
      .css({
        'curve-style': 'bezier',
        'width': 6,
        'line-color': 'black',
        'target-arrow-color': '#ffaaaa'
      }),

  elements: {
    nodes: [
      { data: { id: 'n0' } },
      { data: { id: 'n1' } },
      { data: { id: 'n2' } },
      { data: { id: 'n3' } }
    ],
    edges: [
      { data: { source: 'n1', target: 'n0' } },
      { data: { source: 'n2', target: 'n0' } },
      {data: {source: 'n3', target: 'n2'}}
    ]
  },

  layout: {
    name: 'breadthfirst',
    directed: true,
    padding: 10
  }
}); // cy init

cy.on('tap', 'node', function(){
  var nodes = this;
  var tapped = nodes;
  var network = [];

  for( var i = network.length - 1; i >= 0; i-- ){ (function(){
    var thisNode = network[i];
    var connection = thisNode.connectedEdges(function(el){
      return el.target().same(thisNode);
    }).source();

  })(); } // for

}); // on tap
