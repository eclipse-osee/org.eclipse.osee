<#
/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
This file was created with human authorship and with assistance from an AI tool.

Convert-WmzEmzToPng.ps1
- Converts .wmz/.emz files to PNG using Word COM + SaveAs(filtered HTML).
- Logs to console and to a log file.
Usage:
  powershell -ExecutionPolicy Bypass -File .\Convert-WmzEmzToPng.ps1 -InputFolder "C:\in" -OutputFolder "C:\out" -LogFile "C:\out\convert.log"
#>

param(
    [Parameter(Mandatory = $true)]
    [string]$InputFolder,

    [Parameter(Mandatory = $true)]
    [string]$OutputFolder,

    [string]$LogFile,

    [switch]$ShowWordWindow,

    [switch]$VerboseLog
)

# Ensure output folder exists
if (-not (Test-Path $InputFolder)) {
    Write-Error "Input folder not found: $InputFolder"
    exit 2
}
New-Item -ItemType Directory -Force -Path $OutputFolder | Out-Null

# Create default log path if none provided
if ([string]::IsNullOrWhiteSpace($LogFile)) {
    $stamp = (Get-Date).ToString("yyyyMMdd_HHmmss")
    $LogFile = Join-Path -Path $OutputFolder -ChildPath ("wmz_convert_{0}.log" -f $stamp)
}

# Logging function: writes to console and appends to log file
function Log {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Message,
        [ValidateSet("INFO", "WARN", "ERROR", "DEBUG")]
        [string]$Level = "INFO"
    )
    $ts = (Get-Date).ToString("s")
    $line = "[$ts] [$Level] $Message"

    # Console output (respect VerboseLog for DEBUG)
    if ($Level -eq "DEBUG") {
        if ($VerboseLog) { Write-Host $line }
    }
    elseif ($Level -eq "WARN") {
        Write-Warning $Message
    }
    elseif ($Level -eq "ERROR") {
        Write-Error $Message
    }
    else {
        Write-Host $line
    }

    # Append to log file
    try {
        $line | Out-File -FilePath $LogFile -Append -Encoding UTF8
    }
    catch {
        Write-Host "Failed to write to log file $LogFile $($_.Exception.Message)"
    }
}

Log ("Script started. InputFolder={0}, OutputFolder={1}, LogFile={2}" -f $InputFolder, $OutputFolder, $LogFile) "INFO"

# Discover files recursively (case-insensitive)
$files = Get-ChildItem -Path $InputFolder -File -Recurse -ErrorAction SilentlyContinue |
Where-Object { $_.Extension -match '^\.(?i)(wmz|emz)$' }

if ($files.Count -eq 0) {
    Log "No .wmz or .emz files found in '$InputFolder' (recursive)." "WARN"
    exit 0
}

Log ("Discovered {0} file(s)." -f $files.Count) "INFO"
$files | ForEach-Object { Log (" - {0} ({1} bytes)" -f $_.FullName, $_.Length) "DEBUG" }

# Start Word COM once
try {
    Log "Starting Word COM..." "INFO"
    $word = New-Object -ComObject Word.Application -ErrorAction Stop
}
catch {
    Log ("Failed to create Word COM object: {0}" -f $_.Exception.Message) "ERROR"
    exit 3
}
$word.Visible = $false
if ($ShowWordWindow) { $word.Visible = $true }

# Helper to attempt deleting folder with retries
function Remove-TempFolderWithRetries {
    param(
        [string]$FolderPath,
        [int]$Retries = 6,
        [int]$DelayMs = 250
    )
    for ($i = 0; $i -lt $Retries; $i++) {
        try {
            if (Test-Path $FolderPath) {
                Remove-Item -Recurse -Force -Path $FolderPath -ErrorAction Stop
            }
            Log ("Removed temp folder: {0}" -f $FolderPath) "DEBUG"
            return $true
        }
        catch {
            Log ("Attempt {0} to remove temp folder failed: {1}" -f ($i + 1), $_.Exception.Message) "DEBUG"
            Start-Sleep -Milliseconds $DelayMs
            if ($i -ge 2) { Start-Sleep -Milliseconds ($DelayMs * 2) }
        }
    }
    Log ("Failed to remove temp folder after {0} attempts: {1}" -f $Retries, $FolderPath) "WARN"
    return $false
}

