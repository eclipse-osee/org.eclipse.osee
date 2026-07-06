---
summary: "Markdown publishing pipeline for HTML, PDF, and raw markdown output formats including image handling and table rendering"
tags: [publishing, markdown, pdf, html, images, openhtmltopdf, flexmark]
fileMatch: "**/publishing/markdown/Markdown*,**/MarkdownConverter*,**/markdownToPdfStyles*,**/PublishingEndpointImpl*,**/WordTemplateProcessorServer*"
---

# Markdown Publishing

## Overview

The markdown publishing system converts artifact markdown content into HTML, PDF, or raw markdown output. It processes `<image-link>` references, applicability tags, artifact links, captions, and tables of contents before rendering the final document.

## Design Principles

1. **Uniform processing across output formats.** Post-processing transformations in `WordTemplateProcessorServer` (image links, captions, applicability, etc.) should produce output that works for all formats (markdown, HTML, PDF, and eventually DOCX) without format-specific branching. Where format-specific behavior is unavoidable, use the `PublishingOutputFormatter` abstraction (determined at API entry) rather than inline conditionals.

2. **Minimize cyclomatic complexity and edge cases.** Prefer a single code path that produces universally valid output (e.g., inline HTML with styles that work in both markdown viewers and browsers) over separate branches per format. Each branch doubles the testing surface and introduces potential divergence.

3. **Deterministic processing.** Transformations should be order-independent where possible and produce identical results given identical inputs. Avoid state that accumulates differently depending on output format.

4. **Format differences belong in converters, not processors.** The shared pipeline (`postProcessMarkdown`) should output format-neutral content. Format-specific enhancement (e.g., `embedImages` computing exact pixel dimensions for PDF) belongs in the downstream converter, not the shared processing stage.

## Architecture

### Endpoints

All publishing endpoints are in `plugins/org.eclipse.osee.define/src/org/eclipse/osee/define/rest/publishing/PublishingEndpointImpl.java`:

- `publishMarkdown` — Returns a zip containing `document.md` + image files
- `publishMarkdownAsHtml` — Returns a zip containing `document.html` + image files
- `publishMarkdownAsPdf` — Returns a PDF with embedded images

### Processing Pipeline

1. **`PublishingOperationsImpl.processPublishingRequest`** — Orchestrates the publish via `WordTemplateProcessorServer`
2. **`WordTemplateProcessorServer.postProcessMarkdown`** — Runs content transformations in order:
   - `processApplicability` — Resolves feature/config tags
   - `processArtifactLinks` — Converts `<artifact-link>` to markdown links
   - `processImageLinks` — Converts `<image-link>ID</image-link>` to `![name](resources/name_ID.ext "name")`
   - `processCaptions` — Converts `<figure-caption>` / `<table-caption>` (with optional `position` attribute) to numbered HTML divs
   - `processTableOfContents` — Builds TOC, figure list, table list
3. **`WordTemplateProcessorServer.packageMarkdown`** — Bundles processed markdown + image binaries into a zip
4. **`MarkdownConverter.convertMarkdownZipToPdf`** (PDF path) — Parses markdown with flexmark, renders to HTML, embeds images, converts to PDF via OpenHTMLToPDF

### Key Classes

| Class | Location | Purpose |
| :-- | :-- | :-- |
| `MarkdownConverter` | `plugins/org.eclipse.osee.framework.core/.../markdown/` | HTML/PDF conversion, image embedding |
| `MarkdownHtmlUtil` | Same package | Flexmark parser options, zip processing, caption handling |
| `WordTemplateProcessorServer` | `plugins/org.eclipse.osee.define/.../publishing/` | Content post-processing, image resolution, zip packaging |
| `PublishingOperationsImpl` | Same package | Endpoint operation implementations |
| `PdfPublishingOutputFormatter` | `plugins/org.eclipse.osee.framework.core/.../publishing/` | PDF-specific page/data-rights CSS generation |

## Image Handling

### `<image-link>` Resolution

Markdown content stores image references as `<image-link>ARTIFACT_ID</image-link>`. An optional `size` attribute controls the rendered dimensions: `<image-link size="m">ARTIFACT_ID</image-link>`.

Valid size values:
| Size | % of max width | Standalone result | Table cell result |
|------|---------------|-------------------|-------------------|
| `xs` | 25% | up to 117px | up to 37px |
| `s` | 50% | up to 234px | up to 75px |
| `m` | 75% | up to 351px | up to 112px |
| `l` | 100% | up to 468px | up to 150px |
| _(omitted)_ | 100% (default) | up to 468px | up to 150px |

All sizes are capped by the image's native resolution — images are never upscaled.

During `processImageLinks`, each tag is:
1. Resolved to an `ImageArtifact` via database query
2. If no size attribute: replaced with standard markdown `![sanitized_name](resources/sanitized_name_ID.ext "sanitized_name")`
3. If size attribute present: replaced with inline HTML `<img src="resources/sanitized_name_ID.ext" alt="sanitized_name" style="max-width:75%;height:auto" />` — flexmark passes raw HTML through unchanged
4. The image bytes are stored in `linkedMdImages` for later zip packaging

