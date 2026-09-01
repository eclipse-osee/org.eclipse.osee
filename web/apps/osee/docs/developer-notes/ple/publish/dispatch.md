<!--
/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
 -->

# Dispatch Configuration

## Overview

The Dispatch feature provides **configuration-driven publishing** through a JSON artifact stored in OSEE. The JSON defines tabs, form fields, file inputs, and target APIs. The Angular UI reads this config and dynamically renders the publishing page — no code changes are needed to add new publishing workflows.

---

## Data Driven by a Server-Side Artifact

A server-side **artifact token** (`DispatchConfig`, ID 10716029 on branch 570) stores the `publishingJson` that drives the page. The dispatch component fetches this artifact's attribute on load.

The JSON defines:

- the page title,
- one or more publishing tabs,
- descriptions and instructions per tab,
- dropdown controls (static, API-fetched, or registered component),
- checkbox controls,
- file upload inputs,
- and the target API used to perform the publish action.

---

## UI Design

The Angular component at `src/app/ple/dispatch/dispatch.component.ts` is the top-level host. It fetches config, renders a `MatTabGroup`, and syncs the active tab to the `?tab=` query param.

Each tab is rendered by `dispatch-tab.component.ts` which handles:

- form construction from config,
- registered component rendering,
- autocomplete dropdown filtering,
- file selection,
- URL template resolution,
- publish execution.

### Key Design Points

#### 1. Tabs are generated from JSON

The component reads a `DispatchConfig` object and renders a Material tab for each entry in `config.tabs`. Each tab contains its own description, instructions, dropdowns, checkboxes, file inputs, and publish button.

#### 2. Registered components replace complex widgets

Instead of rendering every dropdown as a plain autocomplete, certain dropdowns specify a `component` field that maps to a pre-built Angular component:

| Component Key | Widget | Behavior |
|---------------|--------|----------|
| `branchSelector` | `BranchPickerComponent` | Button toggle (Baseline/Working) + branch autocomplete. Sets branch in routing state. |
| `viewSelector` | `CurrentViewSelectorComponent` | Applicability view autocomplete. Syncs to `?view=` query param. |
| `emailSelector` | `DispatchEmailSelectorComponent` | Chip input with autocomplete for current user's email. |

#### 3. Non-component dropdowns use autocomplete with built-in filtering

Static and API-fetched dropdowns render as a `mat-autocomplete` input. Users type to filter options directly — no separate filter field is needed.

#### 4. Dependencies between fields

Dropdowns can declare a `dependsOn` array listing keys of other dropdowns/components that must have a value before the dropdown renders. The `areDependenciesMet()` method checks both `componentValues` and the form model.

Example: A view selector can declare `"dependsOn": ["branch"]` so it only appears after a branch is selected.

#### 5. File inputs support drag-and-drop

File inputs use `DragAndDropUploadComponent` and support:

- file type filtering via `accept`,
- single or `multiple` file selection,
- optional `contentType` for raw body uploads (vs. multipart).

#### 6. Keys drive request construction

Each input's `key` acts as:

- the form control name,
- the URL template placeholder name,
- the request parameter name.

Values flow from the UI into the publish API through URL substitution (`{key}`) or as query/body parameters.

#### 7. Publish buttons are defined in JSON

Each tab's `targetApi` defines the HTTP method, URL template, and button label. The button is disabled until all required fields and files are provided.

---

## Configuration Schema

### `DispatchConfig`

```typescript
type DispatchConfig = {
  title: string;
  tabs: DispatchTabConfig[];
};
```

### `DispatchTabConfig`

```typescript
type DispatchTabConfig = {
  key: string;             // Unique tab identifier (used in ?tab= query param)
  label: string;           // Tab header text
  description: string;     // Shown below the tab header
  instructions: string[];  // Bullet list of user instructions
  dropdowns: TabDropdown[];
  checkboxes: TabCheckbox[];
  fileInputs?: TabFileInput[];
  targetApi: TargetApi;
  artifact?: string;       // Artifact ID for URL template substitution
  downloadFileName?: string; // If set, result dialog offers download
};
```

### `TabDropdown`

```typescript
type TabDropdown = {
  key: string;
  label: string;
  required?: boolean;        // Prevents publish if empty
  options?: DropdownOption[];
  contentApi?: TargetApi;
  component?: string;        // Registered component name
  dependsOn?: string[];      // Keys that must have a value first
};
```

### `TabCheckbox`

```typescript
type TabCheckbox = {
  key: string;
  label: string;
  default?: boolean;
};
```

### `TabFileInput`

```typescript
type TabFileInput = {
  key: string;
  label: string;
  accept: string;          // e.g., ".tar", ".docx,.pdf", "image/*"
  required?: boolean;
  multiple?: boolean;
  contentType?: string;    // If set, file sent as raw body with this Content-Type
};
```

### `TargetApi`