# Core conversion using copy/paste + SaveAs filtered HTML
function Convert-OneFileHtmlFallback {
    param($inputPath, $outPath)
    Log ("Converting: {0} -> {1}" -f $inputPath, $outPath) "INFO"

    $localDocs = @()
    $success = $false
    $tmpHtmlDir = $null

    try {
        $doc = $word.Documents.Add()
        $localDocs += $doc
        $rng = $doc.Range(0, 0)

        # Insert via AddPicture, fallback AddOLEObject
        $inline = $null
        try {
            $inline = $rng.InlineShapes.AddPicture($inputPath, $false, $true)
            Log ("Inserted via AddPicture: {0}" -f ([bool]($inline -ne $null))) "DEBUG"
        }
        catch {
            Log ("AddPicture failed: {0}" -f $_.Exception.Message) "DEBUG"
            try {
                $inline = $rng.InlineShapes.AddOLEObject($false, $null, $inputPath, $true, $true)
                Log ("Inserted via AddOLEObject: {0}" -f ([bool]($inline -ne $null))) "DEBUG"
            }
            catch {
                Log ("AddOLEObject failed: {0}" -f $_.Exception.Message) "DEBUG"
            }
        }

        if ($inline -eq $null -and $doc.InlineShapes.Count -eq 0 -and $doc.Shapes.Count -eq 0) {
            try {
                Log "No shapes inserted; trying doc.Content.InsertFile..." "DEBUG"
                $doc.Content.InsertFile($inputPath)
                Start-Sleep -Milliseconds 200
            }
            catch {
                Log ("InsertFile failed: {0}" -f $_.Exception.Message) "DEBUG"
            }
        }

        # Select inserted object
        if ($doc.InlineShapes.Count -gt 0) {
            $toSelect = $doc.InlineShapes.Item(1)
            $toSelect.Select()
        }
        elseif ($doc.Shapes.Count -gt 0) {
            $toSelect = $doc.Shapes.Item(1)
            $toSelect.Select()
        }
        else {
            Log "No shape/inline shape present after insertion; aborting conversion of this file." "WARN"
            return $false
        }

        Start-Sleep -Milliseconds 150
        $word.Selection.Copy()
        Start-Sleep -Milliseconds 150

        # Paste into a new document and SaveAs filtered HTML
        $newDoc = $word.Documents.Add()
        $localDocs += $newDoc
        $newDoc.Range(0, 0).PasteSpecial() | Out-Null
        Start-Sleep -Milliseconds 200

        $tmpHtmlDir = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), "wmz_convert_" + [System.Guid]::NewGuid().ToString("N"))
        New-Item -ItemType Directory -Force -Path $tmpHtmlDir | Out-Null
        $htmlPath = [System.IO.Path]::Combine($tmpHtmlDir, "export.html")
        $wdFormatFilteredHtml = 10

        try {
            $newDoc.SaveAs([ref]$htmlPath, [ref]$wdFormatFilteredHtml)
            Log ("Saved temporary HTML to {0}" -f $htmlPath) "DEBUG"
        }
        catch {
            Log ("SaveAs(filtered HTML) failed: {0}" -f $_.Exception.Message) "DEBUG"
        }

        Start-Sleep -Milliseconds 300

        # Find image output
        $filesDir = [System.IO.Path]::ChangeExtension($htmlPath, "_files")
        if (-not (Test-Path $filesDir)) {
            $candidates = Get-ChildItem -Path $tmpHtmlDir -Directory -ErrorAction SilentlyContinue | Where-Object { $_.Name -match "_files$" }
            if ($candidates.Count -gt 0) { $filesDir = $candidates[0].FullName }
        }

        if (Test-Path $filesDir) {
            $img = Get-ChildItem -Path $filesDir -File -ErrorAction SilentlyContinue |
            Where-Object { $_.Extension -match "\.(?i)(png|jpg|jpeg|gif|bmp)$" } |
            Select-Object -First 1
            if ($img -ne $null) {
                New-Item -ItemType Directory -Force -Path (Split-Path -Path $outPath -Parent) | Out-Null
                Move-Item -Force -Path $img.FullName -Destination $outPath
                Log ("Saved image to: {0}" -f $outPath) "INFO"
                $success = $true
            }
            else {
                Log ("No raster image file found in HTML output folder: {0}" -f $filesDir) "DEBUG"
            }
        }
        else {
            Log ("HTML output folder not found. Listing temp dir {0}:" -f $tmpHtmlDir) "DEBUG"
            Get-ChildItem -Path $tmpHtmlDir -Force | ForEach-Object { Log ("  {0}" -f $_.FullName) "DEBUG" }
        }

        # Close docs to release handles
        foreach ($d in $localDocs) {
            try { if ($d -ne $null) { $d.Close([ref]$false) } } catch { Log ("Error closing doc: {0}" -f $_.Exception.Message) "DEBUG" }
        }

        # Try to remove temp folder
        if ($tmpHtmlDir -ne $null -and (Test-Path $tmpHtmlDir)) {
            $removed = Remove-TempFolderWithRetries -FolderPath $tmpHtmlDir -Retries 8 -DelayMs 300
            if (-not $removed) {
                Log ("Could not remove temp folder: {0} (left in place)" -f $tmpHtmlDir) "WARN"
            }
        }

    }
    finally {
        foreach ($d in $localDocs) {
            try { if ($d -ne $null) { $d.Close([ref]$false) } } catch {}
        }
        [System.GC]::Collect()
        [System.GC]::WaitForPendingFinalizers()
    }

    return $success
}

# Process files sequentially
$failures = @()
foreach ($f in $files) {
    $basename = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
    $outPath = Join-Path -Path $OutputFolder -ChildPath ($basename + ".png")
    try {
        $ok = Convert-OneFileHtmlFallback -inputPath $f.FullName -outPath $outPath
        if (-not $ok) { $failures += $f.FullName; Log ("FAILED: {0}" -f $f.FullName) "WARN" }
    }
    catch {
        Log ("Unhandled exception for {0}: {1}" -f $f.FullName, $_.Exception.Message) "ERROR"
        $failures += $f.FullName
    }
}

# Quit Word and cleanup
try {
    if ($word -ne $null) {
        $word.Quit()
        [System.Runtime.Interopservices.Marshal]::ReleaseComObject($word) | Out-Null
    }
}
catch {
    Log ("Error quitting Word: {0}" -f $_.Exception.Message) "DEBUG"
}
[System.GC]::Collect()
[System.GC]::WaitForPendingFinalizers()

if ($failures.Count -eq 0) {
    Log "All files processed successfully." "INFO"
    exit 0
}
else {
    Log ("Some files failed to convert: {0}" -f ($failures -join ", ")) "WARN"
    exit 4
}
