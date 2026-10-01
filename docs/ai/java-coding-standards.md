---
summary: "Java coding standards for OSEE (server, desktop, and all plugins): file headers, encoding, JavaDoc policy, imports, JSON, constants, OSGi, error handling, artifact identity, naming, style, and performance"
tags: [java, server, desktop, coding-standards, javadoc, style]
fileMatch: "**/*.java"
---

# Java Coding Standards

## File Header

Every Java file must start with the Eclipse Public License 2.0 header block with the correct copyright year and contributor:

```java
/*********************************************************************
 * Copyright (c) <year> Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
```

- Use the current year for new files.
- Do not change the year on existing files unless performing a substantial rewrite.

## Character Encoding

- All Java source files must be UTF-8 encoded.
- **Use only ASCII characters (U+0000 to U+007F) in source code and comments.**
  - No box-drawing characters (U+2500 range)
  - No arrows (use `->` not U+2192)
  - No em dashes (use `--` not U+2014)
  - No smart quotes (use `"` and `'` not U+201C/U+201D)
- String literals may contain non-ASCII when the runtime value requires it (e.g., user-facing display text), but comments and identifiers must be pure ASCII.
- HTML entities that render as arrows/dashes (e.g. `&harr;`, `&rarr;`, `&mdash;`) are acceptable in contexts that are meant to render HTML (JavaDoc), because they are themselves pure ASCII. Do not use them in plain `//` comments or identifiers, where they would appear literally as `&harr;` rather than render.

## JavaDoc Policy

### Principle

Code should be self-documenting. **Default to NO JavaDoc.** Method names, parameter names, field names, and short method bodies are the primary documentation - make them clear instead of describing them. Add JavaDoc only when it communicates something a developer genuinely cannot infer from the signature and a short read of the body. When in doubt, leave it out.

This applies to methods, fields, and types alike. Prefer a better name over any comment. Most methods - especially getters, setters, simple delegations, and anything whose name already states what it does - should have no JavaDoc at all. Do not generate a JavaDoc block per method as a matter of habit.

### Rules

1. **Do not add JavaDoc that restates what the code already says.**
   - No `@throws OseeCoreException` if the short method visibly throws it.
   - No `@return` that just repeats the return type or method name (e.g., `@return the name` on `getName()`).
   - No `@param` that restates the parameter name (e.g., `@param id the id`).

2. **Do add JavaDoc when it provides value a reader cannot get from the code alone:**
   - Non-obvious preconditions or postconditions.
   - Subtle side effects (caching, lazy initialization, network calls).
   - Valid value ranges or formats that are not enforced by the type system.
   - Thread-safety guarantees or lack thereof.
   - Why a particular algorithm or approach was chosen when alternatives exist.

3. **Prefer better code over more comments.**
   - Rename methods/parameters to be self-explanatory rather than adding a comment.
   - Keep methods short so return values and thrown exceptions are obvious.
   - Use expressive types (e.g., `Optional`, enums, value objects) instead of documenting nullable returns or magic values.

4. **Do not generate boilerplate class-level JavaDoc** (e.g., `/** This class does X. */` when the class name already says it does X).

   - When JavaDoc spans multiple lines, use the block form `/** ... */` and let the Eclipse formatter wrap it. Do not use `//` line comments for multi-line documentation.

5. **When modifying existing code:**
   - Remove JavaDoc that has become obvious or redundant.
   - Do not add new obvious JavaDoc just because other methods in the class have it.
   - Fix stale JavaDoc that contradicts the implementation.

### Examples

Bad (redundant):

```java
/**
 * Gets the name.
 * @return the name
 */
public String getName() {
    return name;
}
```

Good (no JavaDoc needed -- the code is self-evident):

```java
public String getName() {
    return name;
}
```

Bad (restating the obvious):

```java
/**
 * Deletes the branch.
 * @param branch the branch to delete
 * @throws OseeCoreException if an error occurs
 */
public void deleteBranch(BranchId branch) {
    // short, clear implementation
}
```

Good (adds real value):

```java
/**
 * Caller must hold the branch lock. Deletion is irreversible and
 * cascades to all child transactions.
 */
public void deleteBranch(BranchId branch) {
    // ...
}
```

## Imports

- Always use proper `import` statements. Never use fully-qualified class names inline unless there is a genuine name collision that cannot be resolved with imports.
- Use specific imports when fewer than 5 imports come from the same package.
- Use wildcard imports (`import foo.*`) when 5 or more imports come from the same package.
- Organize imports alphabetically within groups: `java.*`/`javax.*`, `org.*`, `com.*`.
- Remove unused imports.

## JSON Handling

- Use `com.fasterxml.jackson.databind.ObjectMapper` for all JSON serialization and deserialization. Jackson is available in both server (`orcs.core`, `orcs.rest`) and client bundles.
- Never hand-roll JSON with `StringBuilder` or string concatenation. Jackson handles escaping, null safety, and edge cases correctly.
- For deserialization with generics, use `com.fasterxml.jackson.core.type.TypeReference`.
- Build `Map<String, Object>` or `List<Map<String, Object>>` structures and serialize with `mapper.writeValueAsString()`.
- `ObjectMapper` is thread-safe and expensive to construct. Reuse a single `private static final ObjectMapper` instance rather than allocating one per call, especially in hot paths.

## Constants and Magic Values

