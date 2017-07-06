var workerContext = new Worker('js/worker.js');

var enableTest = true;

var width = 700,
    height = 400;

var nodes = [],
    links = [];

var graph = {
    nodes: {},
    links: {}
};

var interval;
var bars = [];
var events = [];

workerContext.addEventListener('message', function (e) {
    var msg = getLinks(e.data.data);
    var updateNodes = [];
    var updateLink;
    var ts;

    if (enableTest) {
        ts = e.data.data.createdDate;
    } else {
        ts = e.data.data.view.lastModified;
    }


    events.push({timestamp: ts, weight: msg.value});

    if (events.length > 1) {
        // Append to time series
        lineX = d3.scaleTime()
            .domain([events[0].timestamp, events[events.length - 1].timestamp])
            .range([0, width]);
    }

    updateNodes.push(addNode(msg.source, msg.value));
    updateNodes.push(addNode(msg.target, msg.value));
    updateLink = addLink(msg.source, msg.target, msg.value);

    updateNodes.forEach(function (n) {
        if (n != null)
            nodes.push(n);
    });

    if (updateLink != null)
        links.push(updateLink);

    restart(true);

}, false);

function updateSeries(d) {
    var dT = Math.floor((d.timestamp - events[0].timestamp) / interval);
    if (bars[dT] == null) {
        bars[dT] = {};
        bars[dT].data = [dT];
        bars[dT].timestamp = d.timestamp;
        bars[dT].weight = d.weight;
    } else {
        bars[dT].data.push(d.timestamp);
        bars[dT].weight += d.weight;
    }

    for (var i = 1; i < bars.length; i++) {
        if (bars[i] == undefined) {
            bars[i] = {};
            bars[i].data = [];
            bars[i].timestamp = bars[i - 1].timestamp + interval;
            bars[i].weight = 0;
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
