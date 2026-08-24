---
summary: "XLSX report output with markdown rendering and image embedding for Generic Report"
tags: [generic-report, xlsx, poi, markdown, images, excel]
fileMatch: "**/writers/PublishTemplateReportXlsx*,**/writers/MarkdownExcelCellRenderer*,**/MarkdownTableToPdfImage*"
---

# Generic Report XLSX Output

## Overview

The Generic Report system supports two output formats:

1. **SpreadsheetML XML** (`.xml`) — original format via `PublishTemplateReport` + `ExcelXmlWriter`
2. **XLSX** (`.xlsx`) — POI-based format via `PublishTemplateReportXlsx` with rich content support

The XLSX output adds support for:

- Embedded images resolved from `<image-link>` tags in markdown content
- Markdown tables rendered as PNG images and embedded into cells
- Rich text formatting (bold, italic, code spans) via `XSSFRichTextString`
- Fenced code blocks rendered in monospace font

## Architecture

```
Attribute value (markdown)
        │
        ▼
MarkdownExcelCellRenderer
        │
        ├── FlexMark AST parse
        │
        ├── Text/heading/list nodes ──► XSSFRichTextString (bold/italic fonts)
        ├── Table nodes ──────────────► MarkdownTableToPdfImage → PNG → POI picture
        ├── <image-link> tags ────────► Artifact NativeContent → POI picture
        └── Fenced code blocks ───────► Monospace font run in rich text
```

## Key Classes

### `MarkdownTableToPdfImage` (framework.core)

Converts markdown fragments (primarily tables) to PNG images using the FlexMark → HTML → PDF → rasterize pipeline. Uses OpenHTMLToPDF (via FlexMark's pdf-converter) and PDFBox for rendering.

### `MarkdownExcelCellRenderer` (orcs.rest)

The orchestrator that classifies markdown AST nodes and dispatches them to the appropriate rendering path. Produces `CellRenderResult` objects containing rich text + optional image embeds.

### `PublishTemplateReportXlsx` (orcs.rest)

The streaming output writer that produces `.xlsx` files. Delegates markdown-rich cells to `MarkdownExcelCellRenderer` and writes plain cells directly.

## REST Endpoints

### Synchronous

```
GET /orcs/report/{branch}/view/{view}/template/{template}/xlsx
```

### Asynchronous

```
GET /orcs/report/{branch}/view/{view}/template/{template}/xlsx/async/{email}
```

## Content Classification

The renderer classifies each cell's content:

| Content type      | Detection                     | Rendering                                        |
| ----------------- | ----------------------------- | ------------------------------------------------ |
| Plain text        | No markdown markers           | Direct `cell.setCellValue(string)`               |
| Inline formatting | `**`, `*`, `` ` ``            | `XSSFRichTextString` with font runs              |
| Markdown table    | `TableBlock` AST node         | Render to image via `MarkdownTableToPdfImage`    |
| Image link        | `<image-link>id</image-link>` | Load binary from artifact, embed via POI drawing |
| Fenced code       | ` ``` ` blocks                | Monospace font in rich text                      |

## Image Handling

Images from `<image-link>` tags are:

1. Resolved by artifact ID on the report's branch
2. Binary data loaded from `CoreAttributeTypes.NativeContent`
3. Scaled to fit `IMAGE_MAX_WIDTH_PX` (400) × `IMAGE_MAX_HEIGHT_PX` (300)
4. Embedded via POI's `Drawing.createPicture()` with a cell anchor

## Limitations

- Mermaid diagrams in fenced code blocks are rendered as monospace source text (no server-side diagram renderer)
- Math expressions (`$...$`) are rendered as plain text
- Strikethrough is rendered as plain text (Excel rich text font API doesn't easily support it)
- Very large tables may exceed the rendered image area (capped at page width)
