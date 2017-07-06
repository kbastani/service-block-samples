var bound = 10,
    seriesHeight = 150;

var gridSize = 27;

var minX = width / 2, minY = (height - seriesHeight) / 2, maxX = width / 2, maxY = (height - seriesHeight) / 2;
var maxGroup = 1, minGroup = 0;
var maxStrength = 1, minStrength = 0;
var maxBar = 0, minBar = -1;
var gridCells = [];

var gridX = d3.scaleLinear()
    .domain([0, gridSize / 2])
    .interpolate(d3.interpolateNumber)
    .range([-(width / (gridSize)), width]);

var graphToGridX = d3.scaleLinear()
    .domain([0, width])
    .interpolate(d3.interpolateNumber)
    .range([0, gridSize]);

var gridY = d3.scaleLinear()
    .domain([0, gridSize])
    .interpolate(d3.interpolateNumber)
    .range([-(((height - seriesHeight) / gridSize) * 2), (height - seriesHeight) + (((height - seriesHeight) / gridSize) * 2)]);

var voronoi = d3.voronoi()
    .x(function (d) {
        return d[0];
    })
    .y(function (d) {
        return d[1];
    })
    .extent([[-1, -1],
        [width + 1, height - seriesHeight]
    ]);

var colorRange = d3.scaleLog()
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
    minY = (height - seriesHeight) / 2;
    maxX = width / 2;
    maxY = (height - seriesHeight) / 2;
    maxGroup = 0;
    minGroup = 1;
    maxStrength = 0;
    minStrength = 1;
    nodes.forEach(setNodeBounds);
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
        .range([bound, width - bound])(x);
    return Math.min(Math.max(x, d.group), width - d.group)
}

function scaleY(d) {
    var y = d.y != null ? d.y : d;
    y = d3.scaleLinear()
        .domain([minY, maxY])
        .interpolate(d3.interpolateNumber)
        .range([bound, (height - seriesHeight) - bound])(y);
    return Math.min(Math.max(y, d.group), height - d.group);
}

var svg = d3.select('body')
    .append('svg')
    .attr('width', width)
    .attr('height', height + 50);

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

var graphBox = svg.append('g');

graphBox.append('line')
    .attr('transform', 'translate(0,' + (seriesHeight) + ')')
    .attr('x1', 0)
    .attr('y1', height - seriesHeight)
    .attr('x2', width)
    .attr('y2', height - seriesHeight)
    .attr('fill', 'black')
    .attr('stroke', 'black')
    .attr('stroke-width', '1px')
    .attr('class', 'bars-frame');

graphBox.append('rect')
    .attr('transform', 'translate(0,' + (0) + ')')
    .attr('x', 0)
    .attr('y', height - seriesHeight)
    .attr('width', width)
    .attr('height', seriesHeight)
    .attr('fill', '#ebf5fb')
    .attr('stroke', 'black')
    .attr('stroke-width', '1px')
    .attr('class', 'bars-frame');

var cell = svg.append('svg:g').attr('class', 'cells').selectAll('path');

function redrawPolygon(polygon) {
    polygon.attr("d", function (d) {
        return d ? "M" + d.join("L") + "Z" : null;
    });
}

function ticked() {
    calculateBounds();
    cell = cell.exit().remove();

    var diagram = voronoi(gridCells);

    svg.select('.cells').selectAll('path')
        .data(diagram.polygons())
        .enter()
        .append("path")
        .attr("fill", function (d) {
            return d == null ? "#5d6d7e" : d.data.color;
        })
        .attr('class', 'polygon')
        .call(redrawPolygon);

    svg.select('.cells').selectAll('path')
        .data(diagram.polygons())
        .transition()
        .duration(200)
        .attr("fill", function (d) {
            return d == null ? "#5d6d7e" : d.data.color;
        })
        .call(redrawPolygon);
}


var xAxis = svg.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height + ")");

var lineX;
var axis;

for (var i = 0; i < gridSize / 2; i++) {
    for (var j = 0; j < gridSize; j++) {
        var newCell = {};
        newCell.id = i + "_" + j;
        newCell[0] = gridX(i) + ((j % 2 == 0 ? ((width / (gridSize / 2)) / 2) : 0));
        newCell[1] = gridY(j) + (((height - seriesHeight) / gridY(1)) / 4);
        gridCells.push(newCell);
    }
}

function paintGrid() {
    calculateBars();
    var eventLog = [];
    var tempGrid = {};
    var tempGridMax = 0;

    for (var i = 0; i < width; i++) {
        // Get grid column
        var gridCol = Math.floor(graphToGridX(i));

        // Get y-axis
        var ySlice = Math.floor(d3.scaleLinear()
            .domain([0, width])
            .interpolate(d3.interpolateNumber)
            .range([0, bars.length])(i));

        if (eventLog.indexOf(ySlice) < 0) {

            // Color range
            var yVal = Math.floor(d3.scaleLinear()
                .domain([minBar, maxBar])
                .interpolate(d3.interpolateNumber)
                .range([0, gridSize])(bars[ySlice] == null ? 0 : bars[ySlice].data.length));

            for (var j = yVal - 1; j >= 0; j--) {
                var key = gridCol + "_" + (gridSize - j);
                tempGrid[key] = tempGrid[key] == null ? 0 : tempGrid[key];
                tempGrid[key] += (bars[ySlice] != null ? bars[ySlice].weight : 0);
                //tempGrid[key] = 5;
                tempGridMax = (tempGrid[key] > tempGridMax) ? tempGrid[key] : tempGridMax;
            }

            //console.log(tempGrid);
            eventLog.push(ySlice);
        }
    }

    // Re-paint grid
    gridCells.forEach(function (d) {
        d.weight = tempGrid[d.id] == null ? 0 : tempGrid[d.id];

        d.color = d3.scaleTime()
            .domain([0, tempGridMax])
            .interpolate(d3.interpolateHsl)
            .range(["#2ecc71", "#e74c3c"])(d.weight);
    });

    ticked();
}

var lineFunction = d3.area().curve(d3.curveStepBefore)
    .x(function (d, i) {
        return d3.scaleLinear()
            .domain([0, bars.length])
            .interpolate(d3.interpolateNumber)
            .range([0, width])(i);
    })
    .y1(scaleBars)
    .y0(height);

var bar = svg.append('svg:g').attr('class', 'bars')
    .append("path")
    .datum(bars)
    .attr("d", lineFunction)
    .attr("stroke", "#2e86c1")
    .attr("stroke-width", 1)
    .attr("fill", "gray");

function setBarBounds(bar) {
    minBar = minBar == -1 ? bar.data.length : (bar.data.length < minBar ? bar.data.length : minBar);
    maxBar = bar.data.length > maxBar ? bar.data.length : maxBar;
}

function calculateBars() {
    minBar = -1;
    bars.forEach(setBarBounds);
}

function scaleBars(d) {
    return d3.scaleLinear()
        .domain([0, maxBar])
        .interpolate(d3.interpolateNumber)
        .range([height, height - seriesHeight])(d.data.length);
}


function drawGraph() {
    calculateBars();

    bar.datum(bars)
        .transition()
        .duration(250)
        .attr("fill", "#5d6d7e")
        .attr("stroke", "#154360")
        .attr("d", lineFunction);
}

function restart() {
    calculateBounds();
    if (events.length > 1) {
        interval = (events[events.length - 1].timestamp - events[0].timestamp) / 150;
        bars = [];
        events.forEach(updateSeries);
        axis = d3.axisBottom(lineX);
        xAxis = xAxis.transition().duration(250).call(axis);
        drawGraph();
        paintGrid();
    }
}
