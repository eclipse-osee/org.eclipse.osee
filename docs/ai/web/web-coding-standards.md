---
summary: "Angular 18+, Material, Tailwind, signals, httpResource patterns for OSEE web"
tags: [web, angular, material, tailwind, playwright]
fileMatch: "web/**"
---

# OSEE Web Coding Standards

These instructions apply when working with code under the `web/` directory.

Generate code for our repo using **Angular (v18+)**. **Follow our repo style exactly.**

## Component architecture

- Use `@Component` with the `imports` array listing Angular Material and other dependencies (**no NgModule usage**).
- Do not introduce NgModules. Put all dependencies in the Component `imports` array.
- Use `changeDetection: ChangeDetectionStrategy.OnPush`.
- Use `@if` `@else` `@for` `@loading` etc rather than `ngIf` `ngFor` etc.
- Display tooltips for why things are disabled.

## Dependency injection + state

- Use `inject()` for DI (e.g., `MatDialogRef`, `MAT_DIALOG_DATA`, services).
- Use **signals** for local component state if stateful.
- Use RxJS (`BehaviorSubject`/`ReplaySubject`/etc.) only when needed, matching the patterns in our code.
- Use `viewChild()` or `viewChild.required()` if template references to forms or elements are required.
- **Avoid constructors in components.** Prefer field initializers with `inject()` and lifecycle callbacks (`afterNextRender`, effects) for initialization logic. For data loading, use signal-based patterns (`httpResource`, `toSignal()`, `computed()`) which handle async state declaratively without constructor logic. Use a constructor only when logic must run synchronously at creation time before the first render and no signal-based alternative exists (e.g., synchronous batch rendering of pre-loaded local data).

## Angular Material + dialogs

- Use angular materials where possible.
- Use Angular Material dialog primitives: `MatDialogTitle`, `MatDialogContent`, `MatDialogActions`, `MatDialogClose`.
- For dialogs, return values via `MatDialogRef.close(value)`.
- Use explicit interfaces for data passed via `MAT_DIALOG_DATA`.

### Dialog styling (mandatory for all dialogs)

All dialogs must follow this structure and styling:

```html
<h1 mat-dialog-title>
  <div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
    <mat-icon>icon_name</mat-icon>
    Dialog Title Text
  </div>
</h1>
<mat-dialog-content>
  <!-- content -->
</mat-dialog-content>
<div mat-dialog-actions align="end" class="tw-gap-2">
  <button mat-stroked-button class="tw-text-foreground-text" (click)="onCancel()">
    Cancel
  </button>
  <button mat-stroked-button [mat-dialog-close]="data" [disabled]="form.invalid">
    Submit
  </button>
</div>
```

Rules:
- **Title**: `<h1 mat-dialog-title>` with icon + text in a flex row using `tw-gap-2`. No colored backgrounds on the title bar.
- **Actions**: `<div mat-dialog-actions align="end" class="tw-gap-2">`. Always right-aligned with gap.
- **Cancel button**: `mat-stroked-button` with `class="tw-text-foreground-text"`. Always use the word "Cancel" (not "Close" or "Dismiss"). This overrides Material's default primary color to use the neutral foreground color.
- **Submit button**: `mat-stroked-button` (no extra color class). Text describes the action (e.g., "Insert Table", "Ok").
- **Destructive button**: `mat-stroked-button` with `class="tw-text-warning"`. Only for actions that permanently delete or destroy data (e.g., "Delete", "Remove", "Purge"). Never use warning color on Cancel/Close.
- **No `mat-flat-button` or `class="primary-button"`** in dialog actions. Use `mat-stroked-button` for all buttons.

## Empty state messages

When a panel or section has no content to display (e.g., no branch selected, no artifact open, no results), show a helpful empty state message following these rules:

- **Text**: `tw-text-sm tw-opacity-50` — small, muted, not italic.
- **Icons** (if used): same `tw-opacity-50` as the text.
- **Container padding**: `tw-p-4` to align with form field content edges.
- **No italic** — plain text only.
- **Message content**: Tell the user what action to take (e.g., "Select a branch to browse the artifact hierarchy.").

