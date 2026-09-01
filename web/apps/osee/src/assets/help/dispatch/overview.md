# Dispatch

Dispatch renders configurable publishing UIs from JSON config artifacts stored in OSEE. Each `DispatchConfig` artifact on the common branch defines one dispatch page with its own set of tabs.

## Multi-Page Architecture

The frontend queries all `DispatchConfig` artifacts matching its declared version and renders them as separate pages. No code changes are needed to add new publishing workflows — just create a new artifact.

### Navigation

- **Index page** (`/ple/dispatch`) — lists all available dispatch pages
- **Page** (`/ple/dispatch/:pageKey`) — a specific page with its tab group
- **Tab** (`/ple/dispatch/:pageKey/:tab`) — a specific tab within a page

The `pageKey` is derived from the artifact's Name attribute (lowercased, spaces replaced with dashes).

### Adding a New Page

1. Create a `DispatchConfig` artifact on branch 570
2. Set `Name` (e.g., "Safety Publishing")
3. Add a `Dispatch Config Json` value whose `"version"` field is `"1"`
4. The page appears automatically

## Config Version

Version is a string field inside each config JSON (`"version": "1"`). The current version is **"1"**. There is no separate version attribute.

The `Dispatch Config Json` attribute is multi-valued, so a single artifact can hold multiple JSON values — one per version. The frontend declares which version it supports and picks the JSON value whose `version` field matches. This allows nightly and release builds to coexist on the same artifact.

## Config JSON Structure (V1)

```json
{
  "version": "1",
  "title": "Page Title",
  "tabs": [
    {
      "key": "unique-tab-id",
      "label": "Tab Header Text",
      "description": "Shown below the tab header.",
      "instructions": "Markdown string rendered as formatted text.",
      "dropdowns": [...],
      "checkboxes": [...],
      "fileInputs": [...],
      "targetApi": { "method": "GET|POST", "url": "/path/{placeholder}", "button": "Button Label" },
      "artifact": "optional-artifact-id",
      "downloadFileName": "optional-filename.xml"
    }
  ]
}
```

## Dropdowns

A dropdown provides a selectable value substituted into the target API URL.

| Field | Required | Description |
|---|---|---|
| `key` | Yes | Form field key and URL placeholder name |
| `label` | Yes | Display label |
| `required` | No | If true, prevents publish when empty |
| `options` | No | Static options: `[{ "id": "1", "label": "Name" }]` |
| `contentApi` | No | Fetch options from server: `{ "method": "GET", "url": "/path/{branch}" }` |
| `component` | No | Render a registered component (`branchSelector`, `viewSelector`, `emailSelector`) |
| `dependsOn` | No | Keys of fields that must have values before this renders |

## Checkboxes

```json
{ "key": "includeDrafts", "label": "Include Drafts", "default": false }
```

## File Inputs

```json
{
  "key": "tarFile",
  "label": "Source TAR Archive",
  "accept": ".tar",
  "required": true,
  "multiple": false,
  "contentType": "application/x-tar"
}
```

When `contentType` is set, the file is sent as raw body. Without it, files are sent as multipart form data.

## Target API

```json
{ "method": "POST", "url": "/define/view/safety/tar?branch={branch}&view={view}", "button": "Publish" }
```

- URL must be a relative path (no host)
- `{placeholder}` values are substituted from dropdown keys, component values, and branch/artifact IDs
- `button` is optional — defaults to "Launch Publish"

## Registered Components

| Name | Description |
|---|---|
| `branchSelector` | Branch picker. Emits branch ID. |
| `viewSelector` | Applicability view autocomplete. Emits view ID. |
| `emailSelector` | Email selector with active user list. Emits comma-separated emails. |

## Download vs. Inline Results

When `downloadFileName` is set, the result dialog shows a Download button. Without it, the response is displayed as text.
