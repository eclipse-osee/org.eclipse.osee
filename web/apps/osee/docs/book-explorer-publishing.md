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
# Book Explorer Publishing Configuration

## Overview

This change adds support for **Book Explorer–driven publishing** by introducing a Java-side **artifact token** whose attribute contains a `publishingJson` payload. That JSON is consumed by the Book Explorer UI and drives the publishing page layout, inputs, and publish actions.

The result is a configuration-driven publishing experience where tabs, fields, dynamic dropdowns, booleans, and publish buttons are all defined in JSON rather than hard-coded in the page.

---

## Data driven by a server side Artifact

To support publishing, a server side **artifact token** with an attribute that stores the `publishingJson` used by the publishing page has been defined and added - the artifact is referenced directly by the book-explorer component, and contains the publishingJson that populates the information in the page.

That `publishingJson` defines:

- the page title,
- one or more publishing tabs,
- descriptions and instructions for each tab,
- text input collection for email addresses,
- dropdown controls,
- boolean controls,
- and the target API definition used to perform the publish action.

This makes the publishing page extensible and allows new publishing workflows to be introduced by changing the artifact-backed JSON configuration.

---

## UI Design in `book-explorer.component.ts`

The Angular component `src/app/ple/book-explorer/book-explorer.component.ts` is designed as a **configuration-driven publishing UI**.

### Key design points

#### 1. Tabs are generated from JSON

The component reads a `BookExplorerConfig` object and renders a Material tab for each entry in `config.tabs`.

Each tab represents a publishing workflow and contains its own:

- label,
- description,
- instructions,
- dropdowns,
- checkboxes,
- target API,
- optional artifact identifier.

#### 2. Branch selection is required

Each tab shows a branch picker and disables publish controls until a branch is selected.

The selected branch is used when resolving URL templates such as:

- `{branch}`
- `{branchId}`

#### 3. Email input is inferred from the target API

The component includes a text input for email collection when the configured `targetApi.url` contains the token:

- `{email}`

This allows the UI to automatically render an **Email Addresses** input field only for workflows that require it.

#### 4. Dropdowns can be static or dynamic

Dropdowns are defined per tab and support two modes:

- **Static options** via `options`
- **Dynamic options** via `contentApi`

If `options` is present, the dropdown values come directly from JSON.
If `contentApi` is present, the component resolves the configured API URL and fetches the list of items to populate the dropdown.
If the optional element `value` is present and set to "required", then the dropdown is required to have a selection before the TargetAPI button will work.

#### 5. Boolean items are rendered as checkboxes

Boolean workflow flags are defined as `checkboxes` in the JSON.

Each checkbox includes:

- a `key`,
- a `label`,
- an optional `default` value.

These are added to the reactive form and included in the outgoing request.

#### 6. Keys drive request construction

Each input component has a `key`, and those keys are central to how the page works.

For example:

- dropdown keys become **form control names**
- checkbox keys become **form control names**
- those same keys are also used as **request parameter names**
- keys can be used as **URL template tokens** inside the configured APIs

This means values selected in the UI can be inserted into URLs such as:

- `{docType}`
- `{view}`
- `{email}`

The component resolves these placeholders before invoking the target API.

#### 7. Publish buttons are also defined in JSON

Each tab contains a `targetApi` definition with:

- HTTP method,
- URL,
- button label.

The `button` value defines the text displayed on the publish button, and clicking that button calls the resolved `targetApi` to perform the publish action.

If no button name is provided, the UI defaults to **Do Work**.

---

## Important Component Structures

The component models the publishing JSON using a small set of interfaces:

### `BookExplorerConfig`
Top-level configuration object containing:

- optional `title`
- `tabs`

### `BookExplorerTabConfig`
Defines a publishing workflow tab with:

- `key`
- `label`
- optional `description`
- optional `instructions`
- `dropdowns`
- `checkboxes`
- `targetApi`
- optional `artifact`

### `TabDropdown`
Defines a dropdown with:

- `key`
- `label`
- optional `value` 
- static `options`
- optional `contentApi`

### `TabCheckbox`
Defines a boolean field with:

- `key`
- `label`
- optional `default`

### `TargetApi`
Defines an API call with:

- `method`
- `url`
- optional `button`

---

## Runtime Behavior

### Config loading

The component loads configuration from an artifact-backed endpoint:

- `/orcs/branch/570/artifact/10716029/attribute/type/1152921504606847380`

The returned JSON is treated as `BookExplorerConfig`.

### Form creation

For each tab, the component builds a reactive form containing:

- an `email` control when needed,
- one control per dropdown,
- one control per checkbox.

### Dynamic dropdown loading

When a branch is selected, the component loads any dropdown content defined with `contentApi`.

The `contentApi.url` is resolved using branch and artifact substitutions before the API is called.

