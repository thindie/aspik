@echo off
REM Start Aspik STT Server (background process)
start /B java -jar "%~dp0..\build\libs\aspik-stt-server.jar"
