Param(
)

function bk_file() {
    param (
        [Parameter(Mandatory=$true)][string] $filename
    )

    if ( -not $(Test-Path "$filename")) {
        return "NG    "
    }

    if (-not $(Test-Path "$filename" -PathType Leaf)) {
        return "Skip:D"
    }

    $filedate=$(Get-ItemProperty $filename).LastWriteTime
    $workDate=$filedate.ToString("yyyy-MM-dd_HH-mm-ss")

    $workDir=Split-Path $filename -Parent
    $destDir="$workDir/000-Archive"
    if (-not $(Test-Path "$destDir")) {
        New-Item -ItemType Directory -Path "$destDir" | Out-Null
    }
    $bkFileBase = (Get-Item $filename).BaseName
    $bkExt = (Get-Item $filename).Extension
    $bkFile = "$destDir/$bkFileBase.$workDate$bkExt"

    Copy-Item $filename $bkFile
    return "    OK"
}

For ($i=0; $i -lt $Args.Count; $i++) {
    $fn = $Args[$i]
    (Resolve-Path $fn) | %{
        $fnn = $_
        $rslt=bk_file $fnn
        $fnq = (Get-Item $fnn).Name
        echo "$rslt --- $fnq"
    }
}