### URL template resolution

The component resolves placeholders using:

- branch values,
- artifact values,
- email values,
- selected dropdown values.

Supported replacement sources include:

- `branch`
- `branchId`
- `artifact`
- `artifactId`
- `email`
- each dropdown `key`

### Publish execution

When the user clicks the tab’s publish button:

- the target API URL is resolved,
- remaining dropdown and checkbox values are added as query parameters for GET requests,
- POST requests include form data in the request body,
- the configured API is called to perform the publish action.

---

## Example `publishingJson`

Below is the reference `publishingJson` used to describe the Book Explorer publishing workflows.

```json docs/book-explorer-publishing.md
{
  "title": "Book Explorer",
  "tabs": [
    {
      "key": "srs",
      "label": "SRS Book",
      "artifact": "203072",
      "description": "Generate an SRS report for a selected branch and filters.",
      "instructions": [
        "Pick a branch.",
        "Optionally filter by Document Type and Subsystem.",
        "Choose any flags and click Do Work."
      ],
      "dropdowns": [
        {
          "key": "docType",
          "label": "Document Type",
          "contentApi": {
            "method": "GET",
            "url": "/orcs/branch/{branch}/relation/getRelatedHierarchy/{artifact}"
          }
        },
        {
          "key": "subsystem",
          "label": "Subsystem",
          "value": "required",
          "options": [
            { "id": 101, "label": "Navigation" },
            { "id": 102, "label": "Comms" }
          ]
        },
        {
          "key": "view",
          "label": "Applicability View",
          "contentApi": {
            "method": "GET",
            "url": "/orcs/branch/{branch}/applic/views"
          }
        }
      ],
      "checkboxes": [
        { "key": "includeDrafts", "label": "Include Drafts", "default": false },
        {
          "key": "includeDeprecated",
          "label": "Include Deprecated",
          "default": false
        }
      ],
      "targetApi": {
        "method": "GET",
        "url": "/msWordPreview/{branch}/200005/{docType}/{view}/{email}",
        "button": "Publish SRS"
      }
    },
    {
      "key": "color",
      "label": "Color Book",
      "description": "Generate a colorized view for selected branch and filters.",
      "instructions": ["Pick filters and click Do Work."],
      "dropdowns": [],
      "checkboxes": [
        {
          "key": "showOnlyChanged",
          "label": "Show Only Changed",
          "default": true
        }
      ],
      "targetApi": {
        "method": "GET",
        "url": "/api/books/color",
        "button": "Publish color"
      }
    }
  ]
}
```

---

## Example Workflow Breakdown

### SRS Book tab

The `srs` tab demonstrates the full publishing model.

#### Email collection
Its `targetApi.url` includes `{email}`, so the component renders a text input for email addresses.

#### Dynamic dropdowns
It defines dynamic dropdowns whose items are retrieved from APIs:

- `docType` loads content from `/orcs/branch/{branch}/relation/getRelatedHierarchy/{artifact}`
- `view` loads content from `/orcs/branch/{branch}/applic/views`

#### Static dropdowns
It also defines a static dropdown:

- `subsystem` with options like `Navigation` and `Comms`

#### Boolean flags
It includes booleans:

- `includeDrafts`
- `includeDeprecated`

#### Publish action
The publish button is labeled **Publish SRS** and calls:

- `/msWordPreview/{branch}/200005/{docType}/{view}/{email}`

The placeholders are resolved from branch selection, dropdown values, and entered email addresses.

### Color Book tab

The `color` tab is a simpler workflow.

It defines:

- no dropdowns,
- one boolean checkbox: `showOnlyChanged`,
- a publish button labeled **Publish color**,
- a target API of `/api/books/color`.

Because the URL does not embed dropdown tokens or email tokens, the component sends remaining values as request parameters.

---

## Why the Key Names Matter

A key part of the design is that component keys are reused consistently across the UI and API layers.

For each dropdown or checkbox, the `key` acts as:

- the form control name,
- the logical identifier for the field,
- the parameter name for outgoing requests,
- and a template token name for URL substitution.

Examples:

- `docType` fills `{docType}`
- `view` fills `{view}`
- `email` fills `{email}`

This allows the backend-provided JSON to define not just what the UI looks like, but also how user selections map into the publish API call.

---

## Summary

This change introduces a **JSON-driven publishing model** for Book Explorer:

- Java provides the `publishingJson` through an artifact token attribute.
- Angular reads that JSON and dynamically builds the publishing page.
- Tabs define independent publishing workflows.
- Workflows can collect email addresses, present static or API-populated dropdowns, and include boolean options.
- Field `key` names are used to resolve `targetApi` placeholders and build request parameters.
- Each workflow defines its own publish button label and API endpoint, allowing the UI to launch the correct publishing operation without hard-coded page logic.

