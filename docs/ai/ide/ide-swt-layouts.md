---
summary: "How SWT layout, redraw/update, and content changes actually interact in the OSEE IDE widgets — the layout() booleans, when a value change needs a relayout, why forced update()/blanket layout() in a refresh loop causes flicker, and the correct targeted-relayout pattern used by XXAbstractBaseXWidget."
tags: [ide, swt, layout, widgets, refresh, flicker, xxwidget, performance]
fileMatch: "**/widgets/xx/**,**/*XX*Widget*,**/XWidget.java,**/XXAbstractBaseXWidget.java"
---

# IDE SWT Layouts

Hard-won reference for how SWT geometry (`layout`) and painting (`redraw`/`update`)
interact with content changes (`setText`) in the OSEE IDE widget layer. Written
after diagnosing a page-wide value flicker in the Artifact Editor and Workflow
Editor. **Read this before "fixing" any sizing or paint issue in a widget's
`refresh()` — the obvious fix (`update()` + a blanket `layout()`) is exactly the
bug.**

## The Three Mechanisms (Do Not Conflate)

SWT separates *content*, *geometry*, and *painting*. They are different passes.

- **Content change** - `control.setText(...)`, `setImage(...)`, etc. Updates the
  native widget's content and marks its area damaged so SWT paints it on the next
  event-loop pass. It does **not** recompute layout - the control keeps the bounds
  its parent's layout last gave it. If the new content is larger it clips/scrolls;
  the parent is **not** told "I need more room."
- **Geometry pass** - `Composite.layout(...)`. Runs the parent's `Layout` (e.g.
  `GridLayout`), recomputes child bounds, and calls `setBounds` on children. This
  is the only thing that makes a cell grow/shrink.
- **Paint pass** - `redraw()` / `update()`. `redraw()` marks a control damaged so a
  paint event is **queued**. `update()` forces all *pending* paints for that control
  to run **right now, synchronously**, before returning.

## `layout(...)` and Its Booleans

- **`layout()`** = `layout(true)`. Lays out **direct** children. `true` =
  **flush the size cache** (discard cached preferred sizes and recompute). `false` =
  reuse cached sizes, just reposition.
- **`layout(boolean changed)`** - `changed=true` flushes cached computed sizes for
  the direct children. Needed when a child's *preferred size* changed (text got
  longer and the control should grow).
- **`layout(Control[] changed)`** - the **surgical form**. Lays out and flushes the
  cache **only along the ancestor chain of the given controls**. Use this when
  exactly one widget changed size and you don't want to relayout the whole composite.
- **`layout(boolean changed, boolean all)`** - `all=true` recurses into **all**
  descendant composites, not just direct children. The expensive, deep one.

So `getParent().getParent().layout()` is `layout(true, false)` on the grandparent:
a broad, cache-flushing relayout of all the grandparent's children, triggered by one
widget.

## `redraw()` vs `update()` — the Flicker Amplifier

Normally SWT **coalesces** damage and paints once per event-loop turn. `update()`
forces an immediate synchronous repaint. Calling `update()` in a loop (e.g. a
refresh that runs over every widget) forces many intermediate synchronous paints, so
the eye sees intermediate states - blank, partial, filled. **Never force `update()`
in a refresh loop.** Let the event loop batch the paint.

## When Does a Value Change Need a Relayout?

A value control's preferred size can change whenever its displayed text changes, including a non-fill control that
sizes to content. Therefore every **actual displayed-value change** gets one targeted relayout. The safety boundary is
not the fill option; it is the equality check before `setText`:

- Unchanged display value &rarr; no SWT write and no layout.
- Changed display value &rarr; `setText`, then one targeted local `layout(Control[])`; in a managed form, one
  `reflow(true)` propagates the preferred size into the ScrolledForm extent/scrollbars.
- Structural change (rows/sections added or removed) &rarr; the owning container performs one local layout/form reflow,
  as `WfeAttachmentsComposite` does.

