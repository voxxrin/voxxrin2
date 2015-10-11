///////////////////////////
/* Configure this area */
///////////////////////////
var crawlId = '-';

///////////////////////////

var collectionsToClean = ['presentation', 'day', 'event', 'room', 'speaker'];

for (var i = 0; i < collectionsToClean.length; i++) {

    var collectionName = collectionsToClean[i];
    print('deleting ' + db[collectionName].count({crawlId: crawlId}) + ' objects from collection ' + collectionName);
    db[collectionName].remove({crawlId: crawlId});

}