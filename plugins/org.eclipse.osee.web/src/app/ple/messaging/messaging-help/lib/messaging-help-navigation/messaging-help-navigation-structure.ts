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

// if isDropdown, it has children
// if !isDropdown, it has no children

export const helpNavigationStructure: navigationElement[] = [
	// Level-1
	{
		label: 'Help Overview',
		cypressLabel: 'help-overview',
		pageTitle: 'OSEE - MIM - Help - Overview',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'overview',
		icon: 'home',
		children: [],
	},
	{
		label: 'ICD Revisions',
		cypressLabel: 'help-revisions-header',
		pageTitle: 'OSEE - MIM - Help - ICD Revisions',
		isDropdown: true,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'icdrevision',
		icon: 'edit',
		children: [
			{
				label: 'Start an ICD Revision',
				cypressLabel: 'help-revision',
				pageTitle: 'OSEE - MIM - Help - Start an ICD revision',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'icdrevision',
				icon: 'edit',
				children: [],
			},
			{
				label: 'Edit a Structure/Element',
				cypressLabel: 'help-edit-structures',
				pageTitle: 'OSEE - MIM - Help - Edit Structure/Element',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'edit_structure',
				icon: 'edit',
				children: [],
			},
			{
				label: 'Add an Element',
				cypressLabel: 'help-add-element',
				pageTitle: 'OSEE - MIM - Help - Add an Element',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'add_element',
				icon: 'add',
				children: [],
			},
			{
				label: 'Add/Edit Platform Type',
				cypressLabel: 'help-add-edit-platform-type',
				pageTitle: 'OSEE - MIM - Help - Add/Edit Platform Type',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'add_edit_types',
				icon: 'add',
				children: [],
			},
			{
				label: 'Add a Message/SubMessage/Structure',
				cypressLabel: 'help-add-other',
				pageTitle:
					'OSEE - MIM - Help - Add a Message/SubMessage/Structure',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'add_msg_submsg_struct',
				icon: 'add',
				children: [],
			},
			{
				label: 'Using Cross Reference Data Manager',
				cypressLabel: 'help-using-cross-ref-data-mgr',
				pageTitle: 'OSEE - MIM - Help - Add/Edit Platform Type',
				isDropdown: false,
				isDropdownOpen: false,
				requiredRoles: [],
				routerLink: 'using_cross_ref_mgr',
				icon: 'sync_alt',
				children: [],
			},
		],
	},
	{
		label: 'Connections Page',
		cypressLabel: 'help-connections-dropdown',
		pageTitle: 'OSEE - MIM - Help - Overview',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'connections',
		icon: 'device_hub',
		children: [],
	},
	{
		label: 'Type View Page',
		cypressLabel: 'help-type-view-page',
		pageTitle: 'OSEE - MIM - Help - Type View Page',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'type_view_page',
		icon: 'view_module',
		children: [],
	},
	{
		label: 'Structures List Page',
		cypressLabel: 'help-structures',
		pageTitle: 'OSEE - MIM - Help - Structures',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'structures',
		icon: 'line_style',
		children: [],
	},
	{
		label: 'Find Elements By Type Page',
		cypressLabel: 'help-find-elements-by-type',
		pageTitle: 'OSEE - MIM - Help - Find Element By Type',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'find_elements_by_type',
		icon: 'search',
		children: [],
	},
	{
		label: 'Reports',
		cypressLabel: 'help-reports',
		pageTitle: 'OSEE - MIM - Help - Reports',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'reports',
		icon: 'insert_drive_file',
		children: [],
	},
	{
		label: 'Traceability',
		cypressLabel: 'help-traceability',
		pageTitle: 'OSEE - MIM - Help - Traceability',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'traceability',
		icon: 'sync_alt',
		children: [],
	},
	{
		label: 'User Settings',
		cypressLabel: 'help-user-settings',
		pageTitle: 'OSEE - MIM - Help - User Settings',
		isDropdown: false,
		isDropdownOpen: false,
		requiredRoles: [],
		routerLink: 'user_settings',
		icon: 'settings',
		children: [],
	},
];
export default helpNavigationStructure;
