---
summary: "Artifact Editor architecture: the multi-tab Eclipse FormEditor for viewing/editing OSEE artifacts. Covers the ArtEdBaseTab tab base class, ArtEdTabHeader shared header, the Attributes/Admin-Attributes/Relations/Details tabs, the shared toolbar, auto-save behavior, event-driven reload, and WFE embedding."
tags: [ide, artifact-editor, form-editor, tabs, attributes, ui]
fileMatch: "**/artifact/editor/**,**/*ArtifactEditor*,**/*ArtEd*,**/AttributeFormPart*"
---

# Artifact Editor Architecture

## Overview

The Artifact Editor (`ArtifactEditor`) is a multi-tab Eclipse FormEditor for viewing/editing OSEE artifacts. It displays attributes, relations, details, and optionally markdown content.

## Tab Architecture

### Base Class: `ArtEdBaseTab`
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.header`

All tabs extend `ArtEdBaseTab` which provides:
- Common header via `ArtEdTabHeader` (title, image, info row, toolbar, section label)
- Standard lifecycle: `createFormContent()` → `initializeTab()` → `createTabBody()` + `createSectionToolbar()`
- `refresh()` that delegates to `tabHeader.refresh()` + `refreshTabBody()`
- Access to `getArtifact()` and `getEditor()`

**Abstract methods subclasses implement:**
```java
protected abstract String getTabName();          // Label in content area (e.g., "Attributes")
protected abstract void createTabBody(Composite parent, FormToolkit toolkit);
protected void createSectionToolbar(Composite toolbarParent) {}  // Optional
protected void refreshTabBody() {}                                // Optional
```

**Async loading override:** Tabs that need async data loading (like `ArtAttrTab`) override `initializeTab()` to show a loading spinner, kick off a background job, then call `createBody()` manually when data is ready.

### Common Header: `ArtEdTabHeader`
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.header`

Creates identical header for all tabs:
1. **Title + Image** — artifact name + artifact type image (via `ArtifactImageManager`)
2. **Info row** (4-column): View Applicability (left) | Branch/Type (center) | ID (right) | Copy ID hyperlink (far right)
3. **Form toolbar** — shared actions from `ArtifactEditorActionBarContributor` (Delete, Outline, History, Reveal Branch, Access Control, Copy URL, Open in Browser, Refresh)
4. **Content area** — 3-border nested structure:
   - Outer border: contains label row + separator + middle border
   - Middle border: white background, contains inner border
   - Inner border: white background — **this is what tabs get as their parent composite**
5. **Label row** — section label (bold) on left, optional section-specific toolbar on right

### Tabs

| Tab | Class | Package | Section Toolbar |
|-----|-------|---------|----------------|
| Attributes | `ArtAttrTab` | `tab.art` | Add/Delete Attribute Type |
| Attributes (Admin) | `ArtEdAttrTab` | `tab.attr` | Add/Edit/Delete Attribute + Customize |
| Relations | `ArtEdRelationsTab` | `tab.rel` | None |
| Details | `ArtEdDetailsTab` | `tab.details` | None |

### Attributes Tab (`ArtAttrTab`)
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.art`

- Uses async loading pattern (overrides `initializeTab()`)
- Loads artifact in background, then creates header + `AttributeFormPart`
- `AttributeFormPart` — `AbstractFormPart` that creates XX widgets for each attribute type
- `AttributeActionContribution` — provides Add/Delete Attribute Type toolbar actions
- `AttributeTypeEditDisplay` — dialog helper for add/remove attribute type selection
- Also supports embedding in WFE via `createFormContentShared(managedForm)` (creates XViewer body without header)

### Attributes Admin Tab (`ArtEdAttrTab`)
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr`

- Shows raw attributes in an XViewer table (`ArtEdAttrXViewer`)
- Only visible to OSEE admins
- Disables outer form scrolling (XViewer handles its own)
- Section toolbar: Add/Edit/Delete individual attributes + Customize XViewer columns
- Also embeddable in WFE via `(FormEditor, Artifact)` constructor + `createFormContentShared()`

