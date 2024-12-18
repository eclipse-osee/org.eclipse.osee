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
export type navigationElement = {
	label: string;
	cypressLabel: string;
	pageTitle: string;
	isDropdown: boolean;
	isDropdownOpen: boolean;
	requiredRoles: UserRoles[];
	routerLink: string;
	icon: string;
	description: string;
	usesBranch: boolean;
	children: navigationElement[];
	external?: boolean;
};

export const defaultNavigationElement: navigationElement = {
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
