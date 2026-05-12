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
- **Do not use constructors in components.**

## Angular Material + dialogs

- Use angular materials where possible.
- Use Angular Material dialog primitives: `MatDialogTitle`, `MatDialogContent`, `MatDialogActions`, `MatDialogClose`.
- For dialogs, return values via `MatDialogRef.close(value)`.
- Use explicit interfaces for data passed via `MAT_DIALOG_DATA`.

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

## Templates + Tailwind

- Use Tailwind utility classes in templates for layout and spacing. Ensure all Tailwind classes are prepended by `tw-`.
- Tailwind class order must adhere to `eslinttailwindcss/classnames-order`.
- If Tailwind classes override Angular Material button colors (e.g., `tw-bg-*`, `tw-text-*`), add Tailwind disabled variants so disabled buttons still appear disabled (e.g., `disabled:tw-bg-*`, `disabled:tw-text-*`, `disabled:tw-cursor-not-allowed`).
- To show tooltips on disabled buttons, wrap the button with a `span`/`div` that hosts the tooltip (don't attach the tooltip directly to a disabled button).

## HTTP + loading patterns

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
