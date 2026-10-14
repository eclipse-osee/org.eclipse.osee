---
inclusion: fileMatch
fileMatchPattern: "**/coderev/packet/**"
---

# TEMPORARY — XWidget Review Packet Editing Task

Delete this steering file after the XWidget → XXWidget migration review packet is finished and merged. It describes
an in-flight authoring task, not durable project rules.

## What this task is

Iteratively authoring the review packet generators under
`plugins/org.eclipse.osee.ats.ide.integration.tests/src/org/eclipse/osee/ats/ide/integration/tests/coderev/packet/`
(`ReportXWidgetOverview`, `ReportXWidgetChanges`, `ReportBlamChanges`, `ReportWorkDefChange`, `ReportXWidgetMapping`,
`PacketStyle`, run via `GenerateFullReviewPacket`). They emit the self-contained HTML review packet into
`~/CodeXWidgetReview/`.

## The live loop (how the user works with the AI here)

1. User requests a specific wording/structure change to a packet page.
2. AI edits only the relevant generator method (scoped edits, no broad rewrites).
3. **User** runs the generator in Eclipse (Run As → Java Application) — it regenerates and opens the page under
   `~/CodeXWidgetReview/`. The AI CANNOT run it (Eclipse PDE classpath only), but it CAN read the generated HTML in
   `~/CodeXWidgetReview/` to review output.
4. User eyeballs the page and gives the next change. Repeat.

Keep responses concise during the loop (one edit, short confirmation of what changed and where).

## Conventions established in this task

- **Section bold lead-ins are Title Case** (e.g. `<b>Same Widgets Used By All UIs</b>`); descriptive text after the
  dash stays sentence case.
- **"add a note to…" means record it in `coderev/XWidgetsProgress.md`**, NOT in the generated Overview document.
- **Review notes/flags do not go in the published Overview** — they go in `XWidgetsProgress.md`.
- **Linking class names** (reference-source mechanism): classes only, never methods or `XOption` values. Curated
  allowlist — ask the user before treating a new token as a linkable class. Inline `<code>ClassName</code>` mentions
  link to the class's **Supporting Information** anchor (`#support-<lowercase>`); the Supporting Information entry
  links to the copied source. (XOption is the one exception: links straight to its copied source.)
- All links are RELATIVE and the packet is self-contained (no external libs / network). Copied source goes under
  `reference_src/`.

## Current state

The packet is built and reviewed; work is now pre-merge cleanup. See `coderev/XWidgetsProgress.md` (read the latest
session-update sections at the end) for the live status, open items, and next steps.
