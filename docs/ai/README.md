# AI Architecture Documentation

This directory contains architecture and design documentation intended for AI coding assistants (Kiro, Codex, Cursor, Copilot, etc.) and human developers.

## Folder Structure

Docs are organized to mirror the project's plugin/module hierarchy:

```
docs/ai/<subsystem>/<area>/<feature-name>.md
```

- Max depth: 3 levels under `docs/ai/`
- Use lowercase kebab-case for all folder and file names
- Folder names should match how developers think about the subsystem/area
- If a level accumulates 3+ docs, group them into a subfolder

## How it works

- **Kiro** references these files from `.kiro/steering/conditional/` using `#[[file:docs/ai/...]]` with file-match patterns for auto-loading.
- **Codex / other tools** can be pointed directly at `docs/ai/` or specific subdirectories as context.
- **Humans** benefit too — these are concise architectural guides for anyone onboarding to a feature area.

## Adding new docs

See [CONTRIBUTING.md](./CONTRIBUTING.md) for the full process.
