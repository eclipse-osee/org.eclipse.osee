---
summary: "Investigation record for XX widget values visibly blanking and repainting when one attribute changes in the Workflow Editor and Artifact Editor. Captures verified behavior, instrumentation evidence, attempted fixes, retained improvements, rejected hypotheses, and next diagnostic steps."
tags: [ide, swt, xwidget, xxwidget, refresh, flicker, workflow-editor, artifact-editor, debugging]
fileMatch: "**/widgets/xx/**,**/*XX*Widget*,**/WorkflowEditor*,**/WfeWorkFlowTab*,**/WfeWorkflowSection*,**/WfeHeaderComposite*,**/ArtifactEditor*"
---

# IDE SWT Refresh Problem

> **Status: Resolved.** Runtime tracing proved that same-value `Section.setExpanded(...)` calls invalidated the shared
> Eclipse Forms hierarchy. The equality guard in `WfeWorkFlowTab.setSectionExpanded(...)` eliminated the flicker.
> This document retains the evidence and the reusable debug/performance workflow.

## Problem

Editing one artifact-backed XX widget causes every hyperlink/value-style XX widget in the visible editor to appear
blank briefly and then repaint its value.

- **Workflow Editor** - Reproduced by opening a workflow, editing the Priority attribute, and saving. The effect is
  visible in both the header and workflow state sections; it is not limited to the header.
- **Artifact Editor** - The same visible symptom occurs when editing and saving an attribute.
- **Affected controls** - Primarily XX hyperlink/value widgets rendered by `XXAbstractBaseXWidget`; Description and
  similar multi-line text controls do not exhibit the same visible blank/repaint.
- **Regression window** - Started during/recently after the XWidget to XXWidget rearchitecture (roughly two weeks
  before this investigation).
- **Expected design** - A full editor refresh is intentional. One value may affect another derived/calculated value,
  so each widget receives `refresh()` and independently decides whether it needs to update. An unchanged widget must
  short-circuit without touching its UI control.

## Verified Lifecycle

### Widget Lifecycle

`XXAbstractBaseXWidget` currently follows this lifecycle:

```text
createControls(...)
  -> create controls
  -> initializeSelected()
  -> refresh()

setArtifact(artifact)
  -> XWidget.setArtifact(artifact)
  -> initializeSelected()
  -> refresh()

refresh()
  -> calculate getDisplayValue()
  -> compare against native Text/StyledText.getText()
  -> setText(...) only when different
  -> validate()
```

`initializeSelected()` is the data-loading step. It calls `setSelected(..., false)`, which clears and repopulates the
widget's `selected` collection without refreshing. `refresh()` is intended to repaint only from the already-loaded
`selected` values; it must not reload artifact data.

### Workflow Editor Fan-Out

A persisted attribute transaction emits an artifact event. For the workflow artifact:

```text
WfeArtifactEventManager.safelyProcessHandler(...)
  -> WorkflowEditor.refresh()
  -> WfeWorkFlowTab.refresh()
     -> WfeHeaderComposite.refresh()
        -> each header XWidget.refresh()
     -> each WfeWorkflowSection.refresh()
        -> each section XWidget.refresh()
     -> refreshExpandStates()
```

The fan-out is deliberate and should remain: derived/calculated widgets may need to react to another widget's value
change. The per-widget equality guard is responsible for avoiding unnecessary UI writes.

### Artifact Editor Behavior

`ArtifactEditorEventManager` calls `ArtifactEditor.refreshDirtyArtifact()` for a modified editor artifact.
`refreshDirtyArtifact()` currently calls `reloadEditor()`, which removes every page and recreates all tabs/widgets.
That is a separate, stronger dispose/recreate path and remains a likely Artifact Editor-specific flicker/performance
issue. Most instrumentation in this investigation has focused on the Workflow Editor; do not assume one final fix
will necessarily cover both editors.

## Instrumentation Evidence

`XWidget.debugXWidgets` was enabled and `XXAbstractBaseXWidget.refresh()` logs:

- every refresh call (label + WidgetId), and
- every actual `setText` call (old + new values).

### Initial Workflow Editor Load

Nearly every XX widget receives two back-to-back `refresh()` calls during initial construction. The first/second
sequence eventually produces one expected `old=[] new=[value]` UI write as the widget is initialized. Some widgets
receive a third refresh. This redundant construction-time refresh is a lifecycle/performance smell, but initial
empty-to-value population is expected and is not by itself proof of the save-time flicker.

