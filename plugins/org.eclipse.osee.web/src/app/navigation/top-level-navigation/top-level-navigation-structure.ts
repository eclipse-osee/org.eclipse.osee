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

// Adding element(s) requires:
// - Defining the element(s) in this file 

// if isDropdown, it has children
// if !isDropdown, it has no children

export interface navigationElement {
  label: string,
  isDropdown: boolean,
  isDropdownOpen: boolean,
  isAdminRequired: boolean,
  routerLink: string,
  icon: string,
  children: navigationElement[]
}

const navigationStructure:navigationElement[] = [
  // Level-1
  {
    label: 'Product Line Engineering',
    isDropdown: true,
    isDropdownOpen: false,
    isAdminRequired: false,
    routerLink: '/ple',
    icon: '',
    children: [
      // Level-2
      {
        label: 'Product Line Engineering - Home',
        isDropdown: false,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple',
        icon: '',
        children:[]
      },
      {
        label: 'Product Line Configuration',
        isDropdown: false,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple/plconfig',
        icon: '',
        children:[]
      },
      {
        label: 'Messaging Configuration',
        isDropdown: true,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple/messaging',
        icon: '',
        children:[
          // Level-3
          {
            label: 'Message Configuration - Home',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging',
            icon: '',
            children:[]
          },
          {
            label: 'Connection View',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/connections',
            icon: 'device_hub',
            children:[]
          },
          {
            label: 'Type View',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/types',
            icon: 'view_module',
            children:[]
          },
          {
            label: 'Structure Names',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/structureNames',
            icon: 'line_style',
            children:[]
          },
          {
            label: 'Find Elements by Type',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/typeSearch',
            icon: 'search',
            children:[]
          },
          {
            label: 'Reports',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/reports',
            icon: 'insert_drive_file',
            children:[]
          },
          {
            label: 'Help',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/help',
            icon: 'help_outline',
            children:[]
          },
          {
            label: 'Import Page',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: true,
            routerLink: '/ple/messaging/import',
            icon: 'cloud_upload',
            children:[]
          }
        ]
      }
    ]
  }
]; 
export default navigationStructure;