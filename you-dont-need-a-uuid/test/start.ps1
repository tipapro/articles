# Установите количество итераций в этой константе
$iterations = 40

# Папка для сохранения результатов
$outputDir = "benchmarks"

# Имя пакета для тестирования
$benchmarkPackage = "com.example.benchmark.test"

# Путь к директории с результатами на устройстве
$deviceResultsDir = "/storage/emulated/0/Android/media/$benchmarkPackage/additional_test_output"

# Оставить экран включенным
& adb shell svc power stayon true

# Создать папку для результатов, если она не существует
if (-not (Test-Path -Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

# Очистить папку с результатами на устройстве перед циклом
Write-Host "Cleaning benchmark result directory on device..."
& adb shell rm -r $deviceResultsDir

# Остановить все активные Gradle Daemon
Write-Host "Stopping all Gradle Daemon processes..."
& .\gradlew.bat --stop

# Найти максимальный номер теста перед началом цикла
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

# Замер общего времени выполнения скрипта
$scriptStartTime = [System.Diagnostics.Stopwatch]::StartNew()

# Запуск цикла
for ($i = 1; $i -le $iterations; $i++) {
    Write-Host "Running benchmark iteration $i..."

    # Замер времени для текущей итерации
    $iterationStartTime = [System.Diagnostics.Stopwatch]::StartNew()

    # Запустить Gradle тест с использованием Daemon для конкретного класса
    $gradleArgs = @(
        ":microbenchmark:connectedReleaseAndroidTest"
        "-Pandroid.testInstrumentationRunnerArguments.class=com.example.benchmark.IdBenchmark"
        "--daemon"
        "-q"
    )

    & .\gradlew.bat $gradleArgs
    $exitCode = $LASTEXITCODE

    if ($exitCode -ne 0) {
        Write-Host "Gradle execution failed. Skipping result copy for iteration $i."
        continue
    }

    # Копировать результат с устройства
    Write-Host "Pulling benchmark result from device..."
    & adb pull "$deviceResultsDir/com_example_benchmark_test-benchmarkData.json" "$outputDir/test$nextNum.json"

    # Удалить результаты с устройства
    Write-Host "Removing benchmark result from device..."
    & adb shell rm -r $deviceResultsDir

    # Анализ JSON файла и вывод min, avg, max для каждого бенчмарка
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

    # Увеличить номер теста
    $nextNum++

    # Вывести время выполнения текущей итерации
    $iterationStartTime.Stop()
    $iterationTime = [math]::Round($iterationStartTime.Elapsed.TotalMinutes, 2)
    Write-Host "Iteration $i complete in $iterationTime minutes."

    # Вывести общее время выполнения скрипта
    $totalTimeElapsed = [math]::Round($scriptStartTime.Elapsed.TotalMinutes, 2)
    Write-Host "Total time elapsed since script start: $totalTimeElapsed minutes."
}

# Вернуть экран к стандартному поведению
& adb shell svc power stayon false

# Вывести общее время выполнения скрипта в конце
$scriptStartTime.Stop()
$totalScriptTime = [math]::Round($scriptStartTime.Elapsed.TotalMinutes, 2)
Write-Host "All iterations complete."
Write-Host "Total script execution time: $totalScriptTime minutes."
