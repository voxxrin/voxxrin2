#!/bin/bash

mvn clean install -DskipTests
scp crawlers/target/crawlers-1.0-SNAPSHOT.war root@voxxrin:/root/deploys/
ssh root@voxxrin '/root/deploy_crawlers.sh'

