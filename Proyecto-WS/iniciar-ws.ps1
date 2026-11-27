# Carga las variables de Proyecto-WS/.env en esta sesion y ejecuta el servicio.
$ErrorActionPreference = 'Stop'
$envFile = Join-Path $PSScriptRoot '.env'
$exampleFile = Join-Path $PSScriptRoot '.env.example'

if (-not (Test-Path $envFile)) {
    if (Test-Path $exampleFile) {
        Copy-Item $exampleFile $envFile
        Write-Host 'Se creo .env desde .env.example.' -ForegroundColor Yellow
        Write-Host 'Edita .env y configura la IP/host y la clave real de SQL Server; luego vuelve a ejecutar este script.'
        exit 1
    }
    throw 'No existe .env ni .env.example.'
}

Get-Content $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line -split '=', 2
        $name = $parts[0].Trim()
        $value = $parts[1].Trim()
        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        [Environment]::SetEnvironmentVariable($name, $value, 'Process')
    }
}

if ([string]::IsNullOrWhiteSpace($env:PELUDITOS_DB_PASSWORD) -or $env:PELUDITOS_DB_PASSWORD -eq 'CAMBIAR_POR_SECRETO_REAL') {
    throw 'Configura PELUDITOS_DB_PASSWORD en Proyecto-WS/.env antes de iniciar.'
}

Set-Location $PSScriptRoot
.\gradlew.bat run
