@echo off
REM Aspik STT Server - Setup Auto-Start via Task Scheduler
REM Run this script as Administrator to configure auto-start on login

setlocal

REM Get the absolute path to the JAR file
for %%I in ("%~dp0..\build\libs\aspik-stt-server.jar") do set "JAR_PATH=%%~fI"

if not exist "%JAR_PATH%" (
    echo Error: JAR file not found at %JAR_PATH%
    pause
    exit /b 1
)

REM Create scheduled task that runs on user login
schtasks /create /tn "Aspik STT Server" /tr "java -jar \"%JAR_PATH%\"" /sc onlogon /rl highest /f

if %ERRORLEVEL% EQU 0 (
    echo Auto-start configured successfully!
    echo JAR location: %JAR_PATH%
    echo Task name: Aspik STT Server
) else (
    echo Failed to create scheduled task. Try running as Administrator.
    pause
)

endlocal
