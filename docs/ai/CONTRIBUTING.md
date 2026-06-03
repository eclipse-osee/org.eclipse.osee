# Architecture Documentation Practice

When making significant changes to a feature area (new patterns, architectural decisions, performance optimizations, API changes, or bug-prone areas), update or create a documentation file in this `docs/ai/` directory.

## Trust, but verify

These docs are a starting point, not the whole story. Always verify claims against the actual source code before relying on them. Code may have evolved since the doc was last updated. If you find a discrepancy, update the doc to match reality.

## Folder organization

Docs are organized to mirror the project's plugin/module hierarchy:

```
docs/ai/<subsystem>/<area>/<feature-name>.md
```

**Rules:**
- Max depth: 3 levels under `docs/ai/`
- Use lowercase kebab-case for all folder and file names
- Folder names should match how developers think about the subsystem/area
- If a level accumulates 3+ docs, group them into a subfolder
- Don't pre-create empty folders — let the structure emerge as docs are added
- Create a matching Kiro steering file in `.kiro/steering/conditional/` that references the doc (see below)

## Wiring up a Kiro steering file

For each doc in `docs/ai/`, create a matching `.kiro/steering/conditional/<feature-name>.md`:

```markdown
---
inclusion: fileMatch
fileMatchPattern: "**/relevant/source/path/**"
---

#[[file:docs/ai/<subsystem>/<area>/<feature-name>.md]]
```

This keeps conditional (file-match) steering files separated from always-included ones for easier navigation.

## When to create or update docs

- New feature area with non-obvious patterns
- Performance-sensitive code where the "why" matters
- Areas where past mistakes have been made (API misuse, threading bugs, etc.)
- Complex integrations spanning multiple plugins

## What to document

1. **Key files** involved in the feature
2. **Data flow** and architectural patterns
3. **Important constraints** — things that don't work or will break if attempted
4. **Performance design** — why things are done a certain way
5. **Testing approach** — what demo data or setup the tests rely on

## Naming

- **Folders** map to the major plugin or subsystem name
- **Subfolders** map to logical feature areas within that subsystem
- **Files** are named after the specific feature: `<feature-name>.md`

## For small changes

Typo fixes, simple renames, one-liner bug fixes — don't create docs for these. Only document when the change introduces knowledge that would save someone (human or AI) time in the future, or when it invalidates existing documentation (in which case, update or remove the outdated doc).
