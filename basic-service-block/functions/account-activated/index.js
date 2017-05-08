'use strict'

var mongoClient = require('mongodb').MongoClient;

let mongoUri;
let cachedDb = null;

exports.handler = (event, context, callback) => {

    // Test Mongo storage connection
    store(function (db) {
        console.log(db.collection('inserts').insertOne(event));
    });

    var events = event.eventLog;
    var account = event.account;

    var lastEvent;

    if (events.length > 0) {
        lastEvent = events[0];
    }

    if ((lastEvent != null || lastEvent != undefined) ? lastEvent.type != "ACCOUNT_ACTIVATED" : true) {
        account.status = "ACCOUNT_ACTIVATED";
        callback(null, account);
    } else {
        var error = new Error("Account already activated");
        callback(error);
    }
};


function store(query) {
    if (mongoUri != null) {
        query(cachedDb);
    } else {
        // Fetch credentials from environment
        mongoUri = JSON.parse(process.env.SERVICE_CREDENTIALS).uri;

        // Cache database connection
        mongoClient.connect(url, function (err, db) {
            assert.equal(null, err);
            console.log("Connected successfully to server");
            cachedDb = db;
            query(cachedDb);
        });
    }
}