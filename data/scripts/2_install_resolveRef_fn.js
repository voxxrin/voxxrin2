var resolveRef = function (reference) {

    if (!reference) {
        return {
            get: function () {
                return null;
            },
            throwErrorIfNotFound: function () {
            }
        };
    }

    var refRegexp = /ref:\/\/([a-z]+)\/(.*)/;

    if (!reference.match(refRegexp)) {
        print('ERROR >>> MALFORMED URI INTO REFERENCE : ' + reference);
    }
    var computed = refRegexp.exec(reference);
    var entity = db[computed[1]].findOne({_id: ObjectId(computed[2])});

    var result = {
        get: function () {
            return entity;
        },
        throwErrorIfNotFound: function () {
            if (!entity) {
                print('ERROR >>> REFERENCED ENTITY NOT FOUND : ' + reference);
            }
            return result;
        }
    };

    return result;
};

db.system.js.save({
    _id: 'resolveRef',
    value: resolveRef
});