# Table Editor

The table editor is a visual dialog for creating and editing markdown tables. It provides a spreadsheet-like interface for managing table content, alignment, column spans, and captions.

## Table Size

Set the number of rows and columns using the **Cols** and **Rows** input fields in the toolbar. You can also add or remove rows and columns individually using the arrow buttons.

**Limits:** Tables support up to 50 columns and 100 rows.

**To resize quickly:** Type a new number in the Columns or Rows input field and press Tab or click away — the table resizes immediately.

## Headers & Spans

The first row of the table represents column headers. Each header cell is editable.

**Column spanning (merge):** Headers can span multiple columns, useful for grouping related columns under a single heading.

- Click the **merge_type** button to merge a column into the header to its left
- Click the **call_split** button to unmerge (split) a spanned column back into independent headers

Merged columns show an indicator (← Header Name) to show which header owns them. A small **H** badge appears on the alignment button for headers that span multiple columns.

**Colspan in markdown:** Spanned headers output using the `||` syntax (empty pipes for spanned columns), which is rendered by the Flexmark parser.

## Editing Cells

Click any cell to edit its content. Cells support multi-line text — press Enter to add a newline within a cell.

**Special characters:**
- Pipe characters (`|`) are automatically escaped in the output so they don't break the table structure
- Newlines are converted to `<br>` tags in the markdown output since table rows must be single lines

**Adding rows and columns:**
- Use the **keyboard_arrow_up** / **keyboard_arrow_down** buttons in the row gutter to insert rows above or below
- Use the **keyboard_arrow_left** / **keyboard_arrow_right** buttons above column headers to insert columns
- Row numbers are displayed between the arrow buttons for easy reference

**Removing rows and columns:**
- Click the **close** icon (red) next to a row or column to remove it
- At least one row and one column must remain

## Column Alignment

Each column has an alignment setting: **Left**, **Center**, or **Right**.

Click the alignment icon below a column header to cycle through the options:
- **format_align_left** — Left aligned
- **format_align_center** — Center aligned
- **format_align_right** — Right aligned

Alignment is reflected in the markdown separator row (`:--`, `:-:`, `--:`).

## Captions

Tables can have an optional caption displayed above or below.

**To add a caption:**
1. Type your caption text in the **Table Caption** input field at the bottom of the dialog
2. Toggle the position button (**vertical_align_top** / **vertical_align_bottom**) to switch between above and below placement

**Caption syntax:** Captions are output as `<table-caption>` tags adjacent to the table, separated by a blank line.

**Position:** The default position is "below." Click the position toggle to switch to "above."

> Note: Each table supports one caption. If a table somehow has both an above and below caption, only the above caption is loaded for editing.

## Undo & Redo

The table editor tracks all structural changes in an undo history.

**To undo:** Press **Ctrl+Z** or click the **undo** button in the toolbar.
**To redo:** Press **Ctrl+Y** or click the **redo** button in the toolbar.

Text edits within individual cells are captured when you focus the cell — the pre-edit state is saved so you can undo back to it. The caption input also saves undo state on focus.

The undo history holds up to 50 states.

## Keyboard Shortcuts

| Shortcut | Action |
| :-- | :-- |
| Ctrl+Z | Undo last change |
| Ctrl+Y | Redo last undone change |
| Escape | Cancel and close (same as Cancel button) |
| Tab | Move between cells |

## Tips

- **Large tables:** Tables with more than 500 cells use progressive loading — you'll see a loading indicator as rows appear in batches.
- **Edit existing tables:** If your cursor is inside a table in the markdown editor, opening the table dialog will pre-load that table for editing. This works even if your cursor is on the caption line.
- **Quick column alignment:** Click the alignment icon repeatedly to cycle through left → center → right → left.
- **Row resizing:** Drag the horizontal divider between rows to adjust row height for multi-line content.
- **Merge carefully:** Once columns are merged, cell data in the hidden columns is preserved in the data rows. Unmerging restores the columns with their original data intact.
