---
summary: "Data-driven dispatch web app: versioned config artifacts, multi-page support, config-driven tabs, dropdowns, file inputs, registered components, and API execution"
tags: [publishing, dispatch, config, file-upload, safety, web]
fileMatch: "**/dispatch/**"
---

# Dispatch

## Overview

The Dispatch feature is a data-driven Angular feature that renders configurable publishing UIs from JSON config artifacts stored in OSEE. Each `DispatchConfig` artifact on the common branch defines one dispatch page with its own set of tabs. The frontend queries all config artifacts matching its declared version and renders them as separate pages. No code changes are needed to add new publishing workflows — just create a new artifact.

## Architecture

```
dispatch/
├── dispatch-index.component.ts        ← Index page listing all dispatch pages (auto-redirects if only one)
├── dispatch.component.ts              ← Single page: loads config by pageKey, renders tab group
├── dispatch-tab.component.ts          ← Each tab: form fields, validation, API execution
├── dispatch-config.service.ts         ← Fetches DispatchConfig artifacts, filters by version (cached, shared)
├── dispatch-config.normalizer.ts      ← Normalizes raw config to latest schema shape
├── dispatch-http.service.ts           ← HTTP methods: GET, POST, POST with files, raw file POST
├── dispatch-result-dialog.component.ts← Dialog showing API response or download button
├── dispatch-email-selector.component.ts ← Filterable email autocomplete with user list
├── dispatch-component-registry.ts     ← Registry of reusable components for dropdowns
├── dispatch.types.ts                  ← All type definitions
├── dispatch.constants.ts              ← Version number and artifact/attribute type IDs
├── dispatch.utils.ts                  ← Utility functions (slugify)
└── dispatch.routes.ts                 ← Lazy-loaded routes
```

## Artifact Type: DispatchConfig

Defined in `CoreArtifactTypes.java`. Each artifact represents one dispatch page.

| Attribute | Type | Description |
|---|---|---|
| `Name` | String (exactlyOne) | Display name, also used to generate the URL slug via `slugify()` |
| `Dispatch Config Json` | JSON String (atLeastOne, **multi-valued**) | One or more config JSON values, each a different schema version. The `version` field lives inside each JSON value — there is no separate version attribute. |

**IDs:**
- Artifact type: `7226028762153318337`
- JSON attribute type: `6371428937946281743`

All artifacts live on the common branch (570).

## Versioning Strategy

Version is carried inside each config JSON as a string `version` field (e.g., `"1"`), not in a separate attribute. The `Dispatch Config Json` attribute is **multi-valued**: a single artifact can hold several JSON values, one per version. The frontend declares which version it supports via `DISPATCH_CONFIG_VERSION` in `dispatch.constants.ts` and picks the matching value.

### How it works

```
Frontend code declares: DISPATCH_CONFIG_VERSION = '1'
                           ↓
Service queries:        All DispatchConfig artifacts on branch 570
                           ↓
Service loads:          Each full artifact via the ORCS artifact endpoint
                        /orcs/branch/570/artifact/{id}/related/direct?includeRelations=false
                           ↓
Service iterates:       Each artifact's attributes, keeps those whose
                        typeId === DISPATCH_CONFIG_JSON_ATTR_TYPE_ID
                           ↓
Service parses & filters: Parse each JSON value; keep the one whose
                          version field === DISPATCH_CONFIG_VERSION
                           ↓
UI renders:             One page per artifact (using its matching-version JSON)
```

Because the JSON attribute is multi-valued, the same artifact carries both the current version and older/newer versions side by side. Release code (`DISPATCH_CONFIG_VERSION = '1'`) reads the `"1"` value; a future nightly (`'2'`) reads the `"2"` value from the same artifact.

`DispatchConfigService.getConfigs()` is cached: the fan-out fetch (one artifact load per token) is wrapped in `shareReplay({ bufferSize: 1, refCount: false })` and shared between the index and page components, so navigating index → page does not re-fetch every artifact. The cache lives for the app session (config artifacts on branch 570 are effectively static). On any load error the service sets `uiService.ErrorText` and emits an empty list.

