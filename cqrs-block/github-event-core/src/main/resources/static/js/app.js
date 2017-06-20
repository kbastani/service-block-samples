var width = 500,
    height = 500,
    radius = 2;

var center = d3.forceCenter(width / 2, height / 2);
var minX = width / 2, minY = height / 2, maxX = width / 2, maxY = height / 2;
var maxGroup = 1, minGroup = 0;
var maxStrength = 1, minStrength = 0;

var voronoi = d3.voronoi()
    .x(scaleX)
    .y(scaleY)
    .extent([[-1, -1],
        [width + 1, height + 1]
    ]);

var colorRange = d3.scaleLinear()
    .domain([minGroup, maxGroup])
    .interpolate(d3.interpolateHsl)
    .range(["#2ecc71", "#e74c3c"]);

function setNodeBounds(d) {
    maxX = d.x > maxX ? d.x : maxX;
    minX = d.x < minX ? d.x : minX;
    maxY = d.y > maxY ? d.y : maxY;
    minY = d.y < minY ? d.y : minY;
    minGroup = d.group < minGroup ? d.group : minGroup;
    maxGroup = d.group > maxGroup ? d.group : maxGroup;

    colorRange = d3.scaleLinear()
        .domain([minGroup, maxGroup])
        .interpolate(d3.interpolateHsl)
        .range(["#2ecc71", "#e74c3c"]);
}

function setLinkBounds(d) {
    minStrength = d.strength < minStrength ? d.strength : minStrength;
    maxStrength = d.strength > maxStrength ? d.strength : maxStrength;
}

function calculateBounds() {
    // Calculate the scale functions for the SVG boundary
    minX = width / 2;
    minY = height / 2;
    maxX = width / 2;
    maxY = height / 2;
    maxGroup = 0;
    minGroup = 1;
    maxStrength = 0;
    minStrength = 1;
    nodes.forEach(setNodeBounds);
    links.forEach(setLinkBounds)
}


function scaleStrength(d) {
    return d3.scaleLinear()
        .domain([minStrength, maxStrength])
        .interpolate(d3.interpolateNumber)
        .range([0, 1.0])(d.strength);
}

function scaleGroup(d) {
    return d3.scaleLinear()
        .domain([minGroup, maxGroup])
        .interpolate(d3.interpolateNumber)
        .range([0, 1.0])(d.group);
}

function scaleX(d) {
    var x = d.x != null ? d.x : d;
    x = d3.scaleLinear()
        .domain([minX, maxX])
        .interpolate(d3.interpolateNumber)
        .range([0, width])(x);
    return Math.min(Math.max(x, d.group), width - d.group)
}

function scaleY(d) {
    var y = d.y != null ? d.y : d;
    y = d3.scaleLinear()
        .domain([minY, maxY])
        .interpolate(d3.interpolateNumber)
        .range([0, height])(y);
    return Math.min(Math.max(y, d.group), height - d.group);
}

// var nodes = [{id: 0, reflexive: false}, {id: 1, reflexive: false}],
//     links = [{source: nodes[0], target: nodes[1], left: false, right: true}];

var nodes = [],
    links = [];

var svg = d3.select('body')
    .append('svg')
    .attr('width', width)
    .attr('height', height);

//    .alphaMin(.01).alpha(.3).alphaTarget(.3)
var simulation = function () {
    calculateBounds();
    var forceSimulation = d3.forceSimulation()
        .force("center", center)
        .force("collide", d3.forceCollide(function (d) {
            return Math.max(scaleGroup(d) * width / 2, 25);
        }).strength(function (d) {
            return Math.min(1.0 - scaleGroup(d), 1.0);
        }))
        .force("body", d3.forceManyBody())
        .force("x", d3.forceX(20))
        .force("y", d3.forceY(20))
        .force("link", d3.forceLink()
            .id(function (d) {
                return d.id;
            }).strength(function (d) {
                return Math.min(1.0 - scaleStrength(d), 1.0);
            }).distance(function (d) {
                return Math.max((1.0 - scaleStrength(d)) * (width / 3), 40);
            }))
        .nodes(nodes)
        .on("tick", ticked);

    forceSimulation.force("link").links(links);

    return forceSimulation;
};

svg.append('svg:defs').append('svg:marker')
    .attr('id', 'end-arrow')
    .attr('viewBox', '0 -5 10 10')
    .attr('refX', 6)
    .attr('markerWidth', 3)
    .attr('markerHeight', 3)
    .attr('orient', 'auto')
    .append('svg:path')
    .attr('d', 'M0,-5L10,0L0,5')
    .attr('fill', '#000');

svg.append('svg:defs').append('svg:marker')
    .attr('id', 'start-arrow')
    .attr('viewBox', '0 -5 10 10')
    .attr('refX', 4)
    .attr('markerWidth', 3)
    .attr('markerHeight', 3)
    .attr('orient', 'auto')
    .append('svg:path')
    .attr('d', 'M10,-5L0,0L10,5')
    .attr('fill', '#000');

var cell = svg.append('svg:g').attr('class', 'cells').selectAll('path'),
    link = svg.append('svg:g').attr("class", "links").selectAll('path'),
    node = svg.append('svg:g').attr("class", "nodes").selectAll('circle');

function redrawPolygon(polygon) {
    polygon.attr("d", function (d) {
        return d ? "M" + d.join("L") + "Z" : null;
    });
}

function ticked() {
    calculateBounds();

    svg.select(".links").selectAll("path").attr('d', function (d) {
        return 'M' + scaleX(d.source) + ',' + scaleY(d.source) + 'L' + scaleX(d.target) + ',' + scaleY(d.target);
    });

    svg.select(".nodes").selectAll("circle")
        .attr("cx", function (d) {
            d[0] = d.x;
            return scaleX(d);
        })
        .attr("cy", function (d) {
            d[1] = d.y;
            return scaleY(d);
        })
        .attr("fill", function (d) {
            return d != null ? colorRange(d.group) : "black";
        });

    cell = cell.exit().remove();

    var diagram = voronoi(nodes);

    svg.select('.cells').selectAll('path')
        .data(diagram.polygons())
        .enter()
        .append("path")
        .attr("fill", function (d) {
            return d != null ? colorRange(graph.nodes[d.data.id].group) : "black";
        })
        .attr('class', 'polygon')
        .call(redrawPolygon);

    svg.select('.cells').selectAll('path')
        .data(diagram.polygons())
        .attr("fill", function (d) {
            return d != null ? colorRange(graph.nodes[d.data.id].group) : "black";
        })
        .call(redrawPolygon);
}

var sim = simulation();

function restart(updateSim) {
    sim.stop();
    calculateBounds();

    // Update links
    link = link.data(links).exit().remove();

    svg.select(".links").selectAll("path")
        .data(links)
        .enter()
        .append("path")
        .attr('class', 'link');

    // Update nodes
    node = node.data(nodes).exit().remove();

    svg.select(".nodes").selectAll("circle")
        .data(nodes)
        .enter()
        .append('circle')
        .attr('class', 'node')
        .attr('r', radius)
        .attr('cx', function (d) {
            d[0] = d.x;
            return scaleX(d)
        })
        .attr('cy', function (d) {
            d[1] = d.y;
            return scaleY(d)
        })
        .attr('fill', function (d) {
            return colorRange(d.group);
        })
        .classed('reflexive', function (d) {
            return d.reflexive;
        });

    if (updateSim)
        sim = simulation();
}

restart(false);
