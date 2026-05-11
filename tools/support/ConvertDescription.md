# Convert-WmzEmzToPng.ps1

- Converts .wmz/.emz files to PNG using Word COM + SaveAs(filtered HTML).
- Logs to console and to a log file.
- Usage:
  - powershell -ExecutionPolicy Bypass -File .\Convert-WmzEmzToPng.ps1 -InputFolder "C:\in" -OutputFolder "C:\out" -LogFile "C:\out\convert.log"
  - There is a switch for verbose logging, and a switch to make the word doc visible
  - This script is used by the class WmzConverter to convert Native Content artifacts with the emz/wmz extension to png Native Content attributes
