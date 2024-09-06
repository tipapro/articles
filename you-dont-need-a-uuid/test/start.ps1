# Set the number of iterations in this constant
$iterations = 5

# Directory for saving results
$outputDir = "benchmarks"

# Package name for testing
$benchmarkPackage = "com.example.benchmark.test"

# Path to the results directory on the device
$deviceResultsDir = "/storage/emulated/0/Android/media/$benchmarkPackage/additional_test_output"

# Benchmark class to run
$benchmarkClass = "com.example.benchmark.IdBenchmark"

# Keep the screen on
& adb shell svc power stayon true

# Create the results directory if it doesn't exist
if (-not (Test-Path -Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

# Clean the results directory on the device before starting the loop
Write-Host "Cleaning benchmark result directory on device..."
& adb shell rm -r $deviceResultsDir

# Stop all active Gradle Daemon processes
Write-Host "Stopping all Gradle Daemon processes..."
& .\gradlew.bat --stop

# Find the highest test number before starting the loop
$maxNum = 0
$fileFound = $false

Get-ChildItem -Path $outputDir -Filter "test*.json" | ForEach-Object {
    $fileName = $_.BaseName
    if ($fileName -match "test(\d+)") {
        $num = [int]$matches[1]
        if ($num -gt $maxNum) {
            $maxNum = $num
        }
        $fileFound = $true
    }
}

if (-not $fileFound) {
    Write-Host "No test files found, starting from test0.json"
}

$nextNum = $maxNum + 1

# Measure the total execution time of the script
$scriptStartTime = [System.Diagnostics.Stopwatch]::StartNew()

# Start the loop
for ($i = 1; $i -le $iterations; $i++) {
    Write-Host "Running benchmark iteration $i..."

    # Measure the time for the current iteration
    $iterationStartTime = [System.Diagnostics.Stopwatch]::StartNew()

    # Run the Gradle test using the Daemon for the specific class
    $gradleArgs = @(
        ":microbenchmark:connectedReleaseAndroidTest"
        "-Pandroid.testInstrumentationRunnerArguments.class=$benchmarkClass"
        "--daemon"
        "-q"
    )

    & .\gradlew.bat $gradleArgs
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        Write-Host "Gradle execution failed. Skipping result copy for iteration $i."
        continue
    }

    # Pull the result from the device
    Write-Host "Pulling benchmark result from device..."
    & adb pull "$deviceResultsDir/com_example_benchmark_test-benchmarkData.json" "$outputDir/test$nextNum.json"

    # Remove the results from the device
    Write-Host "Removing benchmark result from device..."
    & adb shell rm -r $deviceResultsDir

    # Analyze the JSON file and output min, avg, max for each benchmark
    Write-Host "Analyzing benchmark results..."
    $jsonContent = Get-Content -Path "$outputDir/test$nextNum.json" | ConvertFrom-Json
    foreach ($benchmark in $jsonContent.benchmarks) {
        $name = $benchmark.name
        $minTime = $benchmark.metrics.timeNs.minimum
        $maxTime = $benchmark.metrics.timeNs.maximum
        $avgTime = ($benchmark.metrics.timeNs.runs | Measure-Object -Average).Average
        
        Write-Host "Benchmark Name: $name"
        Write-Host "Minimum Time (ns): $minTime"
        Write-Host "Average Time (ns): $avgTime"
        Write-Host "Maximum Time (ns): $maxTime"
        Write-Host ""
    }

    # Increment the test number
    $nextNum++

    # Output the execution time of the current iteration
    $iterationStartTime.Stop()
    $iterationTime = [math]::Round($iterationStartTime.Elapsed.TotalMinutes, 2)
    Write-Host "Iteration $i complete in $iterationTime minutes."

    # Output the total elapsed time since the script start
    $totalTimeElapsed = [math]::Round($scriptStartTime.Elapsed.TotalMinutes, 2)
    Write-Host "Total time elapsed since script start: $totalTimeElapsed minutes."
}

# Return the screen to standard behavior
& adb shell svc power stayon false

# Output the total execution time of the script at the end
$scriptStartTime.Stop()
$totalScriptTime = [math]::Round($scriptStartTime.Elapsed.TotalMinutes, 2)
Write-Host "All iterations complete."
Write-Host "Total script execution time: $totalScriptTime minutes."
