#!/bin/bash

PATH=$(npm bin):$PATH
cordova build --release android
echo "Path of the android keystore ?"
read keystore
echo "Signing apk"
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore $keystore platforms/android/build/outputs/apk/android-release-unsigned.apk alias_name
echo "Zip align"
~/dev/tools/android/latest/build-tools/21.1.2/zipalign -v 4 platforms/android/build/outputs/apk/android-release-unsigned.apk ~/Desktop/Voxxrin2.apk
echo "... done"
