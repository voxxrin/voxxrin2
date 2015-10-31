#!/bin/bash

echo "Avez-vous verifié la configuration backend sur le frontend ?"
read yes
mvn clean install -DskipTests
scp srv/target/srv-1.0-SNAPSHOT.war root@voxxrin:~/deploys/voxxrin2-1.0-SNAPSHOT.war
ssh root@voxxrin '/root/deploy.sh'