### Relations Tab (`ArtEdRelationsTab`)
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.rel`

- Drag-drop label (grey background, bordered) for dragging the artifact
- `RelationsComposite` for the relation tree
- Listens to artifact events for auto-refresh

### Details Tab (`ArtEdDetailsTab`)
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.details`

- Browser widget showing artifact details as HTML (key/value pairs)
- Operations section with Access Control button

## Shared Toolbar (`ArtifactEditorActionBarContributor`)
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor`

All tabs get the same form toolbar via `ArtEdTabHeader`. Actions:
- Open With (dropdown)
- Delete Artifact
- Open Outline
- Open History
- Reveal in Explorer
- Reveal Branch
- Access Control
- Copy Artifact URL
- Open in Browser
- Dirty Report / Validity Report / Toggle Widget Debug (admin only)
- Refresh (calls `artEditor.refresh()` which refreshes ALL tabs)

## Editor Class: `ArtifactEditor`
**Package:** `org.eclipse.osee.framework.ui.skynet.artifact.editor`

- Extends `AbstractEventArtifactEditor` (handles artifact events)
- Creates tabs in `performLoad()`: Attributes → Markdown (if applicable) → Admin → Relations → Details
- Admin tab only added if current user is OSEE admin
- `refresh()` method refreshes all tabs
- `getAttributeFormPart()` returns the `AttributeFormPart` from the Attributes tab
- `getActionBarContributor()` is a singleton per editor instance

## Key Patterns

### No `var` keyword — always use concrete types
### No grey backgrounds — except Relations drag-drop label
### 3-border content area — provided by `ArtEdTabHeader` for all tabs
### Section toolbar after body creation — ensures widgets exist before toolbar references them
### Form toolbar via `form.reflow(true)` — ensures toolbar renders properly
### Auto-save — no dirty state; each action persists immediately; `isDirty()` always returns false
### Event-driven reload — `ArtifactEvent`/`ArtifactTopicEvent` triggers full `reloadEditor()` (tear down + rebuild all tabs)

## File Structure
```
artifact/editor/
├── header/
│   ├── ArtEdBaseTab.java          (abstract base for all tabs)
│   └── ArtEdTabHeader.java        (shared header creation)
├── tab/
│   ├── art/                        (main Attributes tab)
│   │   ├── ArtAttrTab.java
│   │   ├── AttributeFormPart.java
│   │   ├── AttributeActionContribution.java
│   │   └── AttributeTypeLabelProvider.java
│   ├── attr/                       (Admin Attributes tab)
│   │   ├── ArtEdAttrTab.java
│   │   ├── ArtEdAttrXViewer.java
│   │   ├── ArtEdAttrContentProvider.java
│   │   ├── ArtEdAttrLabelProvider.java
│   │   └── action/
│   │       ├── AddAttributeAction.java
│   │       ├── EditAttributeAction.java
│   │       └── DeleteAttributeAction.java
│   ├── rel/                        (Relations tab)
│   │   └── ArtEdRelationsTab.java
│   └── details/                    (Details tab)
│       └── ArtEdDetailsTab.java
├── util/
│   └── AttributeTypeUtil.java
├── ArtifactEditor.java
├── ArtifactEditorActionBarContributor.java
├── ArtifactEditorInput.java
└── ArtifactEditorEventManager.java
```

## WFE (Workflow Editor) Integration

The `WfeAttributesTab` in ATS embeds `ArtEdAttrTab` for showing admin attributes on workflow items:
- Uses `ArtEdAttrTab(FormEditor, Artifact)` constructor
- Calls `createFormContentShared(managedForm)` — creates XViewer body without the common header
- WFE provides its own title bar and toolbar via `WfeAbstractTab`
