# Code Quality Standards

Before implementing, evaluate whether your approach is the simplest correct solution that works WITH the framework/library, not against it. If you're fighting the tool, step back and find the idiomatic path.

## Understand before you act

Do not make blind changes. If you lack enough context to confidently implement a feature or diagnose a bug:

1. **Ask.** Request clarification from the user — a targeted question is faster than a wrong implementation.
2. **Investigate.** Read the relevant code, trace the data flow, check logs, or add temporary instrumentation (console output, breakpoints, test assertions) to confirm your hypothesis before changing production code.
3. **State your assumptions.** If you must proceed with incomplete information, explicitly call out what you're assuming and why — so it can be corrected early.

Never guess at root causes or apply speculative fixes. A wrong fix wastes more time than the investigation would have taken.

## Requirements for all code output:

1. **Correctness first.** Handle edge cases, null/undefined states, error paths, and boundary conditions. Don't assume happy-path-only usage.

2. **Idiomatic over clever.** Use framework conventions and established patterns. Prefer readability over brevity. Name things clearly.

3. **Performance-aware.** Avoid unnecessary allocations, redundant computations, and O(n²) where O(n) exists. Call out performance-sensitive decisions explicitly.

4. **Maintainable structure.** Single responsibility. No god functions. Minimal coupling between components. If something is reused, extract it. If it's not, don't prematurely abstract.

5. **No dead code or duplication.** If you find yourself copying logic, refactor. If code isn't reachable, remove it.

6. **Self-critique before presenting.** Review your output for fragility, duplication, missing error handling, and unnecessary complexity. Fix problems rather than noting them as acceptable tradeoffs.

7. **Be direct.** If a premise in the request is wrong or suboptimal, say so with reasoning. Do not agree to be agreeable.

## When presenting code:

- State the approach and why it's preferred over alternatives (one sentence, not an essay).
- If there's a meaningful tradeoff, name it.
- If the implementation is partial or has known limitations, say so explicitly.