"Don't grow on value change" is **wrong**. Growing is legitimate and idiomatic. The rule is: **detect a real change,
then relayout narrowly and once.**

## The Broad-Refresh Anti-Pattern

```java
// WRONG - do not do this after one widget value change
valueWidget.setText(currentValue);
valueWidget.update();                           // forced synchronous paint
valueWidget.getParent().update();               // forced synchronous paint
valueWidget.getParent().getParent().layout();   // blanket grandparent relayout (layout(true))
```

This pattern was not the final WFE flicker root cause, but it made redraw slower and more fragile:

1. **Blanket relayout** - relays out all grandparent children for one changed widget.
2. **Forced synchronous paints** - defeats SWT paint coalescing and exposes intermediate rendering.
3. **Wrong scope** - the changed widget knows its own control; use targeted `layout(Control[])`, not guessed parent
   depth.

### Verified WFE Flicker Cause

The editor-wide refresh fan-out is intentional: dependent/calculated widgets must inspect their values. The actual
flicker came from `refreshExpandStates()` calling `Section.setExpanded(true/false)` on every state section even when its
expanded state was already equal. On Windows, same-value calls invalidated the shared Eclipse Forms hierarchy and
caused a page-wide `WM_PAINT` burst. Guarding `Section.isExpanded() == requestedState` eliminated the flicker.

The key rule is broader than layout: **do not call SWT mutators on unchanged state**. Text widgets guard text equality;
section/container widgets must guard expansion, rows, colors, and other structural state.

## The Correct Pattern

```java
if (!currentValue.equals(valueWidget.getText())) {
   valueWidget.setText(currentValue);                              // actual content change
   comp.getParent().layout(new Control[] {valueWidget});           // flush changed leaf and ancestor path
   if (isInForm() && isFormReady()) {
      getManagedForm().reflow(true);                               // update ScrolledForm extent/scrollbars
   }
}
```

Rules this encodes:

- **No-op means no SWT mutation.** Equality is checked before `setText` or layout.
- **Every real value change can resize.** Fixed and fill/grab controls get one targeted local preferred-size
  recomputation and, in a managed form, one reflow to update the outer scroll extent.
- **Target the changed leaf.** Pass the actual `Text`/`StyledText` to `layout(Control[])` so SWT flushes its owning
  layout's cached size and lays out the ancestor path to the receiver.
- **Managed-form propagation.** A local layout alone does not update `ScrolledForm` minimum size/scrollbars;
  `reflow(true)` is required after the actual value change.
- **Structural widgets own structural reflow.** Add/remove/expand logic reports whether structure changed; the owning
  container then performs one local layout and, when needed, one managed-form reflow.

## Troubleshooting A Value That Looks Clipped/Wrong After An Edit

Do not re-add `update()` or a blanket parent layout. Check instead:

1. Did the displayed value actually change and reach the guarded targeted-layout path?
2. Does the control's `GridData` permit the desired fill/grab behavior after preferred size is recomputed?
3. Did a container add/remove child controls? If so, it must report that structural change and perform one local
   layout/form reflow.
4. Is some code calling an SWT setter with an unchanged value? Same-value setters such as `Section.setExpanded()` can
   still invalidate an ancestor and repaint the page.

## Related Known Issue

The Artifact Editor's `refreshDirtyArtifact()` &rarr; `reloadEditor()` disposes and
recreates **all** pages/widgets on save (full teardown) - a separate, heavier
flicker/perf source than the per-widget refresh path. If the Artifact Editor still
blanks after the refresh fix, that teardown-on-own-save is the next thing to address
(it should not full-reload for the editor's own in-place attribute save).

## Authoritative Source

`XXAbstractBaseXWidget.refresh()` and `relayoutForValueChange()` carry the detailed
Javadoc rationale. This doc is the general SWT map; the widget Javadoc is the specific
contract. Keep them consistent.