### Priority-Only Change

A clean reproduction changed only Priority from `2` to `4`. Results:

- `XXPriorityWidget` was the **only** widget that called `setText`: `old=[2] new=[4]`.
- Approximately 65 widget `refresh()` calls still occurred across the header and workflow state sections.
- Some state/related widgets received duplicate refresh calls.
- Every unrelated widget's `getDisplayValue()` matched its current SWT control text; their `setText` equality guard
  correctly short-circuited.
- There was no `old=[value] new=[]` followed by `old=[] new=[value]` sequence.
- There was no `Create new xxArt` or `Cached xxArt` activity during the Priority-only reproduction; the artifact/cache
  was stable.

**Key conclusion:** the data model and native control text are not being cleared. Unchanged widgets do not call
`setText`, yet their values visibly blank/repaint once. Therefore the remaining Workflow Editor symptom is an SWT
paint/erase/reflow effect outside the unchanged widget's `setText` branch, not a `selected`/`getDisplayValue()` data
bug.

## Changes Tried

### Retained: Remove Forced Synchronous Paints And Blanket Per-Widget Layout

Previous `XXAbstractBaseXWidget.refresh()` behavior after a value changed:

```java
valueWidget.setText(currentValue);
valueWidget.update();
valueWidget.getParent().update();
valueWidget.getParent().getParent().layout();
```

Current retained behavior:

- `setText(...)` only when the value differs.
- No `update()` calls. SWT is allowed to coalesce paints on the next event-loop pass.
- Every actual displayed-value change gets one targeted relayout because fixed and fill/grab controls can both change
  preferred size.
- `comp.getParent().layout(new Control[] {changedValueControl})` receives the actual `Text`/`StyledText` leaf so SWT
  flushes its owning layout's cached size; managed-form widgets then reflow once to update the ScrolledForm extent.

**Observed result:** editor rendering became noticeably faster, but one page-wide visible flicker remained. This was a
valid performance/layout correction, not the complete flicker fix.

### Retained: Relayout Workflow Sections Only When Expansion Changes

Old `WfeWorkFlowTab.refreshExpandStates()` always executed:

```java
bodyComp.layout(true, true);
bodyComp.getParent().layout(true, true);
```

Current retained behavior:

- Compare each SWT `Section.isExpanded()` state before/after applying pin/current-state rules.
- Run one `bodyComp.layout(true)` only if some section actually expanded or collapsed.
- Do not perform deep recursive body + parent layouts on every ordinary attribute save.

**Observed result:** reduced multiple visible flickers to one. This is a verified improvement and remains.

### Reverted: Cache The Last Validation/Message Status

Trial: `validate()` applied `XWidgetUtility.setStatus(...)` only when the status string changed, to avoid repeatedly
removing an already-absent form message/decoration.

**Observed result:** no change; one flicker remained. The trial and its `lastAppliedStatus` field were reverted.

### Reverted: Disable `relayoutForValueChange()` Entirely

Trial: make `relayoutForValueChange()` a no-op so the changed Priority widget performed only `setText("4")`.

**Observed result:** no change; one flicker remained. Targeted value relayout is not the remaining cause. The helper
was restored.

### Not Accepted: Suppress The Full Editor Refresh

The full editor refresh is intentional: changing one widget can affect another calculation/derived value. The design
is that every widget receives `refresh()` and unchanged widgets short-circuit. Do not solve the symptom by skipping
`WorkflowEditor.refresh()` for an attribute-only transaction.

## SWT Facts Established

- **`setText(...)`** - Changes native control content and schedules paint; it does not recompute parent layout.
- **`redraw()`** - Queues paint; SWT can coalesce it with other paints.
- **`update()`** - Forces pending paint synchronously. Avoid per-widget `update()` inside editor-wide refresh loops.
- **`layout()` / `layout(true)`** - Relayout direct children; `true` flushes cached preferred sizes.
- **`layout(true, true)`** - Flushes and recursively relayouts all descendant composites; expensive and inappropriate
  for every ordinary value refresh.
