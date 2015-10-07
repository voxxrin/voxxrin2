var user = {
    "_id": ObjectId("55d26ede5ae7b6ee8674dba9"),
    "login": "admin",
    "displayName": "admin",
    "providerInfo": {},
    "roles": [
        "restx-admin"
    ]
};

var credential = {
    "_id": "55d26ede5ae7b6ee8674dba9",
    "passwordHash": "$2a$10$T/tGpl2hD2tZEmk2rDd.n.//pZ7XEixxIsIqO3ld8d.leKOwuoseG"
};

db.users.save(user);
db.credentials.save(credential);