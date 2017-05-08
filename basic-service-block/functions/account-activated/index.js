exports.handler = (event, context, callback) => {

    var result = store(function(db) {
        return db.collection('inserts').insertOne(event);
    });

    var events = event.eventLog;
    var account = event.account;

    var lastEvent;

    if(events.length > 0) {
        lastEvent = events[0];
    }

    if((lastEvent != null || lastEvent != undefined) ? lastEvent.type != "ACCOUNT_ACTIVATED" : true) {
        account.status = "ACCOUNT_ACTIVATED";
        callback(null, account);
    } else {
        var error = new Error("Account already activated");
        callback(error);
    }
};


function store(transaction) {
    // Fetch connection from environment
    var url = JSON.parse(process.env.SERVICE_CREDENTIALS).uri;

    // Create mongo client
    var mongoClient = require('mongodb').MongoClient, assert = require('assert');

    // Create transaction context
    var apply = function(transaction) {
        var response = {};

        // Use connect method to connect to the server
        mongoClient.connect(url, function (err, db) {
            assert.equal(null, err);
            console.log("Connected successfully to server");

            // Apply transaction
            response = transaction(mongoClient);
            db.close();
        });

        return response;
    };

    return apply(transaction);
}