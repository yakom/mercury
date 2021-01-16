#!/usr/bin/env sh

# the script is to be run from the project's root.

docker run \
    -it --rm \
    -e UID=`id -u` \
    -e GID=`id -g` \
    -v `pwd`:/tmp/mercury-src \
    --log-driver none \
    maven:3-openjdk-11 \
    sh -c 'cd /tmp/mercury-src; mvn install; chown -R $UID:$GID target'