### Release vs. Nightly

Both versions coexist inside the same artifacts on the same branch. Each artifact's multi-valued `Dispatch Config Json` holds one JSON value per version:

| Artifact | JSON values (by `version`) | Who reads which |
|---|---|---|
| "Publishing" | `"1"`, `"2"` | Release reads `"1"`, Nightly reads `"2"` |
| "Reports" | `"1"`, `"2"` | Release reads `"1"`, Nightly reads `"2"` |

Release code (`DISPATCH_CONFIG_VERSION = '1'`) only reads the `"1"` value; nightly code (`'2'`) only reads the `"2"` value. Both live on the same artifact on branch 570, so there is no artifact drift between versions.

### When to bump the version

| Change type | Bump version? |
|---|---|
| Add a new optional field to the JSON schema | No — backward-compatible |
| Rename or remove an existing field | Yes — breaking |
| Change an existing field's type/shape | Yes — breaking |
| Add a new dispatch page (new artifact) | No |
| Change the config content (different tabs, endpoints) | No — same schema |

### Steps to introduce version 2

**Backend (no changes needed):** The artifact type already supports multiple JSON values via the multi-valued `Dispatch Config Json` attribute.

**Frontend:**
1. Bump `DISPATCH_CONFIG_VERSION` to `'2'` in `dispatch.constants.ts`
2. Define `DispatchConfigV2` in `dispatch.types.ts`
3. Add it to `AnyDispatchConfig` union
4. Update `normalizeDispatchConfig` to handle version `'2'`
5. Update `DispatchConfig` alias to point to V2

**Data:**
1. Add a second `Dispatch Config Json` value (with `"version": "2"`) to each existing artifact on branch 570
2. Leave the existing `"1"` value in place — release still reads it
3. No new artifacts are needed; both versions live on the same artifact

### Full migration walkthrough (V1 → V2 example)

This section describes the complete process for introducing a breaking schema change. Use this as a checklist.

**1. Define the new schema type:**

```typescript
// dispatch.types.ts
export type DispatchTabConfigV2 = {
  // ... new shape
};

export type DispatchConfigV2 = {
  readonly version: '2';
  readonly title: string;
  readonly tabs: readonly DispatchTabConfigV2[];
};

// Add to the union
export type AnyDispatchConfig = DispatchConfigV1 | DispatchConfigV2;

// Point the alias to the new version
export type DispatchConfig = DispatchConfigV2;
```

**2. Update the normalizer:**

```typescript
// dispatch-config.normalizer.ts
import type { DispatchConfigV1, DispatchConfig } from './dispatch.types';

function migrateV1toV2(raw: DispatchConfigV1): DispatchConfig {
  return {
    version: '2',
    title: raw.title,
    tabs: raw.tabs.map(tab => ({
      ...tab,
      // transform fields as needed, fill defaults for new required fields
    })),
  };
}

export function normalizeDispatchConfig(raw: unknown): DispatchConfig {
  const version = (raw as any).version ?? '1';
  switch (version) {
    case '1': return migrateV1toV2(raw as DispatchConfigV1);
    case '2': return raw as DispatchConfig;
    default:
      console.warn(`[Dispatch] Unknown config version ${version}`);
      return raw as DispatchConfig;
  }
}
```

The normalizer is a safety net — it lets the app run with a V1 JSON value during the transition by transforming it into V2 shape in memory. However, the primary mechanism is version filtering: nightly sets `DISPATCH_CONFIG_VERSION = '2'` and only reads the matching `"2"` JSON value.

**3. Bump the frontend version constant:**

```typescript
// dispatch.constants.ts
export const DISPATCH_CONFIG_VERSION = '2';
```

**4. Add V2 JSON values to the artifacts on branch 570:**

- Add a second `Dispatch Config Json` value to each existing artifact
- Set its `"version"` field to `"2"` and give it the V2-shaped JSON
- Do NOT delete or modify the existing `"1"` value — release still reads it

