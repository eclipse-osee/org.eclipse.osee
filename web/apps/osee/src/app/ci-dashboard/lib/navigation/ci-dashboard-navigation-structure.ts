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
		label: 'Zenith',
		cypressLabel: 'ci',
		pageTitle: 'Zenith',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: '/ci/allScripts',
		icon: 'landscape',
		description: '',
		usesBranch: false,
		children: [
			// Level-2
			{
				label: 'Timeline',
				cypressLabel: 'ci-timeline',
				pageTitle: 'Zenith - Timeline',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/timeline',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'All Scripts',
				cypressLabel: 'ci-allscripts',
				pageTitle: 'Zenith',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/allScripts',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Scripts',
				cypressLabel: 'ci-scripts',
				pageTitle: 'Zenith - Details',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/details',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Dashboard',
				cypressLabel: 'ci-dashboard',
				pageTitle: 'Zenith - Dashboard',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/dashboard',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Subsystems',
				cypressLabel: 'ci-subsystems',
				pageTitle: 'Zenith - Subsystems',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/subsystems',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Batches',
				cypressLabel: 'ci-batches',
				pageTitle: 'Zenith - Batches',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/batches',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Set Diffs',
				cypressLabel: 'ci-set-diffs',
				pageTitle: 'Zenith - Set Diffs',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ci/diffs',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Import',
				cypressLabel: 'ci-import',
				pageTitle: 'Zenith - Import',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.CI_ADMIN],
				routerLink: '/ci/import',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Admin',
				cypressLabel: 'ci-admin',
				pageTitle: 'Zenith - Admin',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.CI_ADMIN],
				routerLink: '/ci/admin',
				icon: '',
				description: '',
				usesBranch: false,
				children: [],
			},
		],
	},
];
