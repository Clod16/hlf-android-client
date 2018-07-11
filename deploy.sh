#!/bin/bash

rm ./build/outputs/jar/hlf-android-client-debug.zip
rm -rf ./build/outputs/jar/hlf-android-client-debug
rm ./build/outputs/jar/hlf-android-client.jar
cp ./build/outputs/aar/hlf-android-client-debug.aar ./build/outputs/jar/hlf-android-client-debug.zip
unzip  -o ./build/outputs/jar/hlf-android-client-debug.zip -d ./build/outputs/jar/hlf-android-client-debug
mv  ./build/outputs/jar/hlf-android-client-debug/classes.jar  ./build/outputs/jar/hlf-android-client.jar
cp  ./build/outputs/jar/hlf-android-client.jar ~/Documents/Sviluppo/workspace_dcot/MyApplication/app/libs/hlf-android-client.jar