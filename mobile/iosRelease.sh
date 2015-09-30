#!/bin/bash

PATH=$(npm bin):$PATH
cordova build --release ios 
echo "... done"
