/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import { ciNavigationStructure } from '@osee/ci-dashboard/navigation';
import { navigationElement } from '@osee/shared/types';
import { UserRoles } from '@osee/shared/types/auth';
import { mimNavigationStructure } from 'src/app/ci-dashboard/lib/navigation/mim-navigation-structure';

// Adding element(s) requires:
// - Defining the element(s) in this file

// if isDropdown, it has children
// if !isDropdown, it has no children

export const navigationStructure: navigationElement[] = [
	// Level-1
	{
		label: 'Product Line Engineering',
		cypressLabel: 'ple',
		pageTitle: 'Product Line Engineering',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: '/ple',
		icon: 'public',
		description: '',
		usesBranch: false,
		children: [
			// Level-2
			{
				label: 'Product Line Engineering Home',
				cypressLabel: 'ple-home',
				pageTitle: 'Product Line Engineering',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.OSEE_CUSTOMER],
				routerLink: '/ple',
				icon: 'home',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Artifact Explorer',
				cypressLabel: 'artifactExplorer',
				pageTitle: 'Artifact Explorer',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.OSEE_CUSTOMER],
				routerLink: '/ple/artifact/explorer',
				icon: 'travel_explore',
				description: '',
				usesBranch: true,
				children: [],
			},
			...mimNavigationStructure,
			{
				label: 'Product Line Configuration',
				cypressLabel: 'plconfig',
				pageTitle: 'Product Line Configuration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.PLE_USER],
				routerLink: '/ple/plconfig',
				icon: 'widgets',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Product Line Engineering Help',
				cypressLabel: 'ple-help',
				pageTitle: 'Product Line Engineering Help',
				isDropdown: true,
				isDropdownOpen: false,
				requiredRoles: [UserRoles.PLE_USER],
				routerLink: '/ple/help',
				icon: 'help_outline',
				description: '',
				usesBranch: false,
				children: [
					{
						label: 'BAT Tool Help',
						cypressLabel: 'bat',
						pageTitle: 'BAT Tool Help',
						isDropdown: false,
						isDropdownOpen: false,
						requiredRoles: [],
						routerLink: '/ple/help/bat',
						icon: 'sports_cricket',
						description: '',
						usesBranch: false,
						children: [],
					},
				],
			},
		],
	},
	...ciNavigationStructure,
	{
		label: 'Server Health',
		cypressLabel: '',
		pageTitle: 'Server Health',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [UserRoles.OSEE_ADMIN, UserRoles.OSEE_CUSTOMER],
		routerLink: '/server/health',
		icon: 'monitor_heart',
		description: '',
		usesBranch: false,
		children: [
			{
				label: 'Server Health Dashboard',
				cypressLabel: '',
				pageTitle: 'Server Health - Dashboard',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health',
				icon: 'healing',
				description: 'Provides Server Health Information and Links',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Status',
				cypressLabel: '',
				pageTitle: 'Server Health - Status',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/status',
				icon: 'router',
				description: 'Server Status',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Balancers',
				cypressLabel: '',
				pageTitle: 'Server Health - Balancers',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/balancers',
				icon: 'device_hub',
				description: 'Server Load Balancer Status',
				usesBranch: false,
				children: [],
			},

			{
				label: 'Usage',
				cypressLabel: '',
				pageTitle: 'Server Health - Usage',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/usage',
				icon: 'people',
				description: 'OSEE Server Usage Report In The Last 1 Month',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Database',
				cypressLabel: '',
				pageTitle: 'Server Health - DB',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/database',
				icon: 'table_chart',
				description: 'OSEE Database Information',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Http Headers',
				cypressLabel: '',
				pageTitle: 'Server Health - Headers',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/headers',
				icon: 'rss_feed',
				description: 'Server and Web Client Communication Protocols',
				usesBranch: false,
				children: [],
			},
		],
	},
	// {									// Uncomment When API Public
	// 	label: 'API Key Management',
	// 	cypressLabel: '',
	// 	pageTitle: 'API Key Management',
	// 	isDropdown: false,
	// 	isDropdownOpen: false,
	// 	requiredRoles: [],
	// 	routerLink: '/apiKey',
	// 	icon: 'key',
	// 	description: 'Management of API Keys',
	// 	children: [],
	// },
];
