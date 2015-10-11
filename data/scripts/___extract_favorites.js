db.loadServerScripts();

db.favorite.find().forEach(function (_favorite) {
    var user = db.users.findOne({_id: ObjectId(_favorite.userId)});
    var prez = resolveRef(_favorite.presentation).get();
    var event = resolveRef(prez.event).get();
    print(user._id + '-' + user.login + ' -> ' + prez.title + ' (' + event.name + ')' + ' from ' + (_favorite.source ? _favorite.source.from : 'voxxrin2'));
});