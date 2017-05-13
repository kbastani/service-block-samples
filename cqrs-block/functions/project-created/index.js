'use strict'

exports.handler = (event, context, callback) => {
    processEvent(event, context, callback, db);
};

function processEvent(event, context, callback, db) {

    // Apply the event to the project
    var applyProjectEvent = function () {
        var events = event.eventLog;
        var project = event.project;

        var lastEvent;

        if (events.length > 0) {
            lastEvent = events[0];
        }

        if ((lastEvent != null || lastEvent != undefined) ? lastEvent.type != "PROJECT_CREATED" : true) {
            project.status = "PROJECT_CREATED";
            callback(null, project);
        } else {
            var error = new Error("Project already created");
            callback(error);
        }
    };

    applyProjectEvent();
}