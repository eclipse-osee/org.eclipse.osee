/*********************************************************************
 * Copyright (c) 2025 Boeing
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

export const mimNavigationStructure: navigationElement[] = [
	// Level-1
	{
		label: 'MIM',
		cypressLabel: 'messaging',
		pageTitle: 'MIM',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [UserRoles.MIM_USER],
		routerLink: '/ple/messaging',
		icon: 'polyline',
		description: '',
		usesBranch: false,
		children: [
			// Level-3
			{
				label: 'MIM Home',
				cypressLabel: 'messaging',
				pageTitle: 'MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging',
				icon: 'home',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Connections',
				cypressLabel: 'connection-nav-button',
				pageTitle: 'Connections - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/connections',
				icon: 'device_hub',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Structures',
				cypressLabel: 'structure-nav-button',
				pageTitle: 'Structures - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/structureNames',
				icon: 'line_style',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Platform Types',
				cypressLabel: 'types-nav-button',
				pageTitle: 'Platform Types - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/types',
				icon: 'view_module',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Find Elements by Type',
				cypressLabel: 'typesearch-nav-button',
				pageTitle: 'Element Type Search - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/typeSearch',
				icon: 'search',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Reports',
				cypressLabel: 'mimreport-nav-button',
				pageTitle: 'Reports - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/reports',
				icon: 'insert_drive_file',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Cross-References',
				cypressLabel: 'cross-reference-nav-button',
				pageTitle: 'Cross-References - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/crossreference',
				icon: 'compare_arrows',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Transport Types',
				cypressLabel: 'transports-nav-button',
				pageTitle: 'Transport Types - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.MIM_ADMIN],
				routerLink: '/ple/messaging/transports',
				icon: 'timeline',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'Import',
				cypressLabel: 'mimimport-nav-button',
				pageTitle: 'Import - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.MIM_ADMIN],
				routerLink: '/ple/messaging/import',
				icon: 'cloud_upload',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'List Configuration',
				cypressLabel: 'enum-list-config-nav-button',
				pageTitle: 'List Configuration - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.MIM_ADMIN],
				routerLink: '/ple/messaging/lists',
				icon: 'view_list',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'MIM Help',
				cypressLabel: 'help-nav-button',
				pageTitle: 'Help - MIM',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/messaging/help',
				icon: 'help_outline',
				description: '',
				usesBranch: false,
				children: [],
			},
		],
	},
];
