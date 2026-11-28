
$ErrorActionPreference = 'Stop'

# Ir a la carpeta del servicio web
Set-Location $PSScriptRoot

# Comprobar que existe la configuración privada
if (-not (Test-Path '.env')) {
    throw 'No existe el archivo .env en Proyecto-WS.'
}

# Cargar las variables del archivo .env
Get-Content '.env' | ForEach-Object {
    $line = $_.Trim()

    if ($line -and -not $line.StartsWith('#')) {
        $parts = $line -split '=', 2

        if ($parts.Count -eq 2) {
            $key = $parts[0].Trim()
            $value = $parts[1].Trim()

            [Environment]::SetEnvironmentVariable(
                $key, $value, 'Process'
            )
        }
    }
}

# Comprobar configuración obligatoria
if (-not $env:PELUDITOS_DB_PASSWORD) {
    throw 'Falta PELUDITOS_DB_PASSWORD en .env.'
}

if (-not $env:PELUDITOS_DB_HOST) {
    throw 'Falta PELUDITOS_DB_HOST en .env.'
}

Write-Host 'Configuracion cargada correctamente.'
Write-Host 'Iniciando Web Service Peluditos Pet Chile...'

# Iniciar aplicación Java con Gradle
& .\gradlew.bat run

if ($LASTEXITCODE -ne 0) {
    throw 'El Web Service no pudo iniciarse correctamente.'
}
