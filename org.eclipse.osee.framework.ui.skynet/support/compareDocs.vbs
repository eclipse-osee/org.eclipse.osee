Option Explicit

Dim oWord
Dim baseDoc
Dim authorName
Dim detectFormatChanges
Dim namedArguments
Dim ver1
Dim ver2
Dim diffPath
Dim wdCompareTargetSelected
Dim wdFormatXML
Dim visible

Public Sub main()
	wdCompareTargetSelected = 0   ' can you believe I have to define a system enum
	wdFormatXML = 11		'dude more than once
	
	Set namedArguments = WScript.Arguments.Named
	
	If namedArguments.Exists("author") Then
		authorName = namedArguments.Item("author")
	Else
		authorName = "OSEE Doc compare"
	End If
	
	If namedArguments.Exists("detectFormatChanges") Then
		detectFormatChanges = (namedArguments.Item("detectFormatChanges") = "True")
	Else
		detectFormatChanges = True
	End If
	
	If namedArguments.Exists("ver1") Then
		ver1 = namedArguments.Item("ver1")
	Else
		MsgBox "required argument ver1 is missing"
		Exit Sub
	End If
	
	If namedArguments.Exists("ver2") Then
		ver2 = namedArguments.Item("ver2")
	Else
		MsgBox "required argument ver2 is missing"
		Exit Sub
	End If
	
	
	If namedArguments.Exists("visible") Then
		visible = namedArguments.Item("visible")
	Else
		MsgBox "required argument visible is missing"
		Exit Sub
	End If
	
	If namedArguments.Exists("diffPath") Then
		diffPath = namedArguments.Item("diffPath")
	Else
		diffPath = "c:\UserData\diff.xml"
	End If
		
	'Start Word and open the document.
	set oWord = WScript.CreateObject("Word.Application")
	oWord.Visible = False
	
	set baseDoc = oWord.Documents.Open (ver1)
	
	oWord.ActiveDocument.Compare ver2, authorName, wdCompareTargetSelected, detectFormatChanges, False, False
   oWord.ActiveDocument.SaveAs diffPath, wdFormatXML, , , False
    
    baseDoc.close()

   If visible Then
		oWord.Visible = True
	Else 
		oWord.Quit()
		set oWord = Nothing
	End If
'	
End Sub

main