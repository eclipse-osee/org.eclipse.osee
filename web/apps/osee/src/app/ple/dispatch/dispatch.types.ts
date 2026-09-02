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

export type BranchType = 'working' | 'baseline' | '';

export type TargetApi = {
	readonly method: 'GET' | 'POST';
	readonly url: string;
	readonly button?: string;
};

export type TabDropdown = {
	readonly key: string;
	readonly label: string;
	readonly required?: boolean;
	readonly options?: readonly DropdownOption[];
	readonly contentApi?: TargetApi;
	readonly component?: string;
	readonly dependsOn?: readonly string[];
};

export type TabCheckbox = {
	readonly key: string;
	readonly label: string;
	readonly default?: boolean;
};

export type TabFileInput = {
	readonly key: string;
	readonly label: string;
	readonly accept: string;
	readonly required?: boolean;
	readonly multiple?: boolean;
	readonly contentType?: string;
};

export type DispatchTabConfig = {
	readonly key: string;
	readonly label: string;
	readonly description: string;
	readonly instructions: string;
	readonly dropdowns: readonly TabDropdown[];
	readonly checkboxes: readonly TabCheckbox[];
	readonly fileInputs?: readonly TabFileInput[];
	readonly targetApi: TargetApi;
	readonly artifact?: string;
	readonly downloadFileName?: string;
};

// --- Versioned Config Types ---

/**
 * V1 config shape - the initial release format.
 * The version field is required and must be present in the JSON.
 */
export type DispatchConfigV1 = {
	readonly version: string;
	readonly title: string;
	readonly tabs: readonly DispatchTabConfig[];
};

/**
 * Union of all known config versions for raw parsing.
 * Add new versions here as they are introduced.
 */
export type AnyDispatchConfig = DispatchConfigV1;

/**
 * The current (latest) config type used throughout the app.
 * Always points to the latest version's shape.
 */
export type DispatchConfig = DispatchConfigV1;

// --- Shared Types ---

export type DropdownOption = {
	readonly id: string | number;
	readonly label: string;
};

export type DropdownApiItem = {
	readonly id: string;
	readonly name: string;
	readonly typeId?: string;
	readonly typeName?: string;
};

export type FormState = Record<string, unknown>;

export type DropdownState = Readonly<
	Record<string, Readonly<Record<string, readonly DropdownOption[]>>>
>;

export type FilterState = Readonly<
	Record<string, Readonly<Record<string, string>>>
>;
