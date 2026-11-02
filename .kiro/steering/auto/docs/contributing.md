---
inclusion: manual
---

# AI Contributing Guide

## Code Review Test Suite

### Location

All code review tests live in:
```
plugins/org.eclipse.osee.ats.ide.integration.tests/src/org/eclipse/osee/ats/ide/integration/tests/coderev/
```

### Architecture

```
AbstractCodeReview.java       - Base class with file discovery, reporting, test execution
CodeReviewOsgiXml.java        - Validates OSGI-INF XML files
CodeReviewXWidgets.java       - Validates XX widget coding standards
CodeReviewBlams.java          - Validates AbstractBlam conventions
CodeReviewAll.java            - Master runner for all reviews
```

`CodeReviewTest.java` in the parent package provides JUnit `@Test` methods and is included in `AtsIdeIntegrationTestSuite`.

### Execution Modes

| Context | Behavior |
|---------|----------|
| Individual test (right-click → Run as JUnit) | Always opens HTML report in browser |
| `CodeReviewAll.main()` standalone | Opens combined report; individual errors also open |
| Integration suite (`AtsIdeIntegrationTestSuite`) | Fails test + opens report on errors; passes silently otherwise |

The mode is detected via `OseeProperties.isInTest()` which is set `true` by the integration suite's `@BeforeClass`.

### Adding a New Code Review

1. Create `CodeReviewMyCheck extends AbstractCodeReview` in the `coderev` package
2. Implement:
   - `getReviewName()` - human-readable name, used for HTML file naming
   - `getFilePattern()` - regex for file names to scan (e.g., `".*Blam\\.java"`)
   - `performReview(List<String> files)` - your validation logic
3. Optionally override:
   - `getIgnoreDirs()` - directories to skip
   - `findFiles()` - custom file discovery if pattern matching isn't sufficient
4. Add to `CodeReviewAll.runAndAssert()`:
   ```java
   runReview(new CodeReviewMyCheck());
   ```
5. Add a `@Test` method to `CodeReviewTest.java`:
   ```java
   @Test
   public void testCodeReviewMyCheck() {
      CodeReviewMyCheck review = new CodeReviewMyCheck();
      Assert.assertTrue("CodeReviewMyCheck found errors. See HTML report.", review.runAndAssert());
   }
   ```
6. Add `main()` method for standalone execution

### Base Search Directory

`AbstractCodeReview.getBaseSearchDir()` resolves from the class file location up to the repo root (one directory above `plugins/`). All file searches start from there recursively.

### Ignore Marker

Lines containing `CodeReview.IgnoreLine` are skipped by all checks. Use when a specific line intentionally violates a rule:
```java
somethingThatViolatesARule(); // CodeReview.IgnoreLine
```

### XWidget Code Review Checks

From `CodeReviewXWidgets`:
- `@Component(service = XWidget.class)` annotation required
- `WidgetId ID` field must match class name
- Default no-arg constructor required for OSGi
- Only one constructor should call `super()`; others chain via `this()`
- ATS widgets need `xxWid` (XXWorkItemData) field
- `setArtifact()` must not call `refresh()` or `setSelected()`
- `initializeSelected()` must use `setSelected(value, false)`
- No direct `XWidgetUtility.setStatus` usage
- No `validate()` override; use `getValidator()` instead
- No `getSelected()` override
- Abstract classes start with `XAbstract` or `XXAbstract`
- No `System.err/out.print`
- No TBD markers

### Blam Code Review Checks

From `CodeReviewBlams`:
- `@Component(service = AbstractBlam.class)` annotation required
- Must use `getXWidgetItems()` with builder, not `getXWidgetsXml()`
- `BLAM_NAME` static final String field required
- Constructor must reference `BLAM_NAME`, not string literal
- Default no-arg constructor required
- `getDescriptionUsage()` recommended
- No `.endWidget()` calls
- Checkbox widgets should not use `.andLabelAfter()` or `.andHorizLabel()` (already defaults)
- Widget labels used in both builder and `runOperation` should be `static final String` constants

### OSGI XML Code Review Checks

From `CodeReviewOsgiXml`:
- Implementation class must exist in the correct bundle
- Bind/unbind methods must follow naming conventions (`set`/`add`/`remove` + interface name)
- Bind/unbind methods must exist in the implementation class

## Code Quality Standards

### No `var` keyword
Always use concrete types. `var` is not permitted in the OSEE codebase.

### No blank lines between field declarations
Group related fields together without gaps.

### Static fields must be `final` and `UPPER_CASE`

### No `/** {@inheritDoc} */` comments
These add noise without value.

### Use lambdas over anonymous inner classes where possible

## Multi-Agent Conventions

When creating files, always use the standard Eclipse/OSEE copyright header with the current year.
