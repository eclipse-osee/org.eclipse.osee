# EFile (Excel File) Model

Mirrors an Excel workbook, but could be for other generic table models

EFile
   - 1 EWorkbook
      - 1 EWorksheet
         - 1 EHeaderRow
            - 1..n EHeaderCell
         - 1 ERow
            - 1..n ECell 
         - 2..n ERows
      - 2..n EWorksheets
 