**5. Verify both consumers work:**

- Nightly: loads V2 artifacts, renders correctly
- Release: still loads V1 artifacts, unaffected

**6. After release merges nightly's code:**

- Release now has `DISPATCH_CONFIG_VERSION = '2'` and the normalizer
- Both release and nightly read the `"2"` JSON value
- The `"1"` JSON value is now dead weight — remove it from each artifact when convenient

### Common pitfalls

- **Don't modify the V1 JSON value after release ships.** Release's code expects V1 shape. Changing the `"1"` value on an artifact breaks release.
- **Don't bump `DISPATCH_CONFIG_VERSION` without adding the matching JSON value first.** The frontend will find no matching-version config and show an empty page.
- **Don't forget the normalizer even though you're using version filtering.** It's the fallback for edge cases where only a V1 value is present (e.g., during testing).
- **Don't remove the V1 type definition from `dispatch.types.ts`.** Keep it frozen for reference and for the normalizer's type safety.

## Routes

```
/ple/dispatch                  → Index page (lists all pages, auto-redirects if only one)
/ple/dispatch/:pageKey         → Specific page (first tab, auto-redirects to :pageKey/:firstTabKey)
/ple/dispatch/:pageKey/:tab    → Specific tab within a page
```

The `pageKey` is the artifact name slugified (e.g., "Safety Publishing" → `safety-publishing`). The `slugify()` function lowercases the name and replaces non-alphanumeric characters with dashes.

## Page Behaviors

### Index page (`dispatch-index.component.ts`)

- Fetches all matching-version configs and lists them as outlined buttons with an `open_in_new` icon
- Each button's tooltip shows the page's tab labels (e.g., "Get Attribute Types")
- If only one page exists, auto-redirects to it without showing the index
- Help button registers and opens the dispatch help topic

### Page component (`dispatch.component.ts`)

- Toolbar row: page title + home icon (back to index) + menu icon (dropdown listing all pages for cross-navigation)
- Toolbar has a bottom border with subtle drop shadow separating it from content
- On initial load without a `:tab` param, auto-redirects to include the first tab's key in the URL
- Tab changes update the URL path segment (not query params) via `replaceUrl`
- Home and cross-navigation links use `queryParamsHandling="preserve"` so the branch query params (`branchType`, `branchId`) survive navigation between the index and pages
- "Dispatch page not found" empty state shown if the pageKey doesn't match any config

### URL design

- Tab state lives in the route **path** (`/ple/dispatch/publishing/attributes`), not query params
- This prevents `tab` from leaking to other pages via the navigator's `queryParamsHandling: 'merge'`
- Branch selection lives in the query params (`branchType`/`branchId`), written by `BranchRoutedUIService`. Navigation within Dispatch (home link, page menu, index links, single-page auto-redirect) preserves these params so the selected branch carries across pages
- URLs are shareable and bookmarkable

## Configuration Schema (JSON)

The `Dispatch Config Json` attribute contains:

```typescript
type DispatchConfig = {
  version: '1';          // Required — matched against DISPATCH_CONFIG_VERSION
  title: string;         // Page heading
  tabs: DispatchTabConfig[];
};

type DispatchTabConfig = {
  key: string;             // Unique tab identifier (used in route path)
  label: string;           // Tab header text
  description: string;     // Shown below the tab header
  instructions: string;    // Markdown string rendered as HTML
  dropdowns: TabDropdown[];
  checkboxes: TabCheckbox[];
  fileInputs?: TabFileInput[];
  targetApi: TargetApi;
  artifact?: string;       // Artifact ID for URL template substitution
  downloadFileName?: string;
};
```

**V1 is frozen.** The V1 shape is permanently defined by:
- Type: `DispatchConfigV1` in `dispatch.types.ts`
- Reference example: `playwright/specs/dispatch/data/AttributeTypesExplorer.json`

Do not modify the V1 type after release. If you need a different shape, create V2.

### Example configs (test data)

