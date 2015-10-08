db.loadServerScripts();

db.favorite.find().forEach(function (_favorite) {
    var user = db.users.findOne({_id: ObjectId(_favorite.userId)});
    var prez = resolveRef(_favorite.presentation).get();
    print(user.login + ' -> ' + prez.title);
});