- **`layout(Control[])`** - Targeted form that flushes/relayouts only the changed-control ancestor chain.
- **Visible blank does not imply empty text** - The Priority reproduction proved unrelated controls retained their
  text and never received `setText`; an ancestor/native erase and repaint can look like blank-then-populate while the
  model and control text remain unchanged.

General SWT layout guidance is in `docs/ai/ide/ide-swt-layouts.md`.

## Current Code And Diagnostic Modes

- **Master Switch** - `XWidget.debugXWidgets` gates all XWidget diagnostics. It is temporarily enabled on the working
  branch; restore the production default before merge.
- **Performance Mode** - `debugXWidgetPerformance=true` emits lightweight `XXWIDGET PERF` and `WFE REFRESH PERF`
  duration lines. Keep lifecycle/stacks/events off while timing so instrumentation does not dominate results.
- **Lifecycle Mode** - `debugXWidgetLifecycle=true` logs timestamped state and control identity at lifecycle boundaries.
- **Caller Stacks** - `debugXWidgetStacks=true` adds compact caller stacks and requires lifecycle mode.
- **SWT Events** - `debugXWidgetSwtEvents=true` traces Paint/Resize/Move/Show/Hide/Dispose/Modify and requires lifecycle
  mode. Use only for short controlled reproductions.

## Reusable Debug Workflow

1. Choose the smallest diagnostic mode that can answer the question.
2. Clear the console after the editor settles.
3. Perform exactly one controlled action.
4. For performance, filter `XXWIDGET PERF|WFE REFRESH PERF`; repeat at least three times and use the median.
5. For lifecycle behavior, filter `XXWIDGET LIFECYCLE` and include action entry through final SWT events.
6. Paste the filtered output to an AI for sequence, call-stack, paint, and timing analysis.
7. Turn expensive modes back off after diagnosis.

The corresponding code-level instructions are in `XXAbstractBaseXWidget.debugLifecycle()` Javadoc. Detailed field and
performance interpretation is in "VPN Performance Trace Mode" below.

## Remaining Follow-Up

- **Artifact Editor** - `refreshDirtyArtifact()` still calls `reloadEditor()`, disposing/recreating every page. Confirm
  and optimize separately; the WFE `setExpanded` fix does not change that path.
- **Duplicate Header Work** - custom and related header widgets can refresh more than once. Current cost is small, but
  the duplication can be cleaned up later.
- **Transaction Latency** - VPN measurements show synchronous `SkynetTransaction.execute()` on the SWT thread dominates
  Priority-save latency; instrument lower transaction/network layers if pursuing that performance issue.
- **Production Defaults** - decide which opt-in diagnostics remain, then disable the master/performance switches before
  merge.

## Verified Resolution And Pause State (2026-10-13)

**Root cause proven by runtime tracing:** `WfeWorkFlowTab.refreshExpandStates()` called
`WfeWorkflowSection.expand()` / `Section.setExpanded(true)` for current/pinned sections and
`Section.setExpanded(false)` for the others on every editor refresh, even when each Section already had that expanded
state. On Windows, same-value `Section.setExpanded(...)` still invalidated the shared Eclipse Forms hierarchy. After
the refresh loop, SWT emitted a page-wide `WM_PAINT` burst for widget composites, labels/hyperlinks, and native Text
controls. Their model values and native `getText()` values remained non-empty and unchanged, which is why the visual
blank/reappear had no corresponding `setText("")` call.

**Fix confirmed by user:** `setSectionExpanded(...)` now compares `Section.isExpanded()` with the requested state and
returns immediately when equal. It calls `expand()` / `setExpanded(false)` only for a real state transition and returns
true so the caller performs one body layout. After rebuilding, changing Priority produced no flicker.

**Retained related improvements:**
- `XXAbstractBaseXWidget.refresh()` no longer forces `update()` or blanket ancestor layouts; only actual text changes
  write the control, with targeted layout for values that can grow. This improved editor redraw speed but was not the
  final root cause.
- `refreshExpandStates()` layouts the workflow body only when some Section truly changed expansion.
- `WfeWorkflowSection.refreshStateTitle()` now guards same-value title/state color writes, reuses one custom Color,
  and disposes it. This did not eliminate the last flicker by itself, but removes unnecessary Section invalidation and
  fixes the per-refresh SWT Color leak.
- `XXAbstractBaseXWidget.setLabelFgColor()` now changes the label/hyperlink foreground instead of accidentally resetting
  the value control foreground.

