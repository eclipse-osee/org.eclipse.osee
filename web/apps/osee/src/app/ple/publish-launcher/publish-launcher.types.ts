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
};

export type TabCheckbox = {
	readonly key: string;
	readonly label: string;
	readonly default?: boolean;
};

export type PublishLauncherTabConfig = {
	readonly key: string;
	readonly label: string;
	readonly description: string;
	readonly instructions: readonly string[];
	readonly dropdowns: readonly TabDropdown[];
	readonly checkboxes: readonly TabCheckbox[];
	readonly targetApi: TargetApi;
	readonly artifact?: string;
};

export type PublishLauncherConfig = {
	readonly title: string;
	readonly tabs: readonly PublishLauncherTabConfig[];
};

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
