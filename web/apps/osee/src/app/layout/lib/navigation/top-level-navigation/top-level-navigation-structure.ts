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

// Adding element(s) requires:
// - Defining the element(s) in this file

// if isDropdown, it has children
// if !isDropdown, it has no children

export const navigationStructure: navigationElement[] = [
	// Level-1
	{
		label: 'Product Line Engineering',
		cypressLabel: 'ple',
		pageTitle: 'OSEE - Product Line Engineering',
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
				pageTitle: 'OSEE - Product Line Engineering',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple',
				icon: 'home',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Artifact Explorer',
				cypressLabel: 'artifactExplorer',
				pageTitle: 'OSEE - Artifact Explorer',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/artifact/explorer',
				icon: 'travel_explore',
				description: '',
				usesBranch: true,
				children: [],
			},
			{
				label: 'MIM',
				cypressLabel: 'messaging',
				pageTitle: 'MIM',
				isDropdown: true,
				isDropdownOpen: false,
				requiredRoles: [],
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
						external: true,
						children: [],
					},
				],
			},
			{
				label: 'Product Line Configuration',
				cypressLabel: 'plconfig',
				pageTitle: 'OSEE - Product Line Configuration',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/ple/plconfig',
				icon: 'widgets',
				description: '',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Product Line Engineering Help',
				cypressLabel: 'ple-help',
				pageTitle: 'OSEE - Product Line Engineering Help',
				isDropdown: true,
				isDropdownOpen: false,
				requiredRoles: [],
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
		pageTitle: 'OSEE - Server Health',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [UserRoles.OSEE_ADMIN],
		routerLink: '/server/health',
		icon: 'monitor_heart',
		description: '',
		usesBranch: false,
		children: [
			{
				label: 'Server Health Dashboard',
				cypressLabel: '',
				pageTitle: 'OSEE - Server Health - Dashboard',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health',
				icon: 'healing',
				description:
					'Provides OSEE Server Health Information and Links',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Status',
				cypressLabel: '',
				pageTitle: 'OSEE - Server Health - Status',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/status',
				icon: 'router',
				description: 'OSEE Server Status',
				usesBranch: false,
				children: [],
			},
			{
				label: 'Balancers',
				cypressLabel: '',
				pageTitle: 'OSEE - Server Health - Balancers',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/balancers',
				icon: 'device_hub',
				description: 'OSEE Server Load Balancer Status',
				usesBranch: false,
				children: [],
			},

			{
				label: 'Usage',
				cypressLabel: '',
				pageTitle: 'OSEE - Server Health - Usage',
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
				pageTitle: 'OSEE - Server Health - DB',
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
				pageTitle: 'OSEE - Server Health - Headers',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: '/server/health/headers',
				icon: 'rss_feed',
				description:
					'OSEE Server and Web Client Communication Protocols',
				usesBranch: false,
				children: [],
			},
		],
	},
	// {									// Uncomment When API Public
	// 	label: 'API Key Management',
	// 	cypressLabel: '',
	// 	pageTitle: 'OSEE - API Key Management',
	// 	isDropdown: false,
	// 	isDropdownOpen: false,
	// 	requiredRoles: [],
	// 	routerLink: '/apiKey',
	// 	icon: 'key',
	// 	description: 'Management of OSEE API Keys',
	// 	children: [],
	// },
];