**Retained debug state:** Lifecycle, caller-stack, SWT-event, and performance diagnostics remain available as
independent modes. Current benchmark settings enable only performance summaries; expensive lifecycle/stacks/events are
off. This lets the same mechanism support quick paste-to-AI analysis without distorting performance measurements.
Before merge, restore production defaults after deciding which opt-in mechanism the team wants to retain.

**Validation status:** User runtime-verified the flicker fix and VPN performance mode. Edited Java methods were parsed
and `git diff --check` passed; a final Eclipse/PDE build remains required before merge.

## VPN Performance Trace Mode

The retained diagnostics now separate lightweight timing from expensive lifecycle/paint tracing in `XWidget`:

```java
debugXWidgets             = true;  // master switch
debugXWidgetPerformance   = true;  // lightweight PERF lines
debugXWidgetLifecycle     = false; // verbose state logs; leave off for timing
debugXWidgetStacks        = false; // compact stacks; requires lifecycle mode
debugXWidgetSwtEvents     = false; // Paint/Resize/etc.; requires lifecycle mode
```

Leave only performance mode enabled while measuring. Lifecycle console output, stack construction, and SWT event
listeners significantly distort UI timings.

### Capture

1. Start the IDE through VPN and open the workflow.
2. Clear the console after initial rendering settles.
3. Change Priority exactly once.
4. Capture lines containing `XXWIDGET PERF` and `WFE REFRESH PERF`.
5. Repeat the same change at least three times (e.g. Priority 2→3→4→5) and compare median values; the first request may
   include connection/cache warm-up.

### Interpret

- `operation=transaction.execute` - database/server round trip; primary VPN-sensitive measurement.
- `operation=handleSelectedPersist EXIT` - total local mutation + transaction + listener notification.
- Difference between persist exit `epochMs` and `WFE REFRESH PERF ... START` - artifact-event dispatch delay.
- `phase=header` - all Workflow Editor header widget refresh work.
- `phase=sections` - all state-section widget refresh work.
- `phase=expandStates` - expansion equality checks; should be very small after the same-state `setExpanded` fix.
- `phase=setEditorWidgets` - editor reference reassignment.
- `phase=TOTAL` - total Workflow tab refresh time.
- `operation=refresh EXIT` / `operation=validate EXIT` - individual widget costs; use label/WidgetId to find outliers.

The monotonic `elapsedMs` and sequence number order lifecycle/performance events within the process; `epochMs` correlates
XX widget timing with Workflow Editor phase lines and other logs.

### VPN Priority Benchmark Results

Five Priority changes were captured with lifecycle/stacks/SWT events disabled and performance mode enabled.

| Measurement | Samples (ms) | Median (ms) |
|---|---|---:|
| `transaction.execute` | 2720.566, 1749.211, 1652.162, 1875.236, 2189.832 | 1875.236 |
| Total `handleSelectedPersist` | 3247.936, 1749.949, 1652.829, 1875.935, 2190.455 | 1875.935 |
| Artifact event dispatch to WFE start | 9, 21, 11, 9, 9 (epoch-ms resolution) | 9 |
| WFE header refresh | 16.302, 25.965, 16.670, 18.582, 14.887 | 16.670 |
| WFE state-section refresh | 1.350, 1.099, 0.926, 1.088, 1.188 | 1.099 |
| WFE expansion checks | 0.052, 0.014, 0.016, 0.018, 0.016 | 0.016 |
| WFE total refresh | 18.068, 27.265, 17.782, 19.919, 16.337 | 18.068 |

**Conclusion:** the synchronous `SkynetTransaction.execute()` round trip on the SWT UI thread dominates perceived
latency (about 1.65–2.72 seconds; ~1.88-second median). The full Workflow Editor widget refresh is healthy at an
~18-ms median, with state sections and the fixed same-state expansion check effectively negligible. Transaction time
is over 100x the WFE refresh and accounts for roughly 98%+ of post-selection latency over VPN.

The first sample had ~527 ms between total persist time and measured `transaction.execute`; later samples had only
~0.6–0.7 ms. Treat that as cold/lazy initialization and use warmed samples for steady-state comparisons. The header
is the only meaningful local refresh phase (about 15–26 ms) and still includes known duplicate custom/related widget
refresh work; optimize it only if pursuing small local gains, not as the VPN latency fix.
