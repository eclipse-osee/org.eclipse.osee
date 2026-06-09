/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { UserRoles } from '@osee/shared/types/auth';

type baseNavigationElement = {
	label: string;
	cypressLabel: string;
	pageTitle: string;
	isDropdownOpen: boolean;
	requiredRoles: UserRoles[];
	icon: string;
	description: string;
};

export type dropdownNavigationElement = baseNavigationElement & {
	isDropdown: true;
	routerLink: '';
	usesBranch: false;
	children: navigationElement[];
	external?: never;
};

export type leafNavigationElement = baseNavigationElement & {
	isDropdown: false;
	routerLink: string;
	usesBranch: boolean;
	children: [];
	external?: boolean;
};

export type navigationElement =
	| dropdownNavigationElement
	| leafNavigationElement;

export const defaultNavigationElement: leafNavigationElement = {
	label: '',
	cypressLabel: '',
	pageTitle: '',
	isDropdown: false,
	isDropdownOpen: false,
	requiredRoles: [],
	routerLink: '',
	icon: '',
	description: '',
	children: [],
	usesBranch: false,
};
