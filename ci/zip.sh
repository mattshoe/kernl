#!/bin/bash

OUTPUT_DIR="./build/distributions/"
echo "$PWD"

./gradlew clean --no-daemon

./gradlew test --no-daemon

rm -rf ~/.m2
./gradlew :AutoRepo.Runtime:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoRepo.Runtime/build/distributions/* $OUTPUT_DIR

rm -rf ~/.m2
./gradlew :AutoRepo.Processor:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoRepo.Processor/build/distributions/* $OUTPUT_DIR

rm -rf ~/.m2
./gradlew :AutoRepo.Annotations:generateZip --no-daemon
mkdir -p $OUTPUT_DIR
cp ./AutoRepo.Annotations/build/distributions/* $OUTPUT_DIR


