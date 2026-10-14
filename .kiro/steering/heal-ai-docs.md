---
inclusion: manual
description: "Scans docs/ai/ for missing frontmatter, missing steering pointers, and orphaned pointers — fixes everything."
---

Reconcile docs/ai/ with .kiro/steering/conditional/docs/:

1. Scan all .md files in docs/ai/ (excluding CONTRIBUTING.md).
2. Any doc without YAML frontmatter (summary, tags, fileMatch) → read it, infer appropriate values, add the frontmatter.
3. Any doc with frontmatter but no matching pointer in .kiro/steering/conditional/docs/ → create the pointer. Pointer filename = doc filename.
4. Any pointer in .kiro/steering/conditional/docs/ referencing a docs/ai/ file that no longer exists → delete the pointer.
5. If a doc is in the wrong subfolder (not matching its subsystem), move it to the correct path and update its pointer.

Report what was fixed.