```typescript
type TargetApi = {
  method: 'GET' | 'POST';
  url: string;             // URL template with {placeholder} substitution
  button?: string;         // Button label (defaults to "Launch Publish")
};
```

---

## HTTP Execution Modes

| Condition | Method | Body |
|-----------|--------|------|
| `method === 'GET'` | GET | Query params from form |
| `POST` without files | POST | JSON body |
| `POST` with files, no `contentType` | POST | `multipart/form-data` |
| `POST` with files + `contentType` set | POST | Raw file body with Content-Type header |

---

## Runtime Behavior

### Config loading

The component fetches configuration from:
`/orcs/branch/570/artifact/10716029/attribute/type/1152921504606847380`

### Form creation

For each tab, a signal-based form is built containing one field per non-component dropdown and one per checkbox.

### Dynamic dropdown loading

When dependencies are met (e.g., branch selected), the tab's `resource()` fetches options from `contentApi` URLs. Placeholder substitution resolves `{branch}`, `{artifact}`, etc. If a fetch fails, the dropdown gets an empty options array and a warning is logged.

### URL template resolution

Publish URLs are resolved using:
- `{branch}` / `{branchId}` — from the selected branch
- `{artifact}` / `{artifactId}` — from the tab config
- `{<key>}` — from dropdown selections or component values

Unresolved placeholders produce an empty string with a console warning.

### Result dialog

After publish completes:
- If `downloadFileName` is set → shows a "Download" button
- Otherwise → displays the response text inline

---

## Example Configuration

```json
{
  "title": "Publishing",
  "tabs": [
    {
      "key": "safety",
      "label": "Safety Report",
      "description": "Generate a Safety Report XML using the source TAR",
      "instructions": [
        "Select the branch.",
        "Select the Applicability View.",
        "Select the Source TAR Archive.",
        "Click Publish Safety Report."
      ],
      "downloadFileName": "Safety_Report.xml",
      "dropdowns": [
        {
          "key": "branch",
          "label": "Branch",
          "component": "branchSelector"
        },
        {
          "key": "view",
          "label": "Applicability View",
          "component": "viewSelector",
          "dependsOn": ["branch"]
        }
      ],
      "checkboxes": [],
      "fileInputs": [
        {
          "key": "tarFile",
          "label": "Source TAR Archive",
          "accept": ".tar",
          "required": true,
          "multiple": false,
          "contentType": "application/x-tar"
        }
      ],
      "targetApi": {
        "method": "POST",
        "url": "/define/view/safety/tar?branch={branch}&view={view}&style=on",
        "button": "Publish Safety Report"
      }
    },
    {
      "key": "srs",
      "label": "SRS Book",
      "artifact": "203072",
      "description": "Generate an SRS report for a selected branch and filters.",
      "instructions": [
        "Pick a branch.",
        "Optionally filter by Document Type and Subsystem.",
        "Choose any flags and click Publish SRS."
      ],
      "dropdowns": [
        {
          "key": "branch",
          "label": "Branch",
          "component": "branchSelector"
        },
        {
          "key": "view",
          "label": "Applicability View",
          "component": "viewSelector",
          "dependsOn": ["branch"]
        },
        {
          "key": "email",
          "label": "Email Addresses",
          "component": "emailSelector"
        },
        {
          "key": "docType",
          "label": "Document Type",
          "contentApi": {
            "method": "GET",
            "url": "/orcs/branch/{branch}/relation/getRelatedHierarchy/{artifact}"
          },
          "dependsOn": ["branch"]
        },
        {
          "key": "subsystem",
          "label": "Subsystem",
          "required": true,
          "options": [
            { "id": "101", "label": "Navigation" },
            { "id": "102", "label": "Comms" }
          ]
        }
      ],
      "checkboxes": [
        { "key": "includeDrafts", "label": "Include Drafts", "default": false },
        { "key": "includeDeprecated", "label": "Include Deprecated", "default": false }
      ],
      "targetApi": {
        "method": "GET",
        "url": "/msWordPreview/{branch}/200005/{docType}/{view}/{email}",
        "button": "Publish SRS"
      }
    }
  ]
}
```

---

## Adding a New Registered Component

1. Add the key to `RegisteredComponent` type in `dispatch-component-registry.ts`.
2. Import the component in `dispatch-tab.component.ts` and add to `imports`.
3. Add a `@case` in the template's `@switch (dropdown.component)` block.
4. Add a signal or service for the component's state.
5. Add an effect that syncs the value into `this.componentValues`.

---

## Summary

The Dispatch feature provides a **JSON-driven publishing model**:

- A Java artifact stores the publishing configuration JSON.
- Angular reads the JSON and dynamically builds the page with tabs, fields, file inputs, and publish buttons.
- Field `key` names resolve URL template placeholders and build request parameters.
- Registered components (branch picker, view selector, email selector) provide reusable complex widgets.
- The `dependsOn` mechanism controls field visibility based on other field values.
- File uploads support both multipart and raw body modes.
- Each workflow defines its own publish endpoint — no hard-coded page logic required.
