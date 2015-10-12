var admin = {
    "_id": ObjectId("55d26ede5ae7b6ee8674dba9"),
    "login": "admin",
    "displayName": "admin",
    "providerInfo": {},
    "roles": [
        "ADM",
        "restx-admin"
    ]
};

var adminCredential = {
    "_id": "55d26ede5ae7b6ee8674dba9",
    // qJqVCQzQZX
    "passwordHash": "$2a$10$T/tGpl2hD2tZEmk2rDd.n.//pZ7XEixxIsIqO3ld8d.leKOwuoseG"
};

db.users.save(admin);
db.credentials.save(adminCredential);

var fairytics = {
    "_id": ObjectId("561c37c15bc128d7918857bf"),
    "login": "fairytics",
    "displayName": "fairytics",
    "providerInfo": {},
    "roles": [
        "fairytics-publisher"
    ]
};

var fairyticsCredential = {
    "_id": "561c37c15bc128d7918857bf",
    // 2ITW55L1xE
    "passwordHash": "$2a$10$pm3trniFRMeYZBATuTaiCuSJqEadirlp/.KVe7DN7IfOo0yNHKVRy"
};

db.users.save(fairytics);
db.credentials.save(fairyticsCredential);