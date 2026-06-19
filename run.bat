@echo off

if "%~1"=="up" (
    echo Bring containers UP...
    docker compose up -d
) else if "%~1"=="down" (
    echo Taking containers DOWN...
    docker compose down
) else (
    echo Usage: %~nx0 {up^|down}
)