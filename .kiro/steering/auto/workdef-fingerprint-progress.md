---
inclusion: always
---

# WorkDef Fingerprint Progress

## Goal

Create an automated regression safety net for Work Definition widget resolution. A renderer change must not silently
alter the resolved `XWidgetData` / `XOptionHandler` fingerprint for a previously approved `(WorkDef, artifact type)`
context.

## Decisions

- `XWidgetData` is the resolution fingerprint. It is created before runtime attachments such as `Artifact`, `XWidget`,
  and `FormToolkit` are applied.
- Widget defaults remain widget-owned for standalone dialogs, editors, and BLAMs. WorkDefs overlay context-specific
  data; defaults must not be moved into WorkDefs.
- The resolution context is `WorkDefinition + ArtifactTypeToken`, not a workflow artifact or its selected values.
- Baselines are owned by product repository and stored below each integration-test bundle's
  `OSEE-INF/workdef-fingerprints/` directory.
- Contexts are approved incrementally at `(workDefId, artifactTypeId)` granularity. The eventual baseline manifest must
  distinguish `approved`, `pending`, and explicitly justified `ignored` contexts.

## Implemented

- `XWidgetWorkDefRenderer.getResolvedXWidgetDatas()` resolves the existing renderer's `XWidgetData` without creating
  SWT controls.
- `WorkDefXWidgetFingerprint` produces stable widget/section data for JSON diagnostics.
- The ATS Admin testing action `Testing - Report WorkDef XWidget Fingerprints` writes a local JSON diagnostic report.
- New `AtsWorkDefEndpointApi.artifactTypeContexts` endpoint returns grouped WorkDef/artifact-type contexts from a
  server-side JDBC query. It filters to Team Workflow, Task, Goal, and Review families; Agile Backlog and Agile Sprint
  are valid Goal-derived contexts.
- Core post-check suite structure is created: `DemoDbInitPostTestSuite` runs immediately after
  `DemoDbPopulateSuite`, when workflow artifacts exist. It runs the existing WorkDef/XWidget validation and the
  core/demo fingerprint comparator.
- `WorkDefXWidgetFingerprintTest` generates the live core/demo report through the existing diagnostic action, removes
  `generatedAt`, branch, and `workflowCount`, orders contexts by `(workDefId, artifactType)`, and compares the result
  against the committed `core-demo.json` baseline.
- On a fingerprint mismatch, the test writes a product-specific HTML report under `OseeData`, opens it in the system
  browser, shows widget-aware context differences (including added/removed widget options), and supplies a copyable
  replacement baseline with the exact workspace destination.
- Other product repositories reuse the core comparator with repository-owned baselines. Their tests run after their
  respective database-initialization suites and before their broader integration-test suites.

## Captured Reports

- Core/demo: 20 valid contexts, 12 artifact types.
  `plugins/org.eclipse.osee.ats.ide.integration.tests/OSEE-INF/workdef-fingerprints/workdef_fingerprints_core_demo.json`
- Other repositories maintain their own captured reports and coverage counts.

These are raw diagnostic reports and currently include transient metadata such as `generatedAt` and `workflowCount`.

## Verified Runtime Context Counts

- Core/demo: 20 contexts, 12 artifact types, no Team Definition contexts.
- Other repository baselines have independently verified context counts.

## Next Steps

1. Run the core/demo post-population suite and confirm the comparator passes against `core-demo.json`.
2. Define canonical baseline JSON with approved/pending status. Do not silently omit contexts.
3. Keep the navigator action as a capture/diagnostic tool. It should not overwrite committed baselines automatically.
4. Later, factor the WorkDef resolution phase into a shared headless resolver for Eclipse and web consumers. Keep this
   separate from the initial snapshot/comparator rollout.

## Work Package Cleanup (Current Question)

Compare unchanged WorkDefs in `git_ggg` with `git_try` to preserve Work Package behavior during the widget-model
migration. The old `XHyperlinkWorkPackageDam` was attribute-backed (`AtsAttributeTypes.WorkPackage`) and resolved its
values from the workflow's Team Definition or top Team Definition. Its current equivalent is:

```java
new AttrWidgetDef(AtsAttributeTypes.WorkPackage, XXWorkPackageEnumArtWidget)
```

Use that form for Work Package headers/default headers and replace every bare
`new WidgetDef(XXWorkPackageEnumArtWidget)`. Do not replace plain state-layout
`new AttrWidgetDef(WorkPackage)`: `git_try` used a text widget there, so those are intentionally plain attribute
widgets. Product-specific base WorkDefs are already correct.

`EnumArtWidgetDef` exists in `org.eclipse.osee.ats.api.workdef.model`, but it is the generic
`XXStringsEnumArtWidget` form for an explicitly supplied enum artifact. It is not the replacement for the dynamic
Team Definition lookup performed by `XXWorkPackageEnumArtWidget`.
