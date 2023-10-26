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

import { navigationElement } from '@osee/shared/types';
import { UserRoles } from '@osee/shared/types/auth';

// Adding element(s) requires:
// - Defining the element(s) in this file

// if isDropdown, it has children
// if !isDropdown, it has no children

export const ciNavigationStructure: navigationElement[] = [
	// Level-1
	{
		label: 'Continuous Integration',
		cypressLabel: 'ci',
		pageTitle: 'OSEE - Continuous Integration',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: '/ci/allScripts',
		icon: '',
		description: '',
		children: [
			// Level-2
			{
				label: 'All Scripts',
				cypressLabel: 'ci-allscripts',
				pageTitle: 'OSEE - Continuous Integration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/allScripts',
				icon: '',
				description: '',
				children: [],
			},
			{
				label: 'Dashboard',
				cypressLabel: 'ci-dashboard',
				pageTitle: 'OSEE - Continuous Integration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/dashboard',
				icon: '',
				description: '',
				children: [],
			},
			{
				label: 'Subsystems',
				cypressLabel: 'ci-subsystems',
				pageTitle: 'OSEE - Continuous Integration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/subsystems',
				icon: '',
				description: '',
				children: [],
			},
			{
				label: 'Results',
				cypressLabel: 'ci-results',
				pageTitle: 'OSEE - Continuous Integration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/results',
				icon: '',
				description: '',
				children: [],
			},
			{
				label: 'Import',
				cypressLabel: 'ci-import',
				pageTitle: 'OSEE - Continuous Integration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.OSEE_ADMIN],
				routerLink: '/ci/import',
				icon: '',
				description: '',
				children: [],
			},
		],
	},
];