The Playwright suite ships several real, working config JSONs in
`playwright/specs/dispatch/data/`. They double as copy-paste examples for every
feature the engine supports. The setup spec (`dispatch-demo-init.e2e-spec.ts`)
creates three `DispatchConfig` artifacts from them on branch 570.

| File | Artifact / page | Exercises |
|---|---|---|
| `AttributeTypesExplorer.json` | "Publishing" → `Attribute Types Explorer` | Minimal page: `branchSelector` + API-fetched `artifactType` dropdown (`required`, `dependsOn: [branch]`), one optional file input, `GET` targetApi, inline result dialog (no `downloadFileName`) |
| `AttributeTypesExplorerDownload.json` | "Reports" → `Attribute Types Explorer - Download` | Same shape as above but with `downloadFileName` set, so the result dialog shows a Download button |
| `AttributeTypesExplorerDispatchFeatures.json` | "Features" → `Attribute Types Explorer - Features` | Feature-coverage page: `viewSelector` and `emailSelector` components, a static-options dropdown (`options`, non-required so it shows the `(none)` option), two `checkboxes` (one `default: true`), a **required** file input and a **multiple** file input |
| `DeprecatedJson.json` | second JSON value on the "Publishing" artifact, `"version": "0"` | Version filtering: this value is intentionally out-of-version and must **not** render, proving the multi-valued attribute filter works |

The artifacts and their `/orcs/txs` creation payload (Names, multi-valued JSON
attribute values, and hierarchy relations) are built inline in
`dispatch-demo-init.e2e-spec.ts` — that spec is the authoritative example of how
to seed DispatchConfig artifacts.

Every field in the [Configuration Schema](#configuration-schema-json) above
appears in at least one of these files, so they collectively serve as a full
worked example set.

### Dropdowns

```typescript
type TabDropdown = {
  key: string;
  label: string;
  required?: boolean;
  options?: DropdownOption[];
  contentApi?: TargetApi;
  component?: string;
  dependsOn?: string[];
};
```

Populated via: static options, API-fetched, or registered component.

### Checkboxes

```typescript
type TabCheckbox = { key: string; label: string; default?: boolean; };
```

### File Inputs

```typescript
type TabFileInput = {
  key: string;
  label: string;
  accept: string;
  required?: boolean;
  multiple?: boolean;
  contentType?: string;  // If set, sends file as raw body with this Content-Type
};
```

### Target API

```typescript
type TargetApi = { method: 'GET' | 'POST'; url: string; button?: string; };
```

URL placeholders (`{key}`) are substituted from dropdown keys, component values, and branch/artifact IDs.

## Registered Components

| Name | Description |
|------|-------------|
| `branchSelector` | Branch picker. Sets branch in UiService. Emits branch ID. |
| `viewSelector` | Applicability view autocomplete. Emits view ID. |
| `emailSelector` | Email selector loading active users. Emits comma-separated emails. |

## Adding a New Dispatch Page

1. Create a new `DispatchConfig` artifact on branch 570
2. Set `Name` to a descriptive name (e.g., "Safety Publishing")
3. Add a `Dispatch Config Json` value whose `"version"` field is `DISPATCH_CONFIG_VERSION` (currently `"1"`)
4. The page appears automatically at `/ple/dispatch/<slugified-name>`

No code changes needed.

## HTTP Execution Modes

| Condition | Method | Body |
|-----------|--------|------|
| `targetApi.method === 'GET'` | GET | Query params from form |
| `POST` without files | POST | JSON body |
| `POST` with files, no `contentType` | POST | `multipart/form-data` |
| `POST` with files + `contentType` | POST | Raw file body |

On a request failure, `DispatchHttpService` sets `uiService.ErrorText` and re-throws (it does not swallow the error into an empty success). The tab's subscription handles the `error` callback by clearing the publishing spinner and **not** opening the result dialog, so a failed request is never mistaken for a successful empty response. The result dialog only opens on a genuine successful response.

## Development

```bash
cd web/apps/osee
npx ng serve --configuration demo_local_debug
```

The proxy config at `src/environments/proxy.conf.json` routes API calls to the backend.
