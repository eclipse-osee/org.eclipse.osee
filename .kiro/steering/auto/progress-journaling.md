---
inclusion: always
---

# Progress Journaling

Context can run out at any time. Keep a durable, resumable record of multi-step work so a future session (or a
compacted one) can pick up without losing intent.

## Keep a progress doc current

- For any non-trivial, multi-step task, maintain a progress/notes file near the work (e.g.
  `.../coderev/XWidgetsProgress.md` for the XWidget migration / code-quality / review-packet work). One per workstream.
- Update it as you go - after finishing a step, when blocked, or when a decision is made - not only when asked. The
  user should rarely have to say "store progress."
- Especially capture: what was done, what is IN PROGRESS and whether it compiles/works, the next steps, open
  bugs/decisions with enough detail to act on, and verified facts (paths, names, numbers) so they are not re-derived.
- Record decisions and the reasoning behind them, so a later session does not re-litigate or reverse them.

## Temporary vs durable

- For a specific in-flight task, a TEMPORARY task steering file (conditional `fileMatch`) is fine; mark it clearly as
  delete-after-done and point it at the live progress doc.
- Keep author reminders / TODOs out of generated/published artifacts; they belong in the progress doc.

## On resume

- At the start of a session that continues prior work, read the relevant progress doc first and reconcile it against
  the current file state before acting.
