#!/bin/bash

if [ "$1" == "up" ]; then
    echo "Bring containers UP..."
    docker compose up --build -d
    sleep 5
    docker compose ps
elif [ "$1" == "down" ]; then
    echo "Taking containers DOWN..."
    docker compose down
else
    echo "Usage: $0 {up|down}"
fi