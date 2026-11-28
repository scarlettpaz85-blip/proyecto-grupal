$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (-not (Test-Path '.env')) { throw 'Falta Proyecto-Worker/.env. Copia .env.example a .env y complétalo.' }
Get-Content '.env' | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#')) {
        $parts = $line -split '=', 2
        if ($parts.Count -eq 2) {
            $key = $parts[0].Trim()
            $value = $parts[1].Trim()
            [Environment]::SetEnvironmentVariable($key, $value, 'Process')
        }
    }
}
if (-not $env:FLOWABLE_URL) { throw 'Configura FLOWABLE_URL en .env.' }
if (-not $env:WS_URL) { throw 'Configura WS_URL en .env.' }
if (-not $env:FLOWABLE_TOKEN -and (-not $env:FLOWABLE_USER -or -not $env:FLOWABLE_PASSWORD)) {
    throw 'Configura FLOWABLE_TOKEN o FLOWABLE_USER y FLOWABLE_PASSWORD en .env.'
}
javac PeluditosApp.java
if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación de PeluditosApp.java.' }
java PeluditosApp