## Toggle button groups

- Use `mat-button-toggle-group` for mutually exclusive options (not radio buttons).
- Always add `hideSingleSelectionIndicator` to remove the default checkmark indicator on the selected toggle. This is purely visual — it does not prevent setting a default `[value]` on the group.
- Selected state: **primary background with background-colored text** (filled). Use tokens:
  - `--mat-button-toggle-selected-state-background-color: var(--osee-primary-default)`
  - `--mat-button-toggle-selected-state-text-color: var(--osee-background-background)`
- Never use white text on a colored (filled) background for toggle selections — use the background token so it adapts to light/dark mode.

## Form field appearance

- **Prefer `appearance="outline"`** for `mat-form-field` elements for a consistent look across the app. Exceptions are allowed where outline styling doesn't fit the context (e.g., inline table cell editors) — use judgment.
- **Add `tw-pt-4` to every outline `mat-form-field`** to provide 1rem of space above the field for the floating label to move into when focused/populated. Without this padding, the label overlaps surrounding content when it floats above the outline notch.
- Always include `subscriptSizing="dynamic"` unless the field needs a fixed-height hint/error area.
- Prefer Tailwind utility classes for spacing and layout around form fields over custom component styles.

Example:

```html
<mat-form-field appearance="outline" class="tw-w-full tw-pt-4" subscriptSizing="dynamic">
  <mat-label>Field Label</mat-label>
  <input matInput ... />
</mat-form-field>
```

## Styling Angular Material components

- **Prefer Tailwind utility classes** for layout, spacing, colors, and typography where they work. Only use Material CSS tokens when customizing Material-specific visual behaviors that Tailwind can't reach (e.g., selected state colors, indicator heights).
- **Never target internal Material/MDC classes** (`.mdc-text-field--filled`, `.mdc-tab--active`, `.mat-mdc-menu-item`, `.mdc-form-field`, etc.) in component styles or global styles. These are implementation details that break on dependency upgrades.
- **Use Material CSS custom property tokens** to customize component appearance. These are the public API for theming. Examples:
  - `--mdc-filled-text-field-container-color: transparent` (form field background)
  - `--mat-menu-item-one-line-container-height: 36px` (menu item height)
  - `--mdc-radio-state-layer-size: 24px` (radio button touch target)
  - `--mdc-tab-indicator-active-indicator-height: 2px` (tab indicator)
- **Set tokens on `:host`** in component styles for scoped overrides, or on the component's element selector in `styles.sass` for global overrides that need to cascade through child components.
- **If no token exists**, prefer changing the component's `appearance` (e.g., `appearance="outline"` for borderless fields) or restructuring the template over targeting internals. **Do not use `::ng-deep`** — it is deprecated and creates fragile, hard-to-maintain styles. If a Material component doesn't expose the customization you need, restructure the template or use a different component.

## Typing + code style (apply broadly)

- Keep code **strongly typed**.
- Prefer type aliases over interfaces.
- No explicit any.
- Prefer `const` if never reassigned.
- Function names should start with a verb.
- Match our naming and folder structure in imports and selectors.
- Mark `private`, `protected`, `readonly` where applicable, especially for class variables.

## Imports + barrel exports

- **Always prefer barrel imports** (e.g., `@osee/shared/types`, `@osee/shared/services`, `@osee/shared/components`, `@osee/shared/utils`, `@osee/transactions/services`, `@osee/transactions/types`, `@osee/attributes/constants`, etc.) over relative paths when the target is exported from a barrel.
- When creating new shared types, services, or components, **add them to the appropriate `public-api.ts`** barrel so consumers can import via the `@osee/` path.
- Use relative imports only for files within the same feature folder that are not exported from a barrel (e.g., sibling files like `./markdown-editor-examples`).
- **Beware of circular barrel dependencies.** A cycle occurs when barrel A exports a file that (transitively) imports from barrel B, and barrel B exports a file that imports from barrel A. Symptoms include `Cannot read properties of undefined (reading 'ɵcmp')` errors in tests. To break cycles, use a relative import for the specific file instead of the barrel import at the point where the cycle would close.

