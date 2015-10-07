#!/bin/sh

APP_OPTS="-Doauth.twitter.apiSecret=_ -Doauth.twitter.apiKey=_ -Dws.ionic.app.privateKey=_ -Dws.ionic.app.id=_ -Doauth.secrets.token=_"

java -cp "target/restx/classes:target/dependency/*" -Drestx.app.package=voxxrin2 -Drestx.mode=prod $APP_OPTS $VM_OPTIONS voxxrin2.AppServer
