db.loadServerScripts();

db.remindMe.find().forEach(function (_remindme) {
    var user = db.users.findOne({_id: ObjectId(_remindme.userId)});
    var prez = resolveRef(_remindme.presentation).get();
    var event = resolveRef(prez.event).get();
    print(user.login + ' -> ' + prez.title + ' (' + event.name + ')');
});