#!/bin/bash

LOGS=~/video-manager/server-logs

echo "Setting up Video Manager Server environment"

if [[ ! -d $LOGS ]]; then
    mkdir -p $LOGS
fi
