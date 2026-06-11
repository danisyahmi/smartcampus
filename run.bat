@echo off
set /p choice="Do you want to start (up) or stop (down) containers? (up/down): "

if "%choice%"=="up" (
    echo Starting Docker Compose...
    docker compose up -d
) else if "%choice%"=="down" (
    echo Stopping Docker Compose...
    docker compose down
) else (
    echo Invalid choice. Exiting.
)
pause