---
inclusion: fileMatch
fileMatchPattern: "plugins/**"
---

# IDE Documentation Style

How the user likes documentation written, for both HTML and Markdown. **Scope: documentation about the IDE / Eclipse
side - the `plugins/` tree and features packaged in bundles** (e.g. the review-packet HTML generators, bundle/plugin
docs). Applies to generated docs, hand-written HTML, and `.md` files in that scope. When a request conflicts with
this, follow the request and mention the deviation.

## Structure

- **Headers in Title Case.** Prefix with `NEW ` when the section describes a new capability (e.g. "NEW XXWidget
  Architecture").
- **Bullets: bold label - short description.** Lead each bullet with a short Title-Case **bold label**, then an
  en-dash/hyphen, then a concise sentence. Example: `<b>Auto-Persist Used By All</b> - every widget persists
  immediately; no separate save step.`
- **Group with sub-bullets.** Nest related items under a parent bullet when there is a parent/child relationship.
  Keep nesting shallow and meaningful.
- **Order by importance.** Lead with the headline items ("...Used By All", the big wins); push secondary concerns
  (error handling, testing, registration details) toward the end.
- **Keep prose concise.** Short sentences. Say it once. Prefer showing structure over long paragraphs.

## Callouts & notes

- **Use a highlighted "note" box** for asides, recommendations, and caveats. Indent it (left/right margin) so it
  reads as part of its section rather than a full-width banner.
- **Review/TODO/author reminders do NOT go in the published document.** Record them in the relevant progress/steering
  file (e.g. `coderev/XWidgetsProgress.md`). "Add a note to..." means put it there, not in the doc.

## Code references & links

- **Wrap code identifiers in `<code>`** (HTML) or backticks (MD): class names, methods, options, flags.
- **Link class names, not methods or option values.** Use a curated allowlist - ask before treating a new token as a
  linkable class. Inline class mentions link to a "Supporting Information" entry (which then links to source);
  self-explanatory enums/types may link straight to copied source.
- **Prefer relative links** so a doc/packet stays portable (zippable, movable).
- **Copy referenced source into the doc/packet** (highlighted) rather than linking to a live workspace path, so links
  keep working wherever the doc is opened.

## Self-contained output (CONDITIONAL on data sensitivity)

- **When the material is or may be classified / ITAR / CUI / otherwise non-public:** the document MUST be fully
  self-contained - no external libraries, CDNs, fonts, scripts, or any network calls. Inline all CSS and any
  JavaScript (e.g. a syntax highlighter). Nothing may "phone home" or load remote resources.
- **When the material is clearly public / open-source only:** external resources (CDN libraries, web fonts, etc.) are
  acceptable if they make the doc better.
- **If unsure which case applies, ASK before adding any external/remote resource.** Default to self-contained when in
  doubt.

## Markdown specifics

- Follow `docs/ai/CONTRIBUTING.md`: frontmatter (`summary`, `tags`, `fileMatch`) on docs under `docs/ai/`, with a
  matching steering pointer. One doc, one pointer.
- Treat docs as a map, not a spec: point to authoritative source, don't duplicate it. Keep them current; fix stale
  docs when found.
