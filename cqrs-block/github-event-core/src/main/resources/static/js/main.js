var workerContext = new Worker('js/worker.js');

var enableTest = false;

var width = 960,
    height = 400;

var nodes = [],
    links = [];

var graph = {
    nodes: {},
    links: {}
};

var interval;
var bars = [];
var startTime;
var events = [];

workerContext.addEventListener('message', function (e) {
    var msg = getLinks(e.data.data);
    var updateNodes = [];
    var updateLink;

    events.push(e.data.data.view.lastUpdated);

    // Append to time series
    interval = (events[events.length - 1] - startTime) / 100;
    bars = [];
    events.forEach(updateSeries);
    drawGraph();

    updateNodes.push(addNode(msg.source, msg.value));
    updateNodes.push(addNode(msg.target, msg.value));
    updateLink = addLink(msg.source, msg.target, msg.value);

    updateNodes.forEach(function(n) {
       if(n != null)
           nodes.push(n);
    });

    if(updateLink != null)
        links.push(updateLink);

    restart(true);
}, false);

function updateSeries(d) {
  if(startTime == null)
    startTime = d;

  var dT = Math.floor((d - startTime) / interval);
  if(bars[dT] == null) {
    bars[dT] = {};
    bars[dT].data = [dT];
  } else {
    bars[dT].data.push(d);
  }

  for(var i = 0; i < bars.length; i++) {
    if(bars[i] == undefined) {
      bars[i] = {};
      bars[i].data = [];
    }
  }
}

function addNode(id, value) {
    if (graph.nodes[id] == null) {
        var node = {id: id, reflexive: false, x: width / 2, y: height / 2, group: 1};
        node[0] = 0;
        node[1] = 0;
        graph.nodes[id] = node;
        return node;
    } else {
        graph.nodes[id].group++;
        return null;
    }
}

function addLink(n1, n2, value) {
    if (graph.links[n1 + "_" + n2] == null) {
        var link = {source: graph.nodes[n1], target: graph.nodes[n2], left: true, right: true, strength: value};
        graph.links[n1 + "_" + n2] = link;
        return link;
    } else {
        return null;
    }
}

function getLinks(result) {
    var view = result.view;
    return getLink(view);
}

function getLink(view) {
    return {
        source: view.fileIds[0],
        target: view.fileIds[1],
        value: view.matches
    };
}
