# Define the directory containing JSON files
$directory = ".\benchmarks"
# Define the output file
$outputFile = "combined_benchmarks.json"

# Function to calculate the percentiles
function Calculate-Percentiles {
    param (
        [float[]]$data
    )

    $sortedData = $data | Sort-Object
    $count = $sortedData.Count

    # Calculate percentiles
    $percentiles = [ordered]@{}
    $percentiles.p0 = [math]::Round($sortedData[0], 8)
    $percentiles.p25 = [math]::Round($sortedData[[math]::Floor($count * 0.25)], 8)
    $percentiles.p50 = [math]::Round($sortedData[[math]::Floor($count * 0.50)], 8)
    $percentiles.p75 = [math]::Round($sortedData[[math]::Floor($count * 0.75)], 8)
    $percentiles.p95 = [math]::Round($sortedData[[math]::Floor($count * 0.95)], 8)
    $percentiles.p100 = [math]::Round($sortedData[-1], 8)

    return $percentiles
}

# Read and process all JSON files
$benchmarks = @{}

Get-ChildItem -Path $directory -Filter *.json | ForEach-Object {
    $jsonContent = Get-Content -Path $_.FullName -Raw | ConvertFrom-Json
    foreach ($benchmark in $jsonContent.benchmarks) {
        $name = $benchmark.name
        if (-not $benchmarks.ContainsKey($name)) {
            $benchmarks[$name] = [PSCustomObject]@{
                name = $name
                timeNsRuns = @()
                allocationCountRuns = @()
            }
        }
        $benchmarks[$name].timeNsRuns += $benchmark.metrics.timeNs.runs
        $benchmarks[$name].allocationCountRuns += $benchmark.metrics.allocationCount.runs
    }
}

# Prepare data for output
$result = @()
foreach ($key in $benchmarks.Keys) {
    $benchmark = $benchmarks[$key]
    $timeNsPercentiles = Calculate-Percentiles -data $benchmark.timeNsRuns
    $allocationCountPercentiles = Calculate-Percentiles -data $benchmark.allocationCountRuns

    $result += [PSCustomObject]@{
        name = $benchmark.name
        timeNs = $timeNsPercentiles
        allocationCount = $allocationCountPercentiles
    }
}

# Write the final JSON file with compression to minimize whitespace
$result | ConvertTo-Json -Depth 5 -Compress | Set-Content -Path $outputFile

Write-Host "Combination and percentile calculation completed. Output file: $outputFile"
