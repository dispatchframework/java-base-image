#!/bin/sh
set -e -x

cd $(dirname $0)

docker build -t dispatchframework/java8-base:0.0.2 .
