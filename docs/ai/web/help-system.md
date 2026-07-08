---
summary: "In-app contextual help system with popup window, UI highlighting, and scrollspy navigation"
tags: [web, angular, help, documentation]
fileMatch: "**/help-drawer*,**/help-popup*,**/assets/help/**"
---

# Help System

## Overview

The help system provides contextual, interactive documentation for OSEE web components. It opens in a separate browser popup window, renders markdown content with Material Icons, and can highlight UI elements in the parent window via "Show Me" buttons.

## Architecture

```
shared/components/help-drawer/
├── help-drawer.service.ts          ← Opens/closes popup, postMessage bridge, highlight state
├── help-topic-registry.service.ts  ← Simple Map registry, components self-register
├── help-anchor.directive.ts        ← [oseeHelpAnchor] applies highlight animation
├── public-api.ts / index.ts        ← Barrel exports
└── help-popup/
    ├── help-popup.component.ts     ← Popup window route component
    ├── help-popup.component.html   ← Template with section chips + markdown
    └── help-popup.component.sass   ← Scoped styles (::ng-deep for markdown/show-me)

assets/help/{topic-id}/
└── overview.md                     ← Static markdown content per topic
```

## Adding Help to a New Component

### Step 1: Register the help topic

In the component's `.ts` file, inject `HelpTopicRegistryService` and call `register()`:

```typescript
import { HelpTopicRegistryService } from '../help-drawer/help-topic-registry.service';
import { HelpAnchorDirective } from '../help-drawer/help-anchor.directive';
import { HelpButtonComponent } from '../help-drawer/help-button.component';

// In imports array:
imports: [..., HelpAnchorDirective, HelpButtonComponent],

// In class body:
private readonly helpRegistry = inject(HelpTopicRegistryService);

private readonly _registerHelp = this.helpRegistry.register({
  id: 'my-component',
  label: 'My Component',
  markdownPath: 'assets/help/my-component/overview.md',
  sections: [
    { id: 'section-one', label: 'Section One', anchorId: 'my-anchor-id' },
    { id: 'section-two', label: 'Section Two', anchorId: 'my-other-anchor' },
  ],
});
```

**Rules:**
- `id` must match the folder name under `assets/help/`
- `sections[].id` must match the h2 heading text slugified (`toLowerCase().replace(/[^a-z0-9]+/g, '-')`)
- `sections[].label` must match the h2 heading text exactly (case-insensitive match is used)
- `sections[].anchorId` is the value used in `oseeHelpAnchor="..."` in the template

### Step 2: Add help anchors to the template

Place `oseeHelpAnchor` on elements you want the "Show Me" feature to highlight:

```html
<div oseeHelpAnchor="my-anchor-id">
  <!-- This region highlights when user clicks "Show Me" for Section One -->
</div>
```

**Anchor placement guidelines:**
- Place on a visually bounded container, not on individual text elements
- Avoid placing on elements with `overflow: hidden` on their parent (outline gets clipped)
- For toolbar buttons, wrap related buttons in a `<span>` with the anchor rather than putting it on individual circular buttons
- For `mat-form-field`, place the anchor on the `<mat-form-field>` element, not the inner `<input>`

### Step 3: Add a help button

```html
<osee-help-button topicId="my-component"></osee-help-button>
```

Import `HelpButtonComponent` in the component's `imports` array. That's it — no method needed.

### Step 4: Create the markdown content

Create `web/apps/osee/src/assets/help/my-component/overview.md`.

**Content rules:**
- First line: `# Title` (renders as the popup header via title-casing the topic ID)
- Sections: use `## Heading` for each section (these become the section chips)
- Icons: use `<span class="material-icons" style="font-size:14px;vertical-align:middle">icon_name</span>` for inline Material Icons
- Sentences end with periods (per tooltip conventions: explanatory text uses sentence case with period)
- Tables use the standard markdown pipe syntax
- The `[disableSanitizer]="true"` flag is set, so raw HTML works

**Example icon in text:**
```markdown
Click the <span class="material-icons" style="font-size:14px;vertical-align:middle">save</span> button to save.
```

**Example icon in table:**
```markdown
| Icon | Action |
| :-- | :-- |
| <span class="material-icons">save</span> | Save the document. |
```

## How the Popup Works

1. `HelpDrawerService.open(topicId)` calls `window.open()` with URL `/help-popup?topic={topicId}`
2. The popup route loads `HelpPopupComponent` (renders without the app shell via `isPopupMode` in `AppComponent`)
3. Markdown is loaded from `assets/help/{topicId}/overview.md` via `ngx-markdown` `[src]`
4. After render, `onMarkdownReady()` discovers h2 headings and requests anchor IDs from the parent via `postMessage`
5. Parent's `HelpDrawerService` responds with registered section data
6. Popup merges anchor IDs and injects "Show Me" buttons on headings that have anchors
7. Clicking "Show Me" sends `postMessage` to parent → parent sets `highlightedAnchor` signal → `HelpAnchorDirective` applies CSS animation

## Scrollspy

Uses proportional mapping:
- `scrollPercent = scrollTop / (scrollHeight - clientHeight)`
- `headingPercent = heading.offsetTop / scrollHeight`
- Active section = last heading where `scrollPercent >= headingPercent - 0.03`

Click-to-scroll positions the viewport at `(headingPercent - 0.02) * maxScroll` so the heading lands in the activation zone.

## Highlight Animation

Defined in `styles.sass` (global, since the directive applies it to arbitrary elements):
- One flash (on → off → on), hold solid, fade out over 3 seconds
- `outline: 3px solid rgba(25, 118, 210, ...)` with `outline-offset: -3px`
- Auto-clears after 3.2 seconds via the directive's timeout

## Key Files to Modify

| Task | File |
| :-- | :-- |
| Add help to a component | Component `.ts` (register) + `.html` (anchors + button) |
| Write help content | `assets/help/{topic-id}/overview.md` |
| Change highlight animation | `styles.sass` (`@keyframes osee-help-pulse`) |
| Change popup styling | `help-popup.component.sass` |
| Change popup layout | `help-popup.component.html` |

## Constraints

- `window.open()` exits browser fullscreen mode (unavoidable browser security). Tooltip warns users.
- The popup is a separate Angular instance — it cannot access services/state from the parent. Communication is via `postMessage` only.
- Help content must be static markdown files in `assets/` (bundled at build time).
- Sections without a matching `oseeHelpAnchor` in the template won't get "Show Me" buttons (they still appear as navigation chips with "Scroll to section" tooltip).
