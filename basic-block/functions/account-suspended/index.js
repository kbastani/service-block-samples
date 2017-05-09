exports.handler = (event, context, callback) => {
    var events = event.eventLog;
    var account = event.account;

    var lastEvent;

    if(events.length > 0) {
        lastEvent = events[0];
    }

    if((lastEvent != null || lastEvent != undefined) ? lastEvent.type != "ACCOUNT_SUSPENDED" : true) {
        account.status = "ACCOUNT_SUSPENDED";
        callback(null, account);
    } else {
        var error = new Error("Account already suspended");
        callback(error);
    }
};