# Markdown Editor

The markdown editor provides a rich editing experience for writing and formatting content using markdown syntax. It features a split-pane layout with a live preview, image support, table editing, and fullscreen mode.

## Toolbar

The toolbar at the top of the editor provides quick access to all features. It is organized into sections that progressively collapse into compact dropdown buttons when the editor is narrow, and expand when space is available.

### History

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">undo</span> | Undo | Revert your last edit (groups by word/pause, like VS Code). |
| <span class="material-icons">redo</span> | Redo | Re-apply a reverted edit. |

### Media

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">image</span> | Upload Image | Attach an image to your content. |
| <span class="material-icons">table_chart</span> | Table | Create or edit a markdown table. |
| <span class="material-icons">select_all</span> | Select Table | Select the table at your cursor position. |
| <span class="material-icons">video_label</span> | Figure Caption | Insert a figure caption tag. |
| <span class="material-icons">legend_toggle</span> | Table Caption | Insert a table caption tag. |

### Format

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">title</span> | Heading | Insert a heading. Click the icon for H2, or the dropdown arrow for all levels (H1–H6). |
| <span class="material-icons">format_bold</span> | Bold | Wrap selected text in `**bold**`. |
| <span class="material-icons">format_italic</span> | Italic | Wrap selected text in `*italic*`. |
| <span class="material-icons">strikethrough_s</span> | Strikethrough | Wrap selected text in `~~strikethrough~~`. |

### Insert

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">format_list_bulleted</span> | List | Insert a list. Click the icon for bullets, or the dropdown arrow for numbered/task lists. |
| <span class="material-icons">code</span> | Inline Code | Wrap selected text in backticks. |
| <span class="material-icons">link</span> | Link | Insert a markdown link. |
| <span class="material-icons">format_quote</span> | Blockquote | Insert a blockquote. |
| <span class="material-icons">data_object</span> | Code Block | Insert a fenced code block. |
| <span class="material-icons">horizontal_rule</span> | Horizontal Rule | Insert a `---` divider. |

### View

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">visibility</span> / <span class="material-icons">edit</span> | Preview | Toggle between image preview and edit mode. |
| <span class="material-icons">view_sidebar</span> | Preview Panel | Show or hide the live preview panel. |
| <span class="material-icons">fullscreen</span> | Fullscreen | Enter or exit fullscreen mode. |
| <span class="material-icons">help_outline</span> | Help | Open this help panel. |

### Responsive Collapse

When the editor is narrow, toolbar sections collapse right-to-left into compact icon+arrow buttons. Click a collapsed section button to access its actions via a dropdown. The sections expand back automatically when space becomes available.

## Formatting

Type markdown syntax directly in the textarea, or use toolbar buttons to insert formatting at the cursor. If text is selected, inline formatting (bold, italic, strikethrough, code, link) wraps the selection.

Common formatting:

| Icon | Syntax | Result |
| :-- | :-- | :-- |
| <span class="material-icons">format_bold</span> | `**bold**` | **bold** |
| <span class="material-icons">format_italic</span> | `*italic*` | *italic* |
| <span class="material-icons">strikethrough_s</span> | `~~strikethrough~~` | ~~strikethrough~~ |
| <span class="material-icons">title</span> | `# Heading` | Heading (more `#` = smaller) |
| <span class="material-icons">format_list_bulleted</span> | `- item` | Bullet list |
| <span class="material-icons">format_list_numbered</span> | `1. item` | Numbered list |
| <span class="material-icons">checklist</span> | `- [ ] task` | Task list |
| <span class="material-icons">code</span> | `` `code` `` | Inline code |
| <span class="material-icons">link</span> | `[text](url)` | Hyperlink |
| <span class="material-icons">format_quote</span> | `> quote` | Blockquote |
| <span class="material-icons">data_object</span> | ` ``` ` | Code block |
| <span class="material-icons">horizontal_rule</span> | `---` | Horizontal rule |

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

## Captions

Use caption tags to add labeled captions to figures and tables. Captions register entries in the **List of Figures** and **List of Tables** when publishing.

| Icon | Button | Action |
| :-- | :-- | :-- |
| <span class="material-icons">video_label</span> | Figure Caption | Insert a figure caption tag. |
| <span class="material-icons">legend_toggle</span> | Table Caption | Insert a table caption tag. |

- `<figure-caption>text</figure-caption>` — caption for an image. Registers in the List of Figures.
- `<table-caption>text</table-caption>` — caption for a table. Registers in the List of Tables.
- Add `position="above"` to place the caption above instead of below.

## Preview

The right panel shows a live HTML preview of your markdown content. The preview updates as you type (with a short debounce delay). The preview supports dark mode and renders blockquotes, strikethrough, headings, lists, code blocks, and other formatting.

**Toggle preview:** Use the <span class="material-icons" style="font-size:14px;vertical-align:middle">view_sidebar</span> / <span class="material-icons" style="font-size:14px;vertical-align:middle">vertical_split</span> button to show or hide the preview panel. The editor expands to fill the available space when the preview is hidden.

**Resize:** Drag the vertical divider between the editor and preview to adjust the split ratio. You can also use keyboard arrow keys when the divider is focused.

## Fullscreen

Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">fullscreen</span> button to expand the editor to fill your entire screen. All features remain available in fullscreen mode, including toolbar dropdowns and split buttons.

Press **Escape** or click the <span class="material-icons" style="font-size:14px;vertical-align:middle">fullscreen_exit</span> button to return to normal view.

> Note: Dialogs (image upload, table editor) will temporarily exit fullscreen while open, then restore it when closed.

## Keyboard Shortcuts

| Shortcut | Action |
| :-- | :-- |
| Ctrl+Z | Undo. |
| Ctrl+Y | Redo. |
| Ctrl+Shift+Z | Redo (alternative). |
| Escape | Exit fullscreen. |