- No magic strings. Define constants in a shared location accessible to all bundles that need them.
- String constants shared between Java and TypeScript must be defined in Java (in `org.eclipse.osee.framework.core`) and documented as the source of truth. The TypeScript side uses union types that must match.
- Enum-like GUID strings (e.g., `EventModType`, `RelationEventType`) must be referenced via their enum/constant class, never as inline string literals.

## OSGi and Bundles

- Place shared types (enums, constants, interfaces) in `org.eclipse.osee.framework.core` so both server and client bundles can access them.
- If a class is used by multiple bundles, it belongs in the lowest common dependency, not duplicated.
- Use DS annotations (`@Component`, `@Reference`, `@Activate`) for service registration.

## Error Handling

- Use `try/catch` with specific exception types. Don't swallow exceptions silently -- at minimum log them.
- For event/notification code paths that must not break the main flow (e.g., post-commit event dispatch), catch `Exception` and log, but don't let it propagate to the caller.

## Artifact Identity

- GUIDs are legacy. Use numeric artifact IDs for all new code (lookups, event matching, cache keys, wire formats).
- Existing GUID-based code paths are kept only for backward compatibility during the migration period. New code must not introduce GUID-based logic.
- When both GUID and artifact ID are available, prefer the artifact ID.
- Names containing "Guid" in existing code (e.g., `artTypeGuid`, `DefaultBasicGuidArtifact`) are historical -- the values stored are often numeric IDs despite the name. Do not propagate this naming convention.

## Naming

- Classes: `PascalCase`
- Methods and fields: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: all lowercase, no underscores
- Boolean getters: `is` or `has` prefix (e.g., `isActive()`, `hasChildren()`)

## General Style

- No hard-coded absolute file paths in production code. Use configurable properties or workspace-relative paths.
- No fully qualified class names in method bodies -- use imports.
- Prefer early return over deep nesting.
- One class per file (inner classes excepted).
- Do not merge `TODO` or `TBD` comments. Remove them before merging and track the improvement in the issue tracking system instead.
- Intentionally empty blocks (empty catch, no-op branch, empty anonymous body) must contain a `// do nothing` comment so it is clear the emptiness is deliberate, not an oversight.
- Do not add `/* EOF */` or similar end-of-file trailer comments. They add no value and drift out of sync.

## Performance

- Avoid redundant iteration. If multiple pieces of data are needed from the same collection, extract them in a single pass.
- Use appropriate data structures: `HashMap` for O(1) lookup, `LinkedHashSet` for ordered deduplication, `HashSet` for membership testing.
- Don't create unnecessary object allocations in hot paths (event processing, cache lookups).

## Testing

- Test suites and test classes that require test mode set `OseeProperties.setIsInTest(true)` in `@BeforeClass`.
- Do NOT reset the flag to `false` in `@AfterClass`/teardown. The flag is a shared process-wide setting; turning it off in one suite can leave a later suite running without test mode in the same JVM, causing intermittent, hard-to-diagnose failures. Once set for a run, leave it on.

### No console logging during tests

Tests must run quietly. A passing run should produce no incidental stdout/stderr chatter beyond the test framework results and intentional WARN/ERROR diagnostics.

- Do not add code whose purpose is to print progress or debug information to the console during a test.
- Rely on the launch config's `-Dlogback.configurationFile` and `-Djava.util.logging.config.file` for logging levels rather than reconfiguring logging in test code.
- If a test must assert on log output, capture it programmatically (appender/handler) rather than printing to the console.

### `ElapsedTime` must not log to the console in tests

`org.eclipse.osee.framework.jdk.core.util.ElapsedTime` writes to the console (via `XConsoleLogger.err`) whenever it is on. Its no-arg and single-arg constructors default `on=true` and `logStart=true`, logging immediately on construction. In tests:

- Construct it disabled: `new ElapsedTime("name", false)`, or call `.off()` before it logs.
- Do NOT pass `true` to `end(Units, boolean printToSysErr)` -- that forces console output. Use `end(units)` on a disabled instance, or `getTimeSpentString(units)` / `getTimeSpent()` which compute timing without logging.
- Never commit an `ElapsedTime` in test code left on (logging) just for local debugging.

### `System.out` / `System.err` in tests

Do not use `System.out.*` or `System.err.*` in individual test classes. This includes `printStackTrace()`, which writes to `System.err` by default.

Exception: test suite classes (JUnit `@RunWith(Suite.class)` aggregators and their `@BeforeClass`/`@AfterClass`, e.g. `*TestSuite` / `*_Suite`) MAY use `System.out.println` for coarse startup/progress banners (e.g. reporting the active logback/config file). This is suite-level orchestration output, not per-test chatter.

- In individual test classes, use the OSEE logging path (`OseeLog` / SLF4J) for diagnostics so output honors configured levels.
- On failure, prefer JUnit assertions with descriptive messages, or rethrow/wrap the exception, over printing.

### Logback config changes must be reverted or reviewed before merge

`plugins/org.eclipse.osee.server.p2/logback-dev.xml` is shared across the server, IDE, and integration-test launches, so changes affect every launch that references it.

- Temporary debugging edits (raising a logger to `INFO`/`DEBUG`, flipping `debug="true"`, adding appenders) must be reverted before a PR is opened.
- If a logback change is intentional and meant to ship, call it out in the PR description and get it reviewed -- do not let it ride along silently in an unrelated change.

### Balance resource open/close in test teardown

Close each resource exactly once, and close every resource a test opens. For example, JGit's `Git.close()` already closes its wrapped `Repository`; calling `git.getRepository().close()` then `git.close()` double-closes and logs `close() called when useCnt is already zero`.
