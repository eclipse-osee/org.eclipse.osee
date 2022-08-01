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
  routerLink: string,
  children: navigationElement[]
}

const navigationStructure:navigationElement[] = [
  // Level-1
  {
    label: 'Product Line Engineering',
    isDropdown: true,
    isDropdownOpen: false,
    routerLink: '/ple',
    children: [
      // Level-2
      {
        label: 'Product Line Engineering - Home',
        isDropdown: false,
        isDropdownOpen: false,
        routerLink: '/ple',
        children:[]
      },
      {
        label: 'Product Line Configuration',
        isDropdown: false,
        isDropdownOpen: false,
        routerLink: '/ple/plconfig',
        children:[]
      },
      {
        label: 'Messaging Configuration',
        isDropdown: true,
        isDropdownOpen: false,
        routerLink: '/ple/messaging',
        children:[
          // Level-3
          {
            label: 'Message Configuration - Home',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging',
            children:[]
          },
          {
            label: 'Connection View',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/connections',
            children:[]
          },
          {
            label: 'Type View',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/types',
            children:[]
          },
          {
            label: 'Help Pages',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/help',
            children:[]
          },
          {
            label: 'Structure Names Page',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/structureNames',
            children:[]
          },
          {
            label: 'Find Elements by Type',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/typeSearch',
            children:[]
          },
          {
            label: 'Go to Reports',
            isDropdown: false,
            isDropdownOpen: false,
            routerLink: '/ple/messaging/reports',
            children:[]
          }
        ]
      }
    ]
  }
]; 
export default navigationStructure;