var workerContext = new Worker('js/worker.js');

var enableTest = false;
var updating = false;

var graph = {
    nodes: {},
    links: {}
};

workerContext.addEventListener('message', function (e) {
    //console.log('Worker said: ', e.data);
    var msg = e.data;
    var updateNodes = [];
    var updateLink;
    updateNodes.push(addNode(msg.source, msg.value));
    updateNodes.push(addNode(msg.target, msg.value));
    updateLink = addLink(msg.source, msg.target, msg.value);

    while(updating) {
    }

    updating = true;

    updateNodes.forEach(function(n) {
       if(n != null)
           nodes.push(n);
    });

    if(updateLink != null)
        links.push(updateLink);

    restart(true);
    updating = false;
}, false);

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
