/****************/
/* Configure this area
/****************/

var doUpdate = false;
var eventCode = 'bdxio15';
var eventId = '56150cc6c2e680e3e0f6a888';

/****************/
load('/Users/eoriou/dev/wkspace_perso/voxxrin2/data/exports/voxxrin1/favorites_11_Oct.js');

var each = function (array, fn) {
    for (var i = 0; i < array.length; i++) {
        fn(array[i]);
    }
};

var usersAddedCount = 0;
var favoritesAddedCount = 0;
var existingFavoritesCount = 0;

each(voxxrin1Favorites, function (favorite) {

    var user = db.users.findOne({ twitterId: favorite.twitterId });
    if (!user) {
        user = {
            "login" : favorite.twitterId,
            "twitterId" : favorite.twitterId,
            "providerInfo" : {},
            "roles" : [],
            "_id" : ObjectId(),
            "source": { "type": "export", "from": "voxxrin1" }
        };
        print('adding new user');
        printjsononeline(user);
        usersAddedCount++;
        if (doUpdate === true) {
            db.users.save(user);
        }
    }
    
    each (favorite.eventFavs, function (eventFav) {
        if (eventFav.eventId === eventCode) {            
            each (eventFav.favs, function (fav) {
                var eventRef = "ref://event/" + eventId;
                var presentation = db.presentation.findOne({ externalId: fav, event: eventRef });
                if (presentation) {
                    var presentationRef = "ref://presentation/" + presentation._id.str;
                    var existingFavorite = db.favorite.findOne({ presentation: presentationRef, userId: user._id.str });
                    if (!existingFavorite) {
                        var newFavorite = {
                            "dateTime" : ISODate(),
                            "presentation" : presentationRef,
                            "userId" : user._id.str,
                            "source": { "type": "export", "from": "voxxrin1" }
                        };
                        print('adding new favorite');
                        printjsononeline(newFavorite);
                        favoritesAddedCount++;
                        if (doUpdate === true) {
                            db.favorite.save(newFavorite);
                        }
                    } else {
                        print('Existing favorite');
                        printjson(existingFavorite);
                        existingFavoritesCount++;
                    }
                }
            });
        }
    });
});

print(usersAddedCount + ' users imported');
print(favoritesAddedCount + ' favorites imported');
print(existingFavoritesCount + ' existing favorites');