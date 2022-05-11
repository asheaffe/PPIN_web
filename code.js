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
    layout: {
      name: 'preset',
      padding: 5
    },
    style: [
      {
        selector: 'node',
        style: {
          'height': 25,
          'width': 25,
          'background-color': '#eee',
          'content': 'data(name)',
          'text-valign': 'center',
          'text-halign': 'center',
          'font-size': '8px'
        }
      },
      {
        selector: '$node > node',
        css: {
          'padding-top': '1px',
          'padding-left': '1px',
          'padding-bottom': '1px',
          'padding-right': '1px',
          'text-valign': 'top',
          'text-halign': 'center',
          'font-size': 14,
          'background-color': '#eee'
        }
      },
      {
        selector: 'node.species1',
        css: {
          'background-color': '#EDA1ED'
        }
      },
      {
        selector: 'node.species2',
        css: {
          'background-color': '#6FB1FC'
        }
      },
      {
        selector: 'node.queryProtein',
        css: {
          'padding': '10px',
          'font-size': 20,
          'z-index': 100
        }
      },
      {
        selector: 'node.domain',
        css: {
          'shape': 'barrel',
          'background-color': '#18e018',
          'border-style': 'solid',
          'border-color': 'black',
          'border-width': 1,
        }
      },
      {
        selector: 'node.terminus',
        css: {
          'height': 10,
          'width': 10,
          'background-color': '#18e018',
          'border-style': 'solid',
          'border-color': 'black',
          'border-width': 1
        }
      },
      {
        selector: 'node.container',
        css: {
          'z-index': 0
        }
      },
      {
        selector: 'node.matched',
        css: {
          'border-width': 2,
          'border-color': 'green',
          'font-size': '6px'
        }
      },
      {
        selector: 'node.unmatched',
        css: {
          'border-width': 2,
          'border-color': 'red',
          'font-size': '6px'
        }
      },
      {
        selector: 'node.unmatchable',
        css: {
          'border-width': 2,
          'border-color': 'blue',
          'font-size': '6px'
        }
      },
      {
        selector: 'node.hidden',
        css: {
          'display': 'none'
        }
      },
      {
        selector: 'edge',
        style: {
          'curve-style': 'haystack',
          'haystack-radius': 0,
          'width': 2,
          'opacity': 0.85,
          'line-color': '#425f42'
        }
      },
      {
        selector: 'edge.orthology',
        style: {
          'curve-style': 'haystack',
          'haystack-radius': 0,
          'width': 3,
          'opacity': 0.5,
          'line-color': 'red',
          'line-style': 'dashed'
        }
      },
      {
        selector: 'edge.protein_sequence',
        style: {
          'curve-style': 'haystack',
          'haystack-radius': 0,
          'width': 8,
          'line-color': 'black'
        }
      },
      {
        selector: 'edge.no-domain-show',
        style: {
          'display': 'none'
        }
      },
      {
        selector: 'edge.test',
        style: {
          'curve-style': 'haystack',
          'source-endpoint': '0%, -50%',
          'target-endpoint': '0%, -50%',
          'line-color': 'green'
        }
      }
    ],
    elements: JSON.parse(data),
    ready: function () {

      cy = this;
      var toggleMissingInterologs = function () {
        if ($('#missing_interolog_content').css('display') === "none") {
          $('#missing_interolog_content').css('display', "block");
          $('#toggle_missing_interologs').prop('value', 'Hide "Predicted" Interologs');
          updateMissingInterologsText();
        } else {
          $('#missing_interolog_content').css('display', "none");
          $('#toggle_missing_interologs').prop('value', '"Predict" Missing Interologs');
        }
      };
      $('#toggle_missing_interologs').off('click').on('click', toggleMissingInterologs);

      var updateMissingInterologsText = function () {
        var prefixString = '';
        if (showPredicted) {
          prefixString = 'p';
        } else {
          prefixString = 'v';
        }
        if (displayMode === 'domain') {
          prefixString += 'd';
        } else if (displayMode === 'protein') {
          prefixString += 'p';
        }

        $('#missing_interolog_content').html('<table><tr><td valign="top">' + cy.$('#' + prefixString + '_missing_interologs1').data('html') + '</td><td valign="top">' + cy.$('#' + prefixString + '_missing_interologs2').data('html') + '</td></tr></table>');
        $('#missing_interologs_header_1').css('background-color', '#' + cy.$('node.species1.protein').style('background-color').substr(1));
        $('#missing_interologs_header_1').css('color', colorIsLight(cy.$('node.species1.protein').style('background-color').substr(1)) ? '#000000' : '#FFFFFF');
        $('#missing_interologs_header_2').css('background-color', '#' + cy.$('node.species2.protein').style('background-color').substr(1));
        $('#missing_interologs_header_2').css('color', colorIsLight(cy.$('node.species2.protein').style('background-color').substr(1)) ? '#000000' : '#FFFFFF');
      };

      var updateStatsText = function () {
        var prefixString = '';
        if (showPredicted) {
          prefixString = 'p';
        } else {
          prefixString = 'v';
        }
        if (displayMode === 'domain') {
          prefixString += 'd';
        } else if (displayMode === 'protein') {
          prefixString += 'p';
        }

        var statsNode = '#' + prefixString + '_stats';

        var statsText = '<center><span style="font-weight: bold; font-size: 20px" >' + cy.$(statsNode).data('score') + '</span>% of interologs found!</center><br /><br /> ';
        statsText += cy.$(statsNode).data('numInterologs') + ' interologs found out of ' + (cy.$(statsNode).data('numMissingInterologs') + cy.$(statsNode).data('numInterologs')) + ' total possible interologs<br />';
        statsText += cy.$(statsNode).data('numInterologProteins1') + ' ' + cy.$(statsNode).data('species1') + ' ' + cy.$(statsNode).data('protein1') + ' neighbours in interologs out of ' + (cy.$(statsNode).data('numInterologProteins1') + cy.$(statsNode).data('numUnmatchedProteins1')) + ' possible interologous neighbours, ' + (cy.$(statsNode).data('numInterologProteins1') + cy.$(statsNode).data('numUnmatchedProteins1') + cy.$(statsNode).data('numUnmatchableProteins1')) + ' total neighbours<br />';
        statsText += cy.$(statsNode).data('numInterologProteins2') + ' ' + cy.$(statsNode).data('species2') + ' ' + cy.$(statsNode).data('protein2') + ' neighbours in interologs out of ' + (cy.$(statsNode).data('numInterologProteins2') + cy.$(statsNode).data('numUnmatchedProteins2')) + ' possible interologous neighbours, ' + (cy.$(statsNode).data('numInterologProteins2') + cy.$(statsNode).data('numUnmatchedProteins2') + cy.$(statsNode).data('numUnmatchableProteins2')) + ' total neighbours<br />';
        $('#stats_text').html(statsText);
      };

      var toggleContainers = function () {
        var groupNodes = cy.$('node.container');

        groupNodes.forEach(function (groupNode, i) {
          var active = hasActiveChildren(groupNode);
          if (active) {
            groupNode.style('visibility', 'visible');
          } else {
            groupNode.style('visibility', 'hidden');
          }
        });
      };

      var hasActiveChildren = function (node) {
        var active = false;
        node.children().forEach(function (child, i, children) {
          if (child.visible) {
            active = true;
            return false;
          }
        });
        return active;
      };

      var displayMode = "domain";
      var toggleDomainFunction = function () {
        if (displayMode === 'protein') {
          var elements = cy.$(function (element, i) {
            return element.hasClass('domain') || element.hasClass('terminus') || (element.hasClass('domain-target') && !element.hasClass('protein-target') && showPredicted ? true : element.hasClass("validated"));
          });
          elements.style('display', 'element');
          cy.elements('edge.no-domain-show').style('display', 'none');

          // When a matched protein that is only a domain-target has interologous partners that are not domain-targets, its
          // partners need to be moved back into matched when the domain-target is unhidden.
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('protein-target') && element.hasClass('interolog') && element.hasClass('unmatched');
          }).filter(function (node) {
            for (i = 0; i < node.data('partners').length; i++) {
              var partnerNode = cy.$('#' + node.data('partners')[i]);
              if (partnerNode.hasClass('domain-target') && !partnerNode.hasClass('protein-target') && showPredicted ? true : partnerNode.hasClass("validated")) {
                return true;
              }
            }
            return false;
          }).forEach(function (node, i) {
            node.removeClass('unmatched');
            node.addClass('matched');
            node.move({parent: "Interologs"});
          });

          displayMode = "domain";
        } else if (displayMode === 'domain') {
          cy.$(function (element, i) {
            return element.hasClass('domain') || element.hasClass('terminus') || (element.hasClass('domain-target') && !element.hasClass('protein-target'));
          }).style('display', 'none');
          cy.elements('edge.no-domain-show').style('display', 'element');

          // When a matched protein that is a protein-target has interologous partners that are all domain-targets, it needs to be moved
          // into unmatched when all its domain-target partners are hidden.
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('protein-target') && element.hasClass('interolog') && element.hasClass('matched');
          }).filter(function (node) {
            for (i = 0; i < node.data('partners').length; i++) {
              var partnerNode = cy.$('#' + node.data('partners')[i]);
              if (partnerNode.hasClass('domain-target') && !partnerNode.hasClass('protein-target')) {
                return true;
              }
            }
            return false;
          }).forEach(function (node, i) {
            node.removeClass('matched');
            node.addClass('unmatched');
            if (node.hasClass('species1')) {
              node.move({parent: 'Unmatched 1'});
            } else {
              node.move({parent: 'Unmatched 2'});
            }
          });


          displayMode = "protein";
        }

        if ($('#autolayout').is(':checked')) {
          testLayout();
        }
        updateMissingInterologsText();
        updateStatsText();
      };

      $('#toggle_mode').off('click').on('click', toggleDomainFunction);

      var showPredicted = true;
      var togglePredicted = function () {
        if (showPredicted) { // Hiding predicted nodes
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('predicted') && !element.hasClass('validated');
          }).style('display', 'none');

          // When a matched protein that is validated has interologous partners that are all predicted, it needs to be moved
          // into unmatched when all its predicted partners are hidden.
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('validated') && element.hasClass('interolog') && element.hasClass('matched');
          }).filter(function (node) {
            for (i = 0; i < node.data('partners').length; i++) {
              var partnerNode = cy.$('#' + node.data('partners')[i]);
              if (partnerNode.hasClass('predicted') && !partnerNode.hasClass('validated')) {
                return true;
              }
            }
            return false;
          }).forEach(function (node, i) {
            node.removeClass('matched');
            node.addClass('unmatched');
            if (node.hasClass('species1')) {
              node.move({parent: 'Unmatched 1'});
            } else {
              node.move({parent: 'Unmatched 2'});
            }
          });

          $('#toggle_predicted').prop('value', "Show Predicted Interactions");
        } else {
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('predicted') && !element.hasClass('validated') && displayMode === 'domain' ? true : element.hasClass('protein-target');
          }).style('display', 'element');

          // When a matched protein that is validated has interologous partners that are all predicted, it needs to be moved
          // into matched when all its predicted partners are shown.
          cy.$(function (element, i) {
            return element.isNode() && element.hasClass('validated') && element.hasClass('interolog') && element.hasClass('unmatched');
          }).filter(function (node) {
            for (i = 0; i < node.data('partners').length; i++) {
              var partnerNode = cy.$('#' + node.data('partners')[i]);
              if (partnerNode.hasClass('predicted') && !partnerNode.hasClass('validated') && displayMode === 'domain' ? true : partnerNode.hasClass('protein-target')) {
                return true;
              }
            }
            return false;
          }).forEach(function (node, i) {
            node.removeClass('unmatched');
            node.addClass('matched');
            node.move({parent: "Interologs"});
          });

          $('#toggle_predicted').prop('value', "Hide Predicted Interactions");
        }



        showPredicted = !showPredicted;
        if ($('#autolayout').is(':checked')) {
          testLayout();
        }
        updateMissingInterologsText();
        updateStatsText();
      };
      $('#toggle_predicted').off('click').on('click', togglePredicted);




      var X_SEP = 30;
      var Y_SEP = 30;

      var INTEROLOG_CENTRE_DIST = 25;
      var QUERY_CENTRE_DIST = 150;
      var DOMAIN_Y_SEP = 60;

      var MATCHED_X_DIST = 50;

      var DOMAIN_TARGET_X_DIST = 50;
      var DOMAIN_TARGET_Y_DIST = 15;

      var TARGET_X_SEP = 27;

      var NUM_DOMAINS = Math.max(cy.elements('node.domain.species1').size(), cy.elements('node.domain.species2').size());


      var getModeFilter = function () {
        if (displayMode === 'protein') {
          return mFilter1;
        } else if (displayMode === 'domain') {
          return mFilter2;
        }
        ;
      };
      var mFilter1 = function (ele) {
        return ele.hasClass('protein-target');
      };
      var mFilter2 = function (ele) {
        return ele.hasClass('protein-target') && !ele.hasClass('domain-target');
      };

      var getPredictedFilter = function () {
        if (showPredicted) {
          return pFilter1;
        } else {
          return pFilter2;
        }
      };
      var pFilter1 = function (ele) {
        return true;
      };
      var pFilter2 = function (ele) {
        return ele.hasClass('validated');
      };

      var testLayout = function () {
        cy.startBatch();
        var maxMiddle = 0;
        var tagged = cy.collection();

        // Domain-level layout
        if (displayMode === 'domain') {
          var domains1 = cy.$('node.domain.species1').sort(function (a, b) {
            return a.data('id').localeCompare(b.data('id'));
          });
          domains1.forEach(function (domain) {
            var targets = domain.neighbourhood().filter(function (element, i) {
              return element.hasClass('domain-target') && element.hasClass('matched') && !tagged.contains(element);
            });

            maxMiddle = Math.max(maxMiddle, targets.size());
          });

          var domains2 = cy.$('node.domain.species2').sort(function (a, b) {
            return a.data('id').localeCompare(b.data('id'));
          });
          domains2.forEach(function (domain) {
            var targets = domain.neighbourhood().filter(function (element, i) {
              return element.hasClass('domain-target') && element.hasClass('matched') && !tagged.contains(element);
            });

            maxMiddle = Math.max(maxMiddle, targets.size());
          });

          query_x_pos = Math.max(QUERY_CENTRE_DIST, TARGET_X_SEP * maxMiddle + 10);

          tagged = cy.collection();
          domains1.forEach(function (domain, i) {
            domain.position({x: -query_x_pos, y: i * DOMAIN_Y_SEP});

            var targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('matched') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') + DOMAIN_TARGET_X_DIST - 1 - domain.width() / 2, y1: domain.position('y') - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            targets.forEach(function (node, i) {
              node.move({parent: "Interologs"});
            });

            targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('unmatched') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') - DOMAIN_TARGET_X_DIST - (TARGET_X_SEP * targets.size()) + 1 + domain.width() / 2, y1: domain.position('y') - DOMAIN_TARGET_Y_DIST - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            // Remove domains from their organizing boxes if any
            targets.nonorphans().forEach(function (node, i) {
              node.move({parent: null});
            });

            targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('unmatchable') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') - DOMAIN_TARGET_X_DIST - (TARGET_X_SEP * targets.size()) + 1 + domain.width() / 2, y1: domain.position('y') + DOMAIN_TARGET_Y_DIST - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            targets.nonorphans().forEach(function (node, i) {
              node.move({parent: null});
            });

            tagged = tagged.add(domain.neighbourhood().filter('node.domain-target'));

          });

          cy.$('#n-term-1').position({x: -query_x_pos, y: -25});
          cy.$('#c-term-1').position({x: -query_x_pos, y: Math.max((cy.elements('node.domain.species1').size() - 1), 0) * DOMAIN_Y_SEP + 25});

          tagged = cy.collection();
          domains2.forEach(function (domain, i) {
            domain.position({x: query_x_pos, y: i * DOMAIN_Y_SEP});

            var targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('matched') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') - DOMAIN_TARGET_X_DIST - (TARGET_X_SEP * (targets.size() - 1)) - 1 - domain.width() / 2, y1: domain.position('y') - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            targets.forEach(function (node, i) {
              node.move({parent: "Interologs"});
            });

            targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('unmatched') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') + DOMAIN_TARGET_X_DIST - 1 - domain.width() / 2, y1: domain.position('y') - DOMAIN_TARGET_Y_DIST - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            // Remove domains from their organizing boxes if any
            targets.nonorphans().forEach(function (node, i) {
              node.move({parent: null});
            });

            targets = domain.neighbourhood().filter(function (element, i) {
              return getPredictedFilter()(element) && element.hasClass('domain-target') && element.hasClass('unmatchable') && !tagged.contains(element);
            });

            targets.layout({
              name: 'grid',
              fit: false,
              padding: 0,
              avoidOverlapPadding: 0,
              rows: 1,
              condense: true,
              boundingBox: {x1: domain.position('x') + DOMAIN_TARGET_X_DIST - 1 - domain.width() / 2, y1: domain.position('y') + DOMAIN_TARGET_Y_DIST - 1 - domain.height() / 2, w: 1, h: 1},
              sort: function (a, b) {
                return a.data('name').localeCompare(b.data('name'));
              }
            }).run();

            targets.nonorphans().forEach(function (node, i) {
              node.move({parent: null});
            });

            tagged = tagged.add(domain.neighbourhood().filter('node.domain-target'));
          });

          cy.$('#n-term-2').position({x: query_x_pos, y: -25});
          cy.$('#c-term-2').position({x: query_x_pos, y: Math.max((cy.elements('node.domain.species2').size() - 1), 0) * DOMAIN_Y_SEP + 25});
        }

        var col1 = [];
        var col2 = [];
        var predictDump1 = cy.collection();
        var predictDump2 = cy.collection();

        var interoPartners1 = cy.$(function (element) {
          return getModeFilter()(element) && element.hasClass('matched') && element.hasClass('species1');
        }).sort(function (a, b) {
          return a.data('name').localeCompare(b.data('name'));
        });

        interoPartners1.forEach(function (interoPartner1) {
          if (!getPredictedFilter()(interoPartner1)) {
            return null;
          }

          var interoPartners2 = interoPartner1.neighbourhood().filter(function (element) {
            return getPredictedFilter()(element) && element.hasClass('matched') && element.hasClass('species2');
          });

          // Other side of interolog is a predicted interaction, and show predicted is off, so throw protein into unmatched
          if (interoPartners2.size() === 0) {
            predictDump1.add(interoPartner1);
            return null;
          }

          interoPartners2 = interoPartners2.filter(function (element) {
            return getModeFilter()(element) && !col2.includes(element);
          }).sort(function (a, b) {
            return a.data('name').localeCompare(b.data('name'));
          });

          col1.push(interoPartner1);

          // Interolog partners are domain-mediated interactions, domain mode is on, so throw unbalanced pair for display
          if (interoPartners2.size() === 0) {
            col2.push(null);
            return null;
          }

          interoPartners2.forEach(function (interoPartner2) {
            col2.push(interoPartner2);
          });

          for (i = 1; i < interoPartners2.size(); i++) {
            col1.push(null);
          }
        });

        var interoPartners2 = cy.$(function (element) {
          return getModeFilter()(element) && element.hasClass('matched') && element.hasClass('species2') && !col2.includes(element);
        }).sort(function (a, b) {
          return a.data('name').localeCompare(b.data('name'));
        });

        interoPartners2.forEach(function (interoPartner2, i) {
          if (!getPredictedFilter()(interoPartner2)) {
            return null;
          }

          interoPartners1 = interoPartner2.neighbourhood().filter(function (element) {
            return getPredictedFilter()(element) && element.hasClass('matched') && element.hasClass('species1');
          });

          // Other side of interolog is a predicted interaction, and show predicted is off, so throw protein into unmatched
          if (interoPartners1.size() === 0) {
            predictDump2.add(interoPartner2);
            return null;
          }

          col1.push(null);
          col2.push(interoPartner2);
        });

        var matchedYDist = ((displayMode === 'domain') ? -col1.length * Y_SEP : ((-col1.length / 2) * Y_SEP + (NUM_DOMAINS / 2 - 1) * DOMAIN_Y_SEP));


        col1.forEach(function (ele, i) {
          if (ele) {
            ele.position({x: -INTEROLOG_CENTRE_DIST, y: matchedYDist + i * Y_SEP});
            ele.move({parent: 'Interologs'});

          }
        });

        col2.forEach(function (ele, i) {
          if (ele) {
            ele.position({x: INTEROLOG_CENTRE_DIST, y: matchedYDist + i * Y_SEP});
            ele.move({parent: 'Interologs'});
          }
        });

        var unmatchedYDist = ((displayMode === 'domain') ? -1 : (NUM_DOMAINS / 2 - 1)) * DOMAIN_Y_SEP;
        matched_x_pos = query_x_pos + MATCHED_X_DIST;

        // Unmatched 1
        var nodes = cy.$(function (element, i) {
          return getModeFilter()(element) && getPredictedFilter()(element) && element.hasClass('unmatched') && element.hasClass('species1');
        }).add(predictDump1);
        var hSize = Math.ceil(Math.sqrt(nodes.size()));
        var vSize = Math.ceil(nodes.size() / hSize);


        var layout = nodes.layout({
          name: 'grid',
          fit: false,
          padding: 3,
          avoidOverlapPadding: 3,
          rows: vSize,
          cols: hSize,
          boundingBox: {x1: -matched_x_pos - (X_SEP * hSize), y1: unmatchedYDist - (Y_SEP * vSize), w: 1, h: 1},
          sort: function (a, b) {
            return a.data('name').localeCompare(b.data('name'));
          }
        });
        layout.run();
        nodes.forEach(function (element, i) {
          element.move({parent: 'Unmatched 1'});
        });

        // Unmatched 2
        nodes = cy.$(function (element, i) {
          return getModeFilter()(element) && getPredictedFilter()(element) && element.hasClass('unmatched') && element.hasClass('species2');
        }).add(predictDump2);
        hSize = Math.ceil(Math.sqrt(nodes.size()));
        vSize = Math.ceil(nodes.size() / hSize);


        layout = nodes.layout({
          name: 'grid',
          fit: false,
          padding: 3,
          avoidOverlapPadding: 3,
          rows: vSize,
          cols: hSize,
          boundingBox: {x1: matched_x_pos, y1: unmatchedYDist - (Y_SEP * vSize), w: 1, h: 1},
          sort: function (a, b) {
            return a.data('name').localeCompare(b.data('name'));
          }
        });
        layout.run();
        nodes.forEach(function (element, i) {
          element.move({parent: 'Unmatched 2'});
        });

        var unmatchableYDist = ((displayMode === 'domain') ? NUM_DOMAINS : NUM_DOMAINS / 2) * DOMAIN_Y_SEP;

        // Unmatchable 1
        nodes = cy.$(function (element, i) {
          return getModeFilter()(element) && getPredictedFilter()(element) && element.hasClass('unmatchable') && element.hasClass('species1');
        });
        size = Math.ceil(Math.sqrt(nodes.size()));

        layout = nodes.layout({
          name: 'grid',
          fit: false,
          padding: 3,
          avoidOverlapPadding: 3,
          rows: size,
          cols: size,
          boundingBox: {x1: -matched_x_pos - (X_SEP * size), y1: unmatchableYDist, w: 1, h: 1},
          sort: function (a, b) {
            return a.data('name').localeCompare(b.data('name'));
          }
        });
        layout.run();
        nodes.forEach(function (element, i) {
          element.move({parent: 'Unmatchable 1'});
        });

        // Unmatchable 2
        nodes = cy.$(function (element, i) {
          return getModeFilter()(element) && getPredictedFilter()(element) && element.hasClass('unmatchable') && element.hasClass('species2');
        });
        size = Math.ceil(Math.sqrt(nodes.size()));

        layout = nodes.layout({
          name: 'grid',
          fit: false,
          padding: 3,
          avoidOverlapPadding: 3,
          rows: size,
          cols: size,
          boundingBox: {x1: matched_x_pos, y1: unmatchableYDist, w: 1, h: 1},
          sort: function (a, b) {
            return a.data('name').localeCompare(b.data('name'));
          }
        });
        layout.run();

        nodes.forEach(function (element, i) {
          element.move({parent: 'Unmatchable 2'});
        });

        // Extra code to deal with weird eles.move() glitch
        if (displayMode === 'domain') {
          cy.elements('edge.no-domain-show').style('display', 'none');
        } else if (displayMode === 'protein') {
          cy.elements('edge.no-domain-show').style('display', 'element');
        }

        toggleContainers();

        tagged = null;
        domains1 = null;
        domains2 = null;
        nodes = null;
        layout = null;
        col1 = null;
        col2 = null;
        interoPartners1 = null;

        cy.endBatch();
      };
      $('#layout_button').off('click').on('click', testLayout);

      $('#scale_button').off('click').on('click', function () {
        cy.fit();
      });

      var options = {
        // List of initial menu items
        menuItems: [
          {
            id: 'protein_name',
            content: 'Dummy text',
            tooltipText: 'Protein information',
            selector: 'node.protein, node.queryProtein'
          },
          {
            id: 'protein_length',
            content: 'Dummy text',
            tooltipText: 'Protein length information',
            selector: 'node.protein, node.queryProtein',
          },
          {
            id: 'protein_neighbours',
            content: 'Dummy text',
            tooltipText: 'Protein neighbour information',
            selector: 'node.protein, node.queryProtein',
            hasTrailingDivider: true
          },
          {
            id: 'ensembl_link',
            content: 'Visit Ensembl page',
            tooltipText: 'Visit Ensembl page',
            selector: 'node.protein, node.queryProtein',
            onClickFunction: function (event) {
              if (event.target.data('id').startsWith('N/A')) {
              } else {
                try { // your browser may block popups
                  window.open('http://www.ensembl.org/id/' + event.target.data('id'));
                } catch (e) { // fall back on url change
                  window.location.href = 'http://www.ensembl.org/id/' + event.target.data('id');
                }
              }
            },
            hasTrailingDivider: true
          },
          {
            id: 'hide',
            content: 'hide',
            tooltipText: 'hide',
            selector: '*',
            onClickFunction: function (event) {
              var target = event.target || event.cyTarget;
              target.hide();
            },
            disabled: false
          }
        ]
      };
      var instance = cy.contextMenus(options);

      cy.on("cxttapstart", "node", function (event) {
        if (event.target.data('id').startsWith('N/A')) {
          $("#protein_name").html(event.target.data('name') + " (N/A)");
          $("#protein_length").html("Length: N/A");
          $("#protein_neighbours").html("N/A neighbours");
        } else {
          $("#protein_name").html(event.target.data('name') + " (" + event.target.data('id') + ")");
          $("#protein_length").html("Length: " + event.target.data('length') + " amino acids");
          $("#protein_neighbours").html(event.target.data('num_neighbours') + " neighbours: (" + event.target.data('neighbours') + ")");
        }
        ;
      });





      // Color picker code
      $('#legend_species1').css('background-color', cy.$('node.species1.protein').style("background-color"));
      $('#legend_species2').css('background-color', cy.$('node.species2.protein').style("background-color"));
      $('#legend_domain').css('background-color', cy.$('node.domain').style("background-color"));
      $('#legend_matched').css('border-color', cy.$('node.matched').style("border-color"));
      $('#legend_unmatched').css('border-color', cy.$('node.unmatched').style("border-color"));
      $('#legend_unmatchable').css('border-color', cy.$('node.unmatchable').style("border-color"));

      $('#name_species1').text(cy.$('#stats').data('species1'));
      $('#name_species2').text(cy.$('#stats').data('species2'));



      colorIsLight = function (jscolor) {

        var result = /gb\((\d{1,3}),\s?(\d{1,3}),\s?(\d{1,3})\)/i.exec(jscolor);
        var r, g, b;
        if (result != null) {
          r = parseInt(result[1], 16);
          g = parseInt(result[2], 16);
          b = parseInt(result[3], 16);
        } else {
          result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(jscolor);
          r = parseInt(result[1], 16);
          g = parseInt(result[2], 16);
          b = parseInt(result[3], 16);
        }
        return (
                0.213 * r +
                0.715 * g +
                0.072 * b >
                255 / 2
                );
      };

      updateSpecies1Color = function (jscolor) {
        cy.startBatch();
        cy.style().selector('node.species1.protein').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        cy.style().selector('node.species1.queryProtein').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        $('#missing_interologs_header_1').css('background-color', '#' + jscolor);
        $('#missing_interologs_header_1').css('color', colorIsLight(jscolor) ? '#000000' : '#FFFFFF');
        $('#legend_species1').css('background-color', '#' + jscolor);
        cy.endBatch();
      };
      updateSpecies2Color = function (jscolor) {
        cy.startBatch();
        cy.style().selector('node.species2.protein').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        cy.style().selector('node.species2.queryProtein').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        $('#missing_interologs_header_2').css('background-color', '#' + jscolor);
        $('#missing_interologs_header_2').css('color', colorIsLight(jscolor) ? '#000000' : '#FFFFFF');
        $('#legend_species2').css('color', colorIsLight(jscolor) ? '#000' : '#FFF');
        cy.endBatch();
      };
      updateDomainColor = function (jscolor) {
        cy.startBatch();
        cy.style().selector('node.domain').style({
          'background-color': '#' + jscolor
        }).update();
        cy.style().selector('node.terminus').style({
          'background-color': '#' + jscolor
        }).update();
        cy.style().selector('node.domain').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        cy.style().selector('node.terminus').style({
          'color': colorIsLight(jscolor) ? '#000000' : '#FFFFFF',
          'background-color': '#' + jscolor
        }).update();
        $('#legend_domain').css('background-color', '#' + jscolor);
        cy.endBatch();
      };
      updateMatchedColor = function (jscolor) {
        cy.style().selector('node.matched').style({
          'border-color': '#' + jscolor
        }).update();
        $('#legend_matched').css('border-color', '#' + jscolor);
      };
      updateUnmatchedColor = function (jscolor) {
        cy.style().selector('node.unmatched').style({
          'border-color': '#' + jscolor
        }).update();
        $('#legend_unmatched').css('border-color', '#' + jscolor);
      };
      updateUnmatchableColor = function (jscolor) {
        cy.style().selector('node.unmatchable').style({
          'border-color': '#' + jscolor
        }).update();
        $('#legend_unmatchable').css('border-color', '#' + jscolor);
      };

      testLayout();
      updateMissingInterologsText();
      updateStatsText();
    }
  });



//        .catch(function (error) {
//          alert("Please select a valid PPAAT JSON file.");
//        });



}

