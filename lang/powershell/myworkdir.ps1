Param (
    [string] $desc
    )

$workDate = Get-Date -Format "yyyy-MM-dd"

$tmplDir = "c:\template_directory"
$destDir = $(Convert-Path .)
$destDir = "$destDir/${workDate}_${desc}"
$issimple = $TRUE


if ( Test-Path "$destDir") {
    exit
}


New-Item -ItemType Directory -Path "$destDir" | Out-Null

if (!$issimple) {
    Get-Children -Recurse "$tmplDir" | sort | ForEach-Object -Process {
        $fname = $_.FullName
        $destFname = $fname.SubString($tmplDir.Length)
        $destFname = $destFname -replace "yyyy", "$dest"
        $destFname = $destFname -replace "dddd", "$workDate"

        if (Test-Path $fname -PathType Container) {
            if (-Not (Test-Path "$destDir/$destFname")) {
                mkdir "$destDir/$destFname" | Out-Null
            }
        } else {
            Copy-Item -Recurse "$fname" -Destination "$destDir/$destFname"
        }
    }
}

cd $destDir
