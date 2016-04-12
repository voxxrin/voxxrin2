#!/bin/bash

echo "Avez-vous verifié la configuration backend sur le frontend ?"
read yes

PATH=$(npm bin):$PATH
cordova build --release ios 
echo "... done"