## Theme colors

The app uses a token-based color system via CSS variables and Tailwind utilities. Colors adapt automatically to light/dark mode.

### Primary palette (blue in light, light-blue in dark)

| Tailwind class | Light mode | Dark mode | Use case |
|---|---|---|---|
| `tw-text-primary` | `#2979ff` (blue-28) | `#4fc3f7` (light-blue-19) | Default primary accent |
| `tw-text-primary-lighter` | `#2962ff` (blue-29) | `#81d4fa` (light-blue-18) | Lighter primary variant |
| `tw-text-primary-darker` | `#0d47a1` (blue-25) | `#29b6f6` (light-blue-20) | Darker primary variant |
| `tw-text-primary-500` | `#2196f3` (blue-21) | `#03a9f4` (light-blue-21) | M2 500-weight primary |

### Warning/error palette (red in both modes)

| Tailwind class | Light mode | Dark mode | Use case |
|---|---|---|---|
| `tw-text-warning` | `#ff1744` (red-28) | `#ff1744` (red-28) | Destructive actions (Delete, Remove) |
| `tw-text-warning-lighter` | `#ef5350` (red-20) | `#ef5350` (red-20) | Lighter warning variant |
| `tw-text-warning-darker` | `#b71c1c` (red-25) | `#b71c1c` (red-25) | Darker warning variant |

### Usage guidelines

- Use `tw-text-primary` / `tw-bg-primary` for primary-colored text and backgrounds.
- Use `tw-text-warning` for destructive actions only (Delete, Remove, Purge). **Do not use on Cancel/Close buttons.** Do not use hard-coded slot numbers like `tw-text-osee-red-8` — always prefer the semantic `tw-text-warning` token which adapts to the theme.
- Use the Material `primary-button`, `tertiary-button`, `error-button` CSS classes on `mat-raised-button` / `mat-flat-button` for pre-themed buttons (these use the M3 theming system, not Tailwind).
- For foreground/background semantic tokens (adapt to light/dark automatically): `tw-text-foreground-base`, `tw-bg-background-card`, `tw-bg-background-hover`, etc.
- Prefer semantic color tokens over hard-coded `osee-*` slot numbers. Use `tw-text-primary` instead of `tw-text-osee-blue-28`, use `tw-text-warning` instead of `tw-text-osee-red-8`.

## Templates + Tailwind

