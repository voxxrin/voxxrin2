#!/bin/bash

PID=$(adb shell "ps | grep ionic" | awk '{ print $2 }')
echo "Launch ADB concerning pid = $PID" 
adb logcat | grep $PID
