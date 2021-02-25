Option Explicit

Dim oWord
Dim baseDoc
Dim compareDoc
Dim authorName
Dim detectFormatChanges
Dim ver1
Dim ver2
dim wdGranularityWordLevel
Dim wdCompareTargetSelectedDiff
Dim wdCompareTargetSelectedMerge
Dim wdFormattingFromCurrent
Dim wdFormatXML
Dim wdDoNotSaveChanges
Dim wdFieldCodeChanges
Dim mainDoc
dim newDoc

Public Sub main()
	wdCompareTargetSelectedDiff = 2
	wdGranularityWordLevel = 1
	wdDoNotSaveChanges = 0
	wdFormattingFromCurrent = 0
	wdFormatXML = 11

	authorName = "OSEE Doc compare"
	set oWord = WScript.CreateObject("Word.Application")
	oWord.Visible = False
	detectFormatChanges = false
	wdFieldCodeChanges = true


WScript.sleep(250)
	ver1 = "##SRC_FILE1##"
	ver2 = "##SRC_FILE2##"

	set baseDoc = oWord.Documents.Open (ver1)
	baseDoc.TrackRevisions = false
	baseDoc.AcceptAllRevisions
	baseDoc.Save

	set compareDoc = oWord.Documents.Open (ver2)
	compareDoc.TrackRevisions = false
	compareDoc.AcceptAllRevisions
	compareDoc.Save

	set newDoc = oWord.CompareDocuments (baseDoc, compareDoc, wdCompareTargetSelectedDiff, wdGranularityWordLevel, true, true, true, true, true, true, true, wdFieldCodeChanges, true, true, authorName)
	compareDoc.close
	newDoc.Activate
	set compareDoc = oWord.ActiveDocument

	set mainDoc = compareDoc
	baseDoc.close
	set baseDoc = Nothing
	oWord.NormalTemplate.Saved = True
	mainDoc.SaveAs "##DIFF_OUTPUT##", wdFormatXML, , , False

		oWord.Quit()
		set oWord = Nothing
End Sub

main