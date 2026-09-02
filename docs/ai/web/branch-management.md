---
summary: "Branch management sidebar panel in the artifact explorer: terminology, actions, and design rules"
tags: [web, artifact-explorer, branch, working, baseline]
fileMatch: "**/branch-management/**,**/branch-picker/**"
---

# Branch Management

## Terminology

All user-facing text, tooltips, aria labels, and code identifiers within the branch management feature must use **working** and **baseline** terminology exclusively.

- A **baseline** branch is the stable, shared branch that working branches are created from and committed to.
- A **working** branch is a user's editable branch created from a baseline.

**Do not use:** parent, child, source, destination, or any other relational terms when referring to branches in user-facing contexts. Internal service APIs (e.g., `parentBranch`, `updateFromParent`) may retain their names since they are shared across the codebase, but local variables and comments should use baseline/working.

## Location

`web/apps/osee/src/app/ple/artifact-explorer/lib/components/hierarchy/branch-management/`

## Actions

| Button | Tooltip | Condition |
|---|---|---|
| Create Working Branch | Create a new working branch from this baseline | Shown when no ATS workflow exists |
| Commit to Baseline | Commit this working branch to its baseline | Disabled if current branch is not a working branch |
| Update from Baseline | Sync this working branch with the latest baseline changes | Disabled if current branch is a baseline |
| Change Report | View changes made on this branch | Always shown when a branch is selected |
| Open Workflow | Open the team workflow for this branch | Shown for working branches with an ATS workflow |
| Create Action | Create a new action for this branch | Shown for baselines with an ATS category |

## Design

- Buttons are rendered as a vertical list using `mat-button` (no outline).
- Each button is full-width, left-aligned, with an icon followed by text.
- Text uses foreground color (`tw-text-foreground-text`).
- Disabled buttons use `disabled:tw-opacity-50`.
- Tooltips explain why a button is disabled when applicable.
- The panel shows "Select a branch to see management options." when no branch is selected.