The inline `style` attribute is the single mechanism for all output formats:
- **PDF path:** `embedImages` reads `max-width:N%` from the style, calculates pixel dimensions accordingly, then removes the style (explicit `width`/`height` attributes take over)
- **HTML path:** The style is already in the rendered HTML — browsers respect it natively
- **Raw markdown path:** The `<img>` tag with inline style is embedded directly in the markdown (valid — markdown allows inline HTML). Most viewers that render inline HTML will respect the sizing.

### PDF Image Embedding (`MarkdownConverter.embedImages`)

The CSS `table-layout: fixed` is what enables images to render inside table cells (it gives OpenHTMLToPDF a resolved column width to work with). However, without explicit dimensions, images render at their full intrinsic size and overflow cells. The `embedImages` method uses Jsoup to:
1. Parse the HTML into a DOM
2. Replace each `<img src="...">` with its base64 data URI
3. Read image dimensions via `javax.imageio.ImageIO` (header-only, no full decode)
4. Read `max-width:N%` from the image's inline `style` attribute if present, apply as percentage-based width cap, then remove the style (explicit `width`/`height` take over for PDF rendering)
5. Set explicit `width`/`height` attributes capped to:
   - **150px** inside `<td>` / `<th>` (table cells)
   - **468px** standalone (~6.5in at 72dpi, matching page content width)
   - If a size hint is present, the cap is further reduced to `sizePercent × maxWidth`
   - Images are never upscaled beyond their native resolution

## Captions

### Syntax

```
<table-caption>Caption text</table-caption>
<table-caption position="above">Caption text</table-caption>
<figure-caption>Caption text</figure-caption>
<figure-caption position="above">Caption text</figure-caption>
```

- `position="above"` — Caption is placed above the table/figure in the source markdown
- `position="below"` or no attribute — Caption is placed below (default)
- Captions are separated from their table/figure by a blank line in the raw markdown

### Processing (`MarkdownHtmlUtil.processCaptions`)

The regex patterns accept an optional `position` attribute:
- `FIGURE_CAPTION_PATTERN`: `<figure-caption(?:\s+position="(above|below)")?>([^<]+)</figure-caption>`
- `TABLE_CAPTION_PATTERN`: `<table-caption(?:\s+position="(above|below)")?>([^<]+)</table-caption>`

Group 1 = position (may be null), Group 2 = caption text.

Each caption is replaced with:
1. An anchor: `<a id="figure-caption-N"></a>` or `<a id="table-caption-N"></a>`
2. A numbered div: `<div class="figure-caption" style="...">Figure N: text</div>`

The caption text is unescaped (`<` → `<`) before rendering, since the web editor escapes `<` to prevent regex breakage in the raw markdown.

Captions are numbered sequentially (Figure 1, Figure 2... / Table 1, Table 2...) in document order regardless of position attribute.

## CSS (PDF)

Location: `plugins/org.eclipse.osee.framework.core/OSEE-INF/markdown/markdownToPdfStyles.css`

Key rules:
- `table-layout: fixed` — Forces equal column distribution from the page width; gives OpenHTMLToPDF a resolved container width so images can render in table cells
- `overflow: hidden; word-wrap: break-word` — Prevents wide tables from overflowing the page
- `td img` — Specific styling for images in table cells (`display: inline-block`, `vertical-align: middle`)

## Demo Data

- **Markdown requirement files**: `plugins/org.eclipse.osee.ats.ide.demo/OSEE-INF/requirements/SAW-*.md`
- **Import logic**: `plugins/org.eclipse.osee.ats.ide.demo/.../ImportAndSetupMarkdownReqs.java`
- **Image artifacts + working branch**: `plugins/org.eclipse.osee.ats.rest/.../AtsDbConfigMarkdownDemoOp.java`

The `SAW-SystemRequirements.md` file includes a "Subsystem Component Overview" table with `<image-link>` references in cells for testing image-in-table rendering.

## Tests

Location: `plugins/org.eclipse.osee.ats.ide.integration.tests/.../publishing/markdown/`

| Test Class | What It Tests |
| :-- | :-- |
| `PublishingMarkdownConversionTest` | Raw markdown-to-HTML string conversion |
| `PublishingMarkdownTest` | Raw markdown zip output |
| `PublishingMarkdownAsHtmlTest` | HTML output: structure, images, links, applicability, captions (incl. `position` attribute) |
| `PublishingMarkdownAsPdfTest` | PDF output: content, data rights, image rendering |
| `MarkdownSanitizerTest` | Applicability tag sanitization |
| `WordTemplateContentToMarkdownContentConversionTest` | Word template to markdown content conversion |
| `EmbedImagesTest` | Unit tests for `embedImages`: dimension capping, table vs standalone context |

Suite: `PublishingMarkdownTestSuite` (`plugins/org.eclipse.osee.ats.ide.integration.tests/.../publishing/markdown/`)

## Dependencies

- **Flexmark 0.64.8** — Markdown parsing and HTML rendering (with TablesExtension, TocExtension, etc.)
- **OpenHTMLToPDF** (via `flexmark-pdf-converter`) — HTML-to-PDF rendering
- **Apache PDFBox** — PDF document manipulation (page trimming, test assertions)
- **Jsoup** — HTML DOM manipulation for image embedding
- **javax.imageio** (JDK) — Image dimension reading from file headers
