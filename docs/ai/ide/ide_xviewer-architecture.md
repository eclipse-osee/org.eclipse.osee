---
summary: "XViewer architecture: the Nebula tree/table viewer framework built on JFace TreeViewer, providing customizable columns, multi-column sorting, filtering, label/content provider strategies, and persisted view customizations"
tags: [ide, xviewer, nebula, table, tree, ui]
fileMatch: "**/nebula/widgets/xviewer/**,**/*XViewer*,**/*XViewerFactory*,**/*XViewerColumn*"
---

# XViewer Architecture

XViewer is a custom tree/table viewer framework built on Eclipse JFace `TreeViewer`. It provides user-customizable columns, multi-column sorting, filtering, and persistence of view configurations.

## Bundles

- `org.eclipse.nebula.widgets.xviewer` — main viewer widget, label providers, sorter, filter, factory
- `org.eclipse.nebula.widgets.xviewer.core` — core model classes (XViewerColumn, CustomizeData, SortingData, FilterData)

## Core Components

### XViewer (extends TreeViewer)
- Main widget class
- Takes `IXViewerFactory` at construction
- Creates `CustomizeManager` for runtime customization state
- Supports optional filter bar, search bar, column filter UI
- Key methods: `setInputXViewer()`, `refreshColumnsWithPreCompute()`, `getColumnText()`
- Tracks ctrl/alt key state for sort/filter shortcuts on column headers

### XViewerColumn (core model)
- Fields: id, name, width, align, show, sortDataType, sortForward, description, multiColumnEditable
- `SortDataType` enum: String, Date, Float, Integer, Long, Percent, Paragraph_Number
- Always copied before use (`copy()` method) to avoid corrupting original definitions
- Serializes to/from XML for persistence
- Has `preComputedValueMap` for caching pre-computed column values
- Equality based on `id` field

### Label/Content Providers (3 strategies)

1. **XViewerLabelProvider** (abstract) — subclass overrides `getColumnText(element, xCol, index)`. Standard approach.
2. **IXViewerValueColumn** — column itself provides text/image/colors. Column IS the provider.
3. **IXViewerPreComputedColumn** — values computed in background before viewer loads, cached in `preComputedValueMap`.

Dispatch priority in `XViewerLabelProvider.getColumnText()`:
```
PreComputedColumn → ValueColumn → abstract getColumnText()
```

### Sorting (XViewerSorter)
- Extends `ViewerSorter`
- Click column header → sort by that column
- Ctrl+click → add as secondary sort column
- Click same column again → reverse direction
- Recursive multi-column sort via `compare(viewer, o1, o2, sortXColIndex)`
- Uses `getBackingData()` from label provider for type-aware comparisons

### Filtering (XViewerTextFilter)
- Extends `ViewerFilter`
- Global text filter (regex or literal, case-insensitive) — matches any visible column
- Per-column text filters with negation (`!` prefix)
- Per-column date range filters (Equals, Before, After, Between)
- Tree-aware: maintains `parentMatches` set to show full paths to matching items

### Factory (IXViewerFactory / XViewerFactory)
- `registerColumns()` — defines available columns
- `getDefaultTableCustomizeData()` — initial column layout
- `createNewXSorter()` — provides the sorter
- `getXViewerCustomizations()` — persistence strategy for saved views
- `getNamespace()` — unique ID for this viewer's customizations
- UI flags: `isFilterUiAvailable()`, `isSearchUiAvailable()`, `isHeaderBarAvailable()`
- `getCustomizeDialog()` — returns the customization dialog

### Customization (CustomizeManager + CustomizeData)
- `CustomizeData` stores complete view config: columns visible/order/widths, sorting, filters
- `CustomizeManager.loadCustomization()` applies it: disposes columns, creates new TreeColumns, wires sort listeners
- `IXViewerCustomizations` interface: save/load/delete/setDefault for persistence
- Users can save named views (personal or shared) and set defaults

## Data Flow

```
IXViewerFactory ──defines──> columns, sorter, customizations, namespace
      │
      ▼
XViewer (TreeViewer) ──creates──> CustomizeManager
      │                                    │
      │                          loads CustomizeData
      │                          creates TreeColumns
      │                          wires sort listeners
      │
      ├── XViewerLabelProvider ──provides──> cell text/images/colors
      ├── XViewerSorter ──sorts──> using SortDataType + column header clicks
      └── XViewerTextFilter ──filters──> global text + per-column + date ranges
```

## Key Interfaces

| Interface | Purpose |
|-----------|---------|
| `IXViewerFactory` | Configures viewer at creation |
| `IXViewerLabelProvider` | Provides cell text and backing data |
| `IXViewerValueColumn` | Column self-provides its values |
| `IXViewerPreComputedColumn` | Background pre-computation of values |
| `IXViewerCustomizations` | Persistence of saved customizations |

## Creating a New XViewer

1. Create a factory extending `XViewerFactory`, register columns in constructor
2. Create the `XViewer` widget passing the factory
3. Set a content provider (`ITreeContentProvider`)
4. Set a label provider extending `XViewerLabelProvider`
5. Call `setInput()` or `setInputXViewer()` (latter enables pre-computed columns)

## Column Types

- **Standard column** — plain `XViewerColumn`, text provided by label provider
- **Value column** — implements `IXViewerValueColumn`, provides its own text/colors
- **Pre-computed column** — implements `IXViewerPreComputedColumn`, cached background computation
- **Computed column** — `XViewerComputedColumn` (e.g., DaysTillToday, DiffsBetweenColumns)
