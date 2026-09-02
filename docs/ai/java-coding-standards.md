---
summary: "Java coding standards for OSEE: file headers, encoding, naming, JavaDoc policy, and style rules"
tags: [java, coding-standards, javadoc, style]
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

## Author Tag

Every top-level type (class, interface, enum) must have an `@author` JavaDoc tag:

```java
/**
 * @author First Last
 */
public class MyClass {
```

- Use your real name (first and last).
- Multiple authors are allowed if multiple people made substantial contributions.

## Character Encoding

- All Java source files must be UTF-8 encoded.
- **Use only ASCII characters (U+0000 to U+007F) in source code and comments.**
  - No box-drawing characters (U+2500 range)
  - No arrows (->  not  U+2192)
  - No em dashes (-- not U+2014)
  - No smart quotes (" and ' not U+201C/U+201D)
- String literals may contain non-ASCII when the runtime value requires it (e.g., user-facing display text), but comments and identifiers must be pure ASCII.

## JavaDoc Policy

### Principle

Code should be self-documenting. JavaDoc is only warranted when it communicates something a developer cannot infer from reading the method signature, argument names, and a short method body.

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

## Import Style

- Use specific imports when fewer than 5 imports come from the same package.
- Use wildcard imports (`import foo.*`) when 5 or more imports come from the same package.
- Organize imports alphabetically within groups: `java.*`, `javax.*`, `org.*`, `com.*`.
- Remove unused imports.

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
