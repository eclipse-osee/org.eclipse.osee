# Markdown Editor

The markdown editor provides a rich editing experience for writing and formatting content using markdown syntax. It features a split-pane layout with a live preview, image support, table editing, and fullscreen mode.

## Toolbar

The toolbar at the top of the editor provides quick access to all features:

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">undo</span> | Undo | Revert your last edit. |
| <span class="material-icons">redo</span> | Redo | Re-apply a reverted edit. |
| <span class="material-icons">lightbulb</span> | Examples | Insert example markdown snippets. |
| <span class="material-icons">image</span> | Upload Image | Attach an image to your content. |
| <span class="material-icons">table_chart</span> | Table | Create or edit a markdown table. |
| <span class="material-icons">select_all</span> | Select Table | Select the table at your cursor position. |
| <span class="material-icons">visibility</span> / <span class="material-icons">edit</span> | Preview | Toggle between image preview and edit mode. |
| <span class="material-icons">view_sidebar</span> | Preview Panel | Show or hide the live preview panel. |
| <span class="material-icons">fullscreen</span> | Fullscreen | Enter or exit fullscreen mode. |
| <span class="material-icons">help_outline</span> | Help | Open this help panel. |

## Formatting

Type markdown syntax directly in the textarea. Common formatting:

- `**bold**` → **bold**
- `*italic*` → *italic*
- `# Heading` → creates a heading (more `#` = smaller heading).
- `- item` → bullet list.
- `1. item` → numbered list.
- `` `code` `` → inline code.
- `> quote` → blockquote.
- `---` → horizontal rule.

Use the **Examples** menu (<span class="material-icons" style="font-size:14px;vertical-align:middle">lightbulb</span> icon) to insert pre-written markdown snippets as a starting point.

## Images

Upload images to embed them in your content. Images are stored as OSEE artifacts and referenced using a special tag.

**To upload an image:**
1. Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">image</span> button in the toolbar.
2. Select a file and optionally set a caption and size.
3. The image reference is inserted at your cursor position.

**Drag and drop:** You can also drag an image file directly onto the editor textarea.

**Size options:**
- XS: 25% width.
- S: 50% width.
- M: 75% width.
- L: 100% width.

**Image preview:** Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">visibility</span> icon to see images rendered inline instead of as tags. Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">edit</span> icon to return to editing. Large markdown content with many images may take a moment to render in preview mode. A loading indicator appears while images are being fetched.

> Note: The image upload button is only available after the artifact has been saved at least once.

## Tables

Create and edit markdown tables using a visual dialog.

**To create a new table:**
1. Place your cursor where you want the table.
2. Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">table_chart</span> button in the toolbar.
3. Configure the table dimensions, headers, and cell content.
4. Click "Insert" to add the table to your markdown.

**To edit an existing table:**
1. Place your cursor anywhere inside an existing table (including on a caption line).
2. Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">table_chart</span> button. It opens in edit mode with the table pre-loaded.

**To select an entire table:**
Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">select_all</span> button to highlight the full table at your cursor position in the textarea, making it easy to copy or delete.

Tables are stored as standard markdown pipe syntax and can be edited as raw text or through the visual dialog.

## Preview

The right panel shows a live HTML preview of your markdown content. The preview updates as you type (with a short debounce delay).

**Toggle preview:** Use the <span class="material-icons" style="font-size:14px;vertical-align:middle">view_sidebar</span> / <span class="material-icons" style="font-size:14px;vertical-align:middle">vertical_split</span> button to show or hide the preview panel. The editor expands to fill the available space when the preview is hidden.

**Resize:** Drag the vertical divider between the editor and preview to adjust the split ratio. You can also use keyboard arrow keys when the divider is focused.

## Fullscreen

Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">fullscreen</span> button to expand the editor to fill your entire screen. All features remain available in fullscreen mode.

Press **Escape** or click the <span class="material-icons" style="font-size:14px;vertical-align:middle">fullscreen_exit</span> button to return to normal view.

> Note: Dialogs (image upload, table editor) will temporarily exit fullscreen while open, then restore it when closed.

## Keyboard Shortcuts

| Shortcut | Action |
| :-- | :-- |
| Ctrl+Z | Undo. |
| Ctrl+Y | Redo. |
| Escape | Exit fullscreen. |

