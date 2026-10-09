<# 
.SYNOPSIS
    Gradle wrapper script for PowerShell
.DESCRIPTION
    This script runs the Gradle wrapper using PowerShell, properly handling arguments and Java detection.
#>

# Require PowerShell 5.1+
# Set strict mode for better error handling
Set-StrictMode -Version Latest

# Get the directory where this script is located
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$appHome = $scriptDir

# Gradle wrapper JAR location
$wrapperJar = Join-Path $appHome "gradle\wrapper\gradle-wrapper.jar"

if (-not (Test-Path $wrapperJar)) {
    Write-Error "Gradle wrapper JAR not found at: $wrapperJar"
    exit 1
}

# Determine Java executable
$javaExe = $null

if ($env:JAVA_HOME) {
    $javaHome = $env:JAVA_HOME.Trim('"')
    $javaExeCandidate = Join-Path $javaHome "bin\java.exe"
    if (Test-Path $javaExeCandidate) {
        $javaExe = $javaExeCandidate
    } else {
        Write-Warning "JAVA_HOME is set to '$javaHome' but java.exe not found there."
    }
}

if (-not $javaExe) {
    # Try to find java in PATH
    $javaExe = (Get-Command java.exe -ErrorAction SilentlyContinue).Source
    if (-not $javaExe) {
        # Check common installation locations
        $commonPaths = @(
            "C:\Program Files\Java\*\bin\java.exe",
            "C:\Program Files (x86)\Java\*\bin\java.exe",
            "$env:USERPROFILE\.jdks\*\bin\java.exe",
            "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\*\bin\java.exe"
        )
        foreach ($pattern in $commonPaths) {
            $found = Get-Item $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
            if ($found) {
                $javaExe = $found.FullName
                break
            }
        }
    }
}

if (-not $javaExe) {
    Write-Error "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    Write-Error "Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    exit 1
}

# Default JVM options
$defaultJvmOpts = @("-Xmx64m", "-Xms64m")

# Collect JVM options from environment variables
$jvmOpts = @()
$jvmOpts += $defaultJvmOpts

if ($env:JAVA_OPTS) {
    $jvmOpts += $env:JAVA_OPTS -split '\s+'
}
if ($env:GRADLE_OPTS) {
    $jvmOpts += $env:GRADLE_OPTS -split '\s+'
}

# App name for Gradle
$appBaseName = "gradlew"

# Build the argument list for java
$javaArgs = @()
$javaArgs += $jvmOpts
$javaArgs += "-Dorg.gradle.appname=$appBaseName"
$javaArgs += "-jar"
$javaArgs += "`"$wrapperJar`""
$javaArgs += $args

# Execute Gradle
try {
    & $javaExe @javaArgs
    exit $LASTEXITCODE
}
catch {
    Write-Error "Failed to execute Gradle: $_"
    exit 1
}