---
summary: "Minimal JavaDoc policy — only add comments that can't be captured in code itself"
tags: [java, javadoc, documentation, code-style]
fileMatch: "**/*.java"
---

# JavaDoc Policy

## Principle

Code should be self-documenting. JavaDoc is only warranted when it communicates something a developer can't infer from reading the method signature, argument names, and a short method body.

## Rules

1. **Do not add JavaDoc that restates what the code already says.**

   - No `@throws OseeCoreException` if the short method visibly throws it.
   - No `@return` that just repeats the return type or method name (e.g., `@return the name` on `getName()`).
   - No `@param` that restates the parameter name (e.g., `@param id the id`).

2. **Do add JavaDoc when it provides value a reader can't get from the code alone:**

   - Non-obvious preconditions or postconditions.
   - Subtle side effects (caching, lazy initialization, network calls).
   - Valid value ranges or formats that aren't enforced by the type system.
   - Thread-safety guarantees or lack thereof.
   - Why a particular algorithm or approach was chosen when alternatives exist.

3. **Prefer better code over more comments.**

   - Rename methods/parameters to be self-explanatory rather than adding a comment.
   - Keep methods short so return values and thrown exceptions are obvious.
   - Use expressive types (e.g., `Optional`, enums, value objects) instead of documenting nullable returns or magic values.

4. **Do not generate boilerplate class-level JavaDoc** (e.g., `/** This class does X. */` when the class name already says it does X).

5. **When modifying existing code:**
   - Remove JavaDoc that has become obvious or redundant.
   - Do not add new obvious JavaDoc just because other methods in the class have it.
   - Fix stale JavaDoc that contradicts the implementation.

## Examples

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

Good (no JavaDoc needed — the code is self-evident):

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
