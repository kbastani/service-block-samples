'use strict'

var mongoClient = require('mongodb').MongoClient;
var md5 = require('md5');
var Sync = require('sync');

// This must be unique to each materialized view
var viewName = "TCQ";

// The match threshold for generating a new tight coupling event
var matchThreshold = 2;

let mongoUri;
let cachedDb = null;

exports.handler = (event, context, callback) => {

    // Enables reuse of cached database connection
    context.callbackWaitsForEmptyEventLoop = false;

    // Process the event with Cloud Foundry service connections
    withServices(function (db, err) {
        if(err != null) {
            callback(null, err);
        } else {
            processEvent(event, context, callback, db);
        }
    });
};

/**
 * The event handler implementation for processing a Lambda event invocation from Spring Boot.
 *
 * @param event is the event payload
 * @param context is the AWS Lambda context object
 * @param callback is the function that provides a response back to Spring Boot
 * @param db is the MongoDB service provided from Cloud Foundry
 */
function processEvent(event, context, callback, db) {

    var events = event.eventLog;
    var project = event.project;
    var col = db.collection('query');

    // Apply the event to the project
    var applyProjectEvent = function () {
        var lastEvent;

        if (events.length > 0) {
            lastEvent = events[0];
        }

        callback(null, project);
    };

    function updateViewForSet(fileNames, complete) {

        // Generate a unique hash of the composite filename key
        // [VIEW_NAME]_[PROJECT_ID]_[K_COMBINATION_HASH]
        var compositeKey = [viewName, project.projectId, md5(fileNames.sort().concat("_"))].join("_");
        var tightCouplingEvent = false;

        col.findAndModify({
            query: compositeKey,
            update: {
                $setOnInsert: {
                    model: {
                        projectId: project.projectId,
                        matches: 0,
                        captures: 0,
                        fileIds: fileNames,
                        createdAt: new Date(),
                        updatedAt: new Date()
                    }
                }
            },
            new: true,
            upsert: true
        }, function (err, r) {
            if (!err) {
                var tcq = r.result;
                tcq.matches = tcq.matches + 1;

                // Check if matches exceeds threshold
                if (tcq.matches >= matchThreshold) {
                    // Reset counter and fire a new tight coupling event
                    tcq.matches = 0;
                    tcq.captures = tcq.captures + 1;

                    tightCouplingEvent = true;
                }

                col.updateOne(compositeKey, {$set: tcq}, function (err, r) {
                    if (!err) {
                        complete(null, r.result);
                    } else {
                        complete(null, err);
                    }
                });
            } else {
                complete(null, err);
            }
        });

        Sync(function () {
            var tce = updateViewForSet.future(null, ["s.java", "a.java"]);
            console.log(tce);
            applyProjectEvent();
        });
    }

    // updateViewForSet(event.payload.files.map(function(item) {
    //     return item.fileName;
    // }));
}

/**
 * Wrapper function for providing Cloud Foundry data service context to an AWS Lambda function.
 */
function withServices(callback) {
    initializeDataSource(callback);
}

/**
 * Initializes and caches the MongoDB connection provided by Cloud Foundry.
 */
function initializeDataSource(callback) {
    if (mongoUri == null) {
        // Fetch credentials from environment
        mongoUri = JSON.parse(process.env.SERVICE_CREDENTIALS).uri;

        // Cache database connection
        mongoClient.connect(mongoUri, function (err, db) {
            if (err == null) {
                console.log("Connected successfully to MongoDB server");
                cachedDb = db;
                callback(cachedDb);
            } else {
                console.log("Could not connect to MongoDB server: " + err);
                callback(null, err);
            }
        });
    } else {
        callback(cachedDb);
    }
}