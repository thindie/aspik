# Aspik STT Server - Setup Auto-Start Script
# Run as Administrator to register in Task Scheduler

$jarPath = Join-Path $PSScriptRoot "..\build\libs\aspik-stt-server.jar"
$javaExe = "java"

if (-not (Test-Path $jarPath)) {
    Write-Host "Error: JAR file not found at $jarPath" -ForegroundColor Red
    exit 1
}

# Create scheduled task for auto-start on login
$action = New-ScheduledTaskAction -Execute $javaExe -Argument "-jar `"$jarPath`""
$trigger = New-ScheduledTaskTrigger -AtLogOn -User $env:USERNAME
$settings = New-ScheduledTaskSettingsSet -StartWhenAvailable -DontStopIfGoingOnBatteries

Register-ScheduledTask -TaskName "Aspik STT Server" `
    -Action $action `
    -Trigger $trigger `
    -Settings $settings `
    -Description "Speech-to-text relay server for Aspik" `
    -Force

Write-Host "Auto-start configured successfully!" -ForegroundColor Green
Write-Host "JAR location: $jarPath"
Write-Host "Task name: Aspik STT Server"
