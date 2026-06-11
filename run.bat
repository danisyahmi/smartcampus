@echo off

if "%~1"=="up" (
    echo Bring containers UP...
    docker compose up --build -d
    
    REM Sleep for 5 seconds
    timeout /t 5 /nobreak >nul
    
    docker compose ps
) else if "%~1"=="down" (
    echo Taking containers DOWN...
    docker compose down
) else (
    echo Usage: %~nx0 {up^|down}
)