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

/**
 * Registry of reusable components that can be referenced by name in the
 * Dispatch configuration's `component` field on a dropdown.
 *
 * To add a new component:
 * 1. Add the key to `RegisteredComponent`.
 * 2. Import and add the component to the tab's `imports` array.
 * 3. Add a `@case` block in the tab template's dropdown loop.
 * 4. Wire up the component's output to `componentValues` via an effect.
 *
 * Each registered component must emit a string value (typically an ID) that
 * gets substituted into the targetApi URL template using the dropdown's `key`.
 */
export type RegisteredComponent =
	| 'viewSelector'
	| 'emailSelector'
	| 'branchSelector';

/**
 * Descriptions of each registered component for documentation/tooling.
 */
export const COMPONENT_DESCRIPTIONS: Readonly<
	Record<RegisteredComponent, string>
> = {
	viewSelector:
		'Renders the shared ViewSelectorComponent with autocomplete. Emits the selected applicability view ID.',
	emailSelector:
		'Renders a chip-input with the current user email as a suggestion. Emits a comma-separated list of emails.',
	branchSelector:
		'Renders the shared BranchPickerComponent. Sets the branch in UiService. Emits the selected branch ID.',
};
