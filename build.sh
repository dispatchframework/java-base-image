#!/bin/sh
set -e -x

cd $(dirname $0)

docker build -t dispatchframework/java-base:0.0.4 .
