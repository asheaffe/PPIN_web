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
      { data: { source: 'n1', target: 'n2' } },
      { data: { source: 'n2', target: 'n4' } },
      { data: {source: 'n0', target: 'n4'}},
      { data: {source: 'n3', target: 'n2'}}
    ]
  },

  layout: {
    name: 'breadthfirst',
    padding: 250,
  }
}); // cy init

cy.on('tap', 'node', function(){
  var nodes = this;
  var tapped = nodes;
  var network = [];

  for(;;){
    var connectedEdges = nodes.connectedEdges(function(el){
      return !el.target().anySame( nodes );
    });

    var connectedNodes = connectedEdges.targets();

    Array.prototype.push.apply( network, connectedNodes );

    nodes = connectedNodes;

    if( nodes.empty() ){ break; }
  }

  for( var i = network.length - 1; i >= 0; i-- ){ (function(){
    var thisNode = network[i];
    var parent = thisNode.connectedEdges(function(el){
      return el.target().same(thisNode);
    }).source();

    /*
    thisNode.delay( delay, function(){
      parent.addClass('child');
    } ).animate({
      position: parent.position(),
      css: {
        'width': 10,
        'height': 10,
        'border-width': 0,
        'opacity': 0
      }
    }, {
      duration: duration,
      complete: function(){
        thisNode.remove();
      }
    });

    delay += duration;*/
  })(); } // for

}); // on tap
