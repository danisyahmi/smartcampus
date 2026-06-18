#!/bin/bash

if [ "$1" == "up" ]; then
    echo "Bring containers UP..."
    docker rm -f rabbitmq-server && docker compose up -d
elif [ "$1" == "down" ]; then
    echo "Taking containers DOWN..."
    docker compose down
else
    echo "Usage: $0 {up|down}"
fi