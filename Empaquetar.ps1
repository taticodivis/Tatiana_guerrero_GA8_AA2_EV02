$ErrorActionPreference = 'Stop'
$raiz = $PSScriptRoot
$rar = Join-Path (Split-Path $raiz -Parent) 'herramientas\winrar\Rar.exe'
if (-not (Test-Path -LiteralPath $rar)) { $rar = 'C:\Program Files\WinRAR\Rar.exe' }
if (-not (Test-Path -LiteralPath $rar)) { throw 'Se necesita Rar.exe de WinRAR.' }
foreach ($f in @('Desarrollo-Movil-GA8-AA2-EV02.pdf','apk\Codivis-Movil-GA8-AA2-EV02.apk','enlace-repositorio.txt')) {
    if (-not (Test-Path -LiteralPath (Join-Path $raiz $f))) { throw "Falta $f" }
}
$destino = Join-Path $raiz 'Tatiana_Guerrero_GA8_AA2_EV02.rar'
if (Test-Path -LiteralPath $destino) { throw 'El RAR ya existe. Renombrarlo antes de regenerar.' }
$lista = Join-Path $env:TEMP ('ga8-ev02-rar-'+[guid]::NewGuid().ToString('N')+'.txt')
$archivos = @(Get-ChildItem -LiteralPath $raiz -Recurse -File -Force | Where-Object {
    $rel = $_.FullName.Substring($raiz.Length + 1)
    $rel -notmatch '(^|\\)(\.git|\.gradle|\.idea|build)(\\|$)' -and
    $_.Name -ne 'local.properties' -and $_.Extension -notin @('.zip','.rar','.log','.jks','.keystore')
} | ForEach-Object { '"'+$_.FullName.Substring($raiz.Length + 1)+'"' })
[IO.File]::WriteAllLines($lista,$archivos,[Text.Encoding]::Unicode)
Push-Location $raiz
try {
    & $rar a -m5 -idq -scul $destino ('@'+$lista)
    if ($LASTEXITCODE -ne 0) { throw 'Fallo al comprimir.' }
    & $rar t -idq $destino
    if ($LASTEXITCODE -ne 0) { throw 'Fallo al verificar el RAR.' }
} finally { Pop-Location; Remove-Item -LiteralPath $lista -Force }
Write-Output $destino
