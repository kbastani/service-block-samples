'use strict'

var mongoClient = require('mongodb').MongoClient;
var md5 = require('md5');
var Sync = require('sync');

// This must be unique to each materialized view
var viewName = "tcq";

// The match threshold for generating a new tight coupling event
var matchThreshold = 2;

let mongoUri;
let cachedDb = null;

exports.handler = (event, context, callback) => {

    // Enables reuse of cached database connection
    context.callbackWaitsForEmptyEventLoop = false;

    // Process the event with Cloud Foundry service connections
    withServices(function (db, err) {
        if (err != null) {
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

    // Get the project details
    var project = event.project;
    var commit = event.projectEvent.payload.commit;

    // Get the files for this commit
    var files = commit.files.map(function (item) {
        return item.fileName.toLowerCase();
    });

    var col = db.collection('query');

    function updateViewForSet(fileNames, complete) {
        // Generate a unique hash of the composite filename key
        // [VIEW_NAME]_[PROJECT_ID]_[K_COMBINATION_HASH]
        var compositeKey = [viewName, project.projectId, md5(fileNames.sort().join("_"))].join("_");
        var tightCouplingEvent = false;

        col.findOneAndUpdate({_id: compositeKey},
            {
                $set: {
                    model: {
                        projectId: project.projectId,
                        matches: 1,
                        captures: 0,
                        fileIds: fileNames,
                        createdAt: new Date(),
                        updatedAt: new Date()
                    },
                    viewName: viewName
                }
            }, {
                new: false,
                upsert: true,
                returnOriginal: true
            }, function (err, r) {
                if (!err) {
                    if (r.value != null) {
                        var tcq = r.value;
                        tcq.model.matches = tcq.model.matches + 1;

                        // Check if matches exceeds threshold
                        if (tcq.model.matches >= matchThreshold) {
                            // Reset counter and fire a new tight coupling event
                            tcq.model.matches = 0;
                            tcq.model.captures = tcq.model.captures + 1;

                            tightCouplingEvent = true;
                        }

                        col.findOneAndUpdate({_id: compositeKey}, {
                            $set: {
                                model: tcq.model,
                                updatedAt: new Date()
                            }
                        }, {returnOriginal: false, upsert: false}, function (err, r) {
                            complete(null, !err ? r.value : err);
                        });
                    } else {
                        col.find({_id: compositeKey}).limit(1).next(function (err, doc) {
                            complete(null, !err ? doc : err);
                        });
                    }
                } else {
                    complete(null, err);
                }
            });
    }

    Sync(function () {
        var task;
        // Synchronously update the view using the event payload
        updateViewForSet(files, task = new Sync.Future());
        callback(null, task.result);
    });
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