---
summary: "Design of the server-side HTML renderer for ATS workflows (ats.rest) that produces pages resembling the WfeWorkflowSection in the Workflow Editor. Covers the endpoint, data flow, WidgetId-to-HTML mapping, HTML/CSS structure, performance considerations, and the implementation plan. No client dependencies."
tags: [ats, rest, workflow, html-renderer, server, design]
fileMatch: "**/ats.rest/**,**/*WorkflowHtml*,**/*WorkflowWidgetHtmlRenderer*,**/AtsActionUiEndpoint*"
---

# Workflow HTML Renderer Design

Server-side HTML renderer for ATS workflows that produces pages resembling the WfeWorkflowSection in the Workflow Editor.

## Architecture

- **Location**: `org.eclipse.osee.ats.rest.internal.util.WorkflowHtmlPage` (replaces/extends ActionPage pattern)
- **Endpoint**: `AtsActionUiEndpointApi` — new endpoint `@Path("{id}/workflow")` returning `MediaType.TEXT_HTML`
- **No client dependencies** — only uses `ats.api`, `framework.core`, `framework.jdk.core`, `orcs.rest.model`

## Data Flow

1. REST endpoint receives workflow ID
2. Load `IAtsWorkItem` and its `WorkDefinition`
3. Render header (from WorkDefinition.getHeaderDef() + work item metadata)
4. For each visited state (ordered by ordinal, descending):
   - Render collapsible state section header
   - Walk `StateDefinition.getLayoutItems()` recursively
   - For each `WidgetDef`: render appropriate HTML form element with current value from `atsApi.getAttributeResolver()`
   - For each `CompositeLayoutItem`: wrap children in CSS grid/flexbox with N columns
5. Return complete HTML document

## Widget Rendering Strategy

Each `WidgetDef` maps to an HTML widget renderer based on its `WidgetId`:

| WidgetId Pattern | HTML Element | Notes |
|---|---|---|
| XXTextWidget, XXStringSTextHEditWidget | `<input type="text">` or `<textarea>` | Based on MULTI_LINE option |
| XXCheckBoxWidget, XXBooleanWidget | `<input type="checkbox">` | |
| XXDateSelWidget | `<input type="date">` | |
| XXBranchSelWidget | `<select>` or label | Read-only for now |
| XXListDropWidget, XXStringsWidget | `<select>` | With options from selectable |
| XXLabelWidget, XXLabelValueWidget | `<span>` | Display only |
| XXIntegerSTextWidget, XXDoubleSTextWidget | `<input type="number">` | |
| XXButtonPushWidget | `<button>` | Disabled for now |
| Custom ATS widgets | `<span>` with value | Fallback to showing attribute value |

## Design for Future Editability

- All rendered form elements include `name="attrTypeId"` for form submission
- Widget containers have `data-widget-id` and `data-attr-type` attributes
- State sections wrapped in `<form>` elements
- JavaScript submit handler TBD (will POST to existing `/ats/action/attr` endpoint)

## HTML Structure

```html
<!DOCTYPE html>
<html>
<head>
  <title>Workflow - {atsId} - {title}</title>
  <style>/* WFE-like styling */</style>
</head>
<body>
  <div class="wfe-header">
    <!-- metadata grid: title, state, team, IDs, assignees, etc. -->
  </div>
  
  <div class="wfe-states">
    <details class="wfe-state" open> <!-- current state expanded -->
      <summary class="wfe-state-title">Endorse - State Completed 06/18/2026</summary>
      <div class="wfe-state-body">
        <!-- widgets rendered from layout items -->
        <div class="wfe-widget" data-widget-id="XXTextWidget" data-attr-type="123">
          <label>Description:</label>
          <textarea readonly>value</textarea>
        </div>
      </div>
    </details>
    
    <details class="wfe-state"> <!-- non-current states collapsed -->
      <summary>Analyze - State Completed</summary>
      ...
    </details>
  </div>
</body>
</html>
```

## CSS Approach

- Tailwind-like utility classes for consistency with web app
- CSS variables for theming (--wfe-primary, --wfe-header-bg, etc.)
- `<details>/<summary>` for native collapsible sections (no JS needed)
- CSS Grid for composite layouts (columns)

## Performance Considerations

- Single query for all attributes (batch load)
- Template caching for CSS/JS includes
- No per-widget database calls — resolve all values upfront into a Map
- Minimal DOM — avoid nested tables, use modern CSS layout

## Implementation Plan

1. Create `WorkflowHtmlPage` in ats.rest (similar to ActionPage but modern HTML)
2. Create `WorkflowWidgetHtmlRenderer` — maps WidgetId to HTML output
3. Add endpoint in `AtsActionUiEndpointApi` / `AtsActionUiEndpointImpl`
4. Add CSS file in `OSEE-INF/templates/workflow.css`
5. Add template in `OSEE-INF/templates/workflow.html`

## TBD Items

- Header widget rendering (complex/dynamic — skip for v1, use metadata table)
- Transition UI (existing transition.html can be reused)
- Role/Defect table widgets (copy from ActionPage for now)
- Real-time validation (future — requires WebSocket or polling)
- Edit mode toggle (future)
