'use strict'

var mongoClient = require('mongodb').MongoClient;

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

    // Apply the event to the project
    var applyProjectEvent = function () {
        var events = event.eventLog;
        var project = event.project;

        var lastEvent;

        if (events.length > 0) {
            lastEvent = events[0];
        }

        callback(null, project);
    };

    // Tests writing to MongoDB from Lambda
    db.collection('inserts').insertOne(event, function (err, r) {
        if (err == null) {
            console.log(r);
            applyProjectEvent();
        } else {
            console.error(err);
            callback(null, err);
        }
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