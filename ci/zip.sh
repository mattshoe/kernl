#!/bin/bash

OUTPUT_DIR="./build/distributions/"
echo "$PWD"

./gradlew clean

rm -rf ~/.m2
./gradlew :AutoRepo.Processor:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoRepo.Processor/build/distributions/* $OUTPUT_DIR

rm -rf ~/.m2
./gradlew :AutoRepo.Annotations:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoRepo.Annotations/build/distributions/* $OUTPUT_DIR