- Use Tailwind utility classes in templates for layout and spacing. Ensure all Tailwind classes are prepended by `tw-`.
- Tailwind class order must adhere to `eslinttailwindcss/classnames-order`.
- If Tailwind classes override Angular Material button colors (e.g., `tw-bg-*`, `tw-text-*`), add Tailwind disabled variants so disabled buttons still appear disabled (e.g., `disabled:tw-bg-*`, `disabled:tw-text-*`, `disabled:tw-cursor-not-allowed`).
- To show tooltips on disabled buttons, wrap the button with a `span`/`div` that hosts the tooltip (don't attach the tooltip directly to a disabled button).

## Tooltip text conventions

- **Action labels** (short phrases naming what a button does): Use Title Case, no period. Examples: "Upload Image", "Add Column", "Exit Fullscreen", "Insert or Edit Table", "Preview With Images".
- **Explanatory sentences** (describing state, reasons, or instructions): Use sentence case with a trailing period. Examples: "Editing is disabled.", "Save the artifact first to enable image uploads.", "Click the edit icon to return to editing."
- Never use ALL CAPS for tooltips.

## HTTP + loading patterns

- **All HTTP calls (`httpResource`, `HttpClient`) must live in services, not in components.** Components consume service methods — they never inject `HttpClient` or call `httpResource` directly.
- Prefer `httpResource` for read-only endpoints wherever possible.
- For `text/plain` endpoints, use `httpResource.text(() => url)` or explicitly set `responseType: 'text' as const` in the `httpResource` request object. Do not allow JSON parsing errors for text endpoints.
- Prefer the repo's global refresh pattern:
  - Ensure the `httpResource` factory **reads** `uiService.updateCount()` so it refreshes after updates.
  - After successful non-transaction HTTP mutations (`PUT`, `POST`, `DELETE`) that should trigger refreshes, set `this.uiService.updated = true` in the service layer after the request completes.
- Do not manage per-request disable/loading flags manually. Use either:
  - `resource.isLoading()` for resource-specific loading, and/or
  - `loadingGlobal()` from `HttpLoadingService` (via `toSignal`) for global loading.
- Our `loadingGlobal()` is a **string**. Always compare explicitly:
  - Use `loadingGlobal() === 'true'` / `loadingGlobal() === 'false'` (do not rely on truthy/falsy).

Example pattern:

```typescript
private loadingService = inject(HttpLoadingService);
protected readonly $loadingGlobal = this.loadingService.isLoading;
protected readonly loadingGlobal = toSignal(this.$loadingGlobal, { initialValue: 'false' });
```

## Errors

- If displaying errors is required, use the ui service:
  - `import { UiService } from '@osee/shared/services';`
  - `uiService = inject(UiService);`
  - `this.uiService.ErrorText = "error message";`

## Required header (top of every file)

Include the Boeing EPL header comment block at the top of every **new** file. The content should be exactly the same, but the comment syntax can be different based on the file type. **Do not modify or update headers on existing files** — leave them as-is (they may have older copyright years or different contributor lists).

```text
/*********************************************************************
 * Copyright (c) 2026 Boeing
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

## Formatting and linting

Run all commands from `web/apps/osee/`.

- **Prettier** (format changed files): `npx prettier --write <file1> <file2> ...`
- **ESLint** (lint changed files): `npx eslint <file1> <file2> ...`

Always run prettier and eslint on changed files before committing. Use relative paths from `web/apps/osee/`. Do not run these automatically during development — only run when explicitly requested or at the end of a development session.

## Playwright E2E tests

Tests live in `web/apps/osee/playwright/specs/` organized by feature area (e.g., `mim/tests/`, `artifact-explorer/tests/`). The framework is `@ngx-playwright/test`.

### Running tests

All commands run from `web/apps/osee/`. The OSEE backend (port 8089) and Angular dev server (port 4200) must be running. Always pass `--config playwright.config.ng.ts` to use the correct project configuration.

```bash
# Run all tests (includes setup)
npx playwright test --config playwright.config.ng.ts

# Run a specific project
npx playwright test --config playwright.config.ng.ts --project "Setup" --project "Artifact Explorer Tests"

# Run tests matching a grep pattern
npx playwright test --config playwright.config.ng.ts --project "Setup" --project "Artifact Explorer Tests" --grep "Table Dialog"

# Run a single test file
npx playwright test --config playwright.config.ng.ts --project "Setup" --project "Artifact Explorer Tests" playwright/specs/artifact-explorer/tests/markdown-editor.e2e-spec.ts

# Run in headed mode for debugging
npx playwright test --headed --grep "should insert a table"

# View last test report
npx playwright show-report
```

**Important:** Always include the `"Setup"` project when running tests — most projects depend on it for database initialization.

### Project structure

- **Config**: `playwright.config.ng.ts` defines `baseURL` (from `playwright/shared/test-config.ts`), test directories, and project dependencies.
- **Shared config**: `playwright/shared/test-config.ts` exports `APP_BASE`, `API_BASE`, and `AUTH_HEADER`. Use these for API calls and `waitForResponse` URL matching — never hardcode `localhost` URLs.
- **Navigation**: Use **relative paths** in `page.goto()` (e.g., `'/ple/artifact-explorer'`, `'/ci/admin'`). The config's `baseURL` handles resolution. Do not use `APP_BASE` for navigation.
- **Test files**: Named `*.e2e-spec.ts`. Include the Boeing EPL header.
- **Helper utilities**: Place shared navigation and setup functions in a `utils/helpers.ts` file within the feature's spec folder. Import and reuse them across tests.
- **Register new test directories**: When creating tests in a new feature folder under `playwright/specs/`, add a corresponding project entry in `playwright.config.ng.ts` with the appropriate `testDir` and `dependencies`.

### Writing effective tests

- **Use accessible locators** in priority order: `getByRole`, `getByLabel`, `getByText`, `getByTestId`. Avoid CSS selectors and XPath — they break on refactors.
- **Add `data-testid` attributes** to components when no accessible locator is available. Prefer this over fragile structural selectors.
- **Prefer adding aria attributes over `data-testid`** when the element lacks a semantic role or label. Adding `role`, `aria-label`, or `aria-labelledby` to the component template improves both accessibility and testability — use `data-testid` only as a last resort when no meaningful aria semantics apply (e.g., a purely decorative wrapper). For tooltips, use `getByRole('tooltip', { name: '...' })` instead of targeting internal Material CSS classes.
- **Wait for state, not time.** Use `expect(...).toBeVisible()`, `waitForResponse`, or `waitForSelector` instead of `waitForTimeout`. Fixed timeouts are flaky and slow.
- **Keep tests independent.** Each test should set up its own state. Use `test.beforeEach` for shared navigation, not shared mutable state between tests.
- **Use `test.describe` blocks** to group related tests and share setup via `beforeEach`.
- **Assert on outcomes, not implementation.** Check that the user sees the right content — don't assert internal class names or DOM structure that could change.
- **One logical behavior per test** — or consolidate related behaviors that share setup cost. Multiple related `expect` calls verifying different aspects of a single workflow are fine. Separate tests for unrelated features that could run in parallel.

### Writing robust tests

- **Verify data, not just visibility.** When a dialog or form loads data, assert the actual field values — not just that the element appeared.
- **Verify outputs end-to-end.** If a user action produces output (text, DOM changes, downloads), assert the output contains the expected structure and content.
- **Test programmatic state via `page.evaluate()`** when no visible indicator exists — e.g., reading selection ranges, scroll positions, or computed values from the DOM.
- **Use `locator.filter({ hasText })` for icon-only buttons** — buttons with only an icon and no label often lack accessible names. Filter by the icon's text content.
- **Scope locators to reduce ambiguity** — prefer `page.locator('section button')` over `page.getByRole('button')` when multiple buttons share similar names across different page regions.

### Testing auto-save (focus-lost) editors

The artifact explorer uses persistent editors that save automatically when focus leaves the editor. To avoid test state leaking between tests:

- **Use a unique artifact per test.** Each test that modifies editor content should open a different artifact. This eliminates cross-test interference entirely — no blur-save waits needed.
- **Use parallel mode** when each test has its own artifact. Serial mode is only needed if tests intentionally share state.
- **The table dialog reads from the textarea's DOM value directly**, not from the backend. No save is needed before opening the dialog — just `fill()` and click the toolbar button.
- **For cursor positioning**, use `page.evaluate()` to set `selectionStart`/`selectionEnd` after `textarea.click()`. No blur dispatch needed.
- **Scope dialog button locators to `mat-dialog-container`** to avoid matching buttons behind the dialog backdrop.
- **Test disabled states and their explanations** — verify the button is disabled and that the user receives feedback explaining why (tooltip, message, etc.).
- **Use `.first()` for tooltip assertions** — Angular Material can leave stale tooltip overlays in the DOM. Use `.first()` when asserting tooltip visibility to avoid strict-mode violations.

### Patterns to follow

```typescript
import { test, expect } from '@ngx-playwright/test';

test.describe('Feature Name', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/ple/feature-route');
    // shared setup: select branch, wait for load, etc.
  });

  test('should do the expected thing', async ({ page }) => {
    await page.getByRole('button', { name: 'Action' }).click();
    await expect(page.getByText('Expected Result')).toBeVisible();
  });

  test('should verify data was parsed correctly', async ({ page }) => {
    // Set up known state
    const textarea = page.getByRole('textbox', { name: 'Editor' });
    await textarea.fill('known input');

    // Trigger action
    await page.getByRole('button', { name: 'Edit' }).click();

    // Verify the dialog fields contain parsed data
    await expect(page.getByRole('textbox', { name: 'Field 1' })).toHaveValue('expected');
  });

  test('should verify selection covers correct range', async ({ page }) => {
    const textarea = page.getByRole('textbox', { name: 'Editor' });
    await textarea.fill('before\\ntarget content\\nafter');

    await page.getByRole('button', { name: 'Select' }).click();

    const selection = await page.evaluate(() => {
      const ta = document.querySelector('textarea') as HTMLTextAreaElement;
      return ta.value.substring(ta.selectionStart, ta.selectionEnd);
    });
    expect(selection).toContain('target content');
    expect(selection).not.toContain('before');
  });
});
```

### What to avoid

- **Hardcoded URLs** — use relative paths for `page.goto()` and `APP_BASE` only for `waitForResponse` or API request matching.
- **Chained `waitForTimeout`** — replace with proper waitFor conditions.
- **Overly specific selectors** — `page.locator('div > span:nth-child(3)')` will break on any layout change.
- **Tests that depend on execution order** — use `test.describe.configure({ mode: 'serial' })` only when tests genuinely share state (e.g., multi-step workflows like branch creation then commit).
- **Screenshot-only tests** — screenshots are for documentation, not assertions. Always pair with `expect` calls.
- **Testing only that something is visible** — always verify the content/value, not just presence. A dialog opening is not enough; verify the data inside it.
- **Using `getByRole('button', { name: 'tooltip text' })` for icon buttons** — `matTooltip` doesn't set accessible name. Use `.filter({ hasText: 'icon_name' })` instead.

### Writing efficient tests

Page navigation is the primary time cost (~5s per test). Minimize it:

- **Enable parallel execution.** Add `test.describe.configure({ mode: 'parallel' })` at the top of each describe block whose tests are independent (no shared mutable state). This runs tests across multiple workers simultaneously.
- **Consolidate related assertions into fewer tests.** Group assertions that test the same feature area into one test with multiple steps. A single test that verifies "insert with caption, insert without caption, and edit existing caption" is faster than three separate tests each paying the navigation cost. Use `await test.step('step name', async () => { ... })` to label each phase — failure reports will show exactly which step failed.
- **Reset state within a test instead of relying on fresh navigation.** Use `textarea.fill('')` or similar resets between sub-steps within a consolidated test rather than creating a new test.
- **Keep `beforeEach` minimal.** Only include shared navigation. Dialog-specific setup (opening the table dialog) belongs in individual tests or a nested describe's own `beforeEach`.

## After development checklist

Before presenting changes as complete, **ask the user** if you should run through these steps. Do not perform them automatically during development — only when the user approves or at the end of a development chunk.

1. **Format and lint** — Run prettier and eslint on changed files (see [Formatting and linting](#formatting-and-linting) above). The `/web-lint-prettier-changed` hook automates this.
2. **Build check** — Verify no TypeScript diagnostics exist in changed files.
3. **Run relevant tests** — Identify which Playwright test suites cover the changed components (`playwright/specs/` by feature area). Run them per the instructions in [Running tests](#running-tests). If failures occur, diagnose and fix before presenting the result.
4. **Code review** — Trigger the "Review Changed Code" hook (`.kiro/hooks/code-review.kiro.hook`) to review all changed web and Java files for code quality, bugs, and standards adherence.
5. **Review documentation** — Check if any `docs/ai/` files describe components, patterns, or architecture that was modified. If docs reference stale class names, signal names, CSS classes, or file paths, propose updates and wait for approval before writing. Keep docs concise and factual — describe what IS, not what was.
