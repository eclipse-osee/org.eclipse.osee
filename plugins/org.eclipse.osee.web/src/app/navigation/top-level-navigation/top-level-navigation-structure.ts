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
  cypressLabel: string,
  pageTitle:string,
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
    cypressLabel: 'ple',
    pageTitle:'OSEE - Product Line Engineering',
    isDropdown: true,
    isDropdownOpen: false,
    isAdminRequired: false,
    routerLink: '/ple',
    icon: '',
    children: [
      // Level-2
      {
        label: 'Product Line Engineering - Home',
        cypressLabel: 'ple-home',
        pageTitle:'OSEE - Product Line Engineering',
        isDropdown: false,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple',
        icon: '',
        children:[]
      },
      {
        label: 'Product Line Configuration',
        cypressLabel: 'plconfig',
        pageTitle:'OSEE - Product Line Configuration',
        isDropdown: false,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple/plconfig',
        icon: '',
        children:[]
      },
      {
        label: 'Messaging Configuration',
        cypressLabel: 'messaging',
        pageTitle:'OSEE - MIM',
        isDropdown: true,
        isDropdownOpen: false,
        isAdminRequired: false,
        routerLink: '/ple/messaging',
        icon: '',
        children:[
          // Level-3
          {
            label: 'Message Configuration - Home',
            cypressLabel: 'messaging',
            pageTitle:'OSEE - MIM',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging',
            icon: 'home',
            children:[]
          },
          {
            label: 'Connection View',
            cypressLabel: 'connection-nav-button',
            pageTitle:'OSEE - MIM - Connection View',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/connections',
            icon: 'device_hub',
            children:[]
          },
          {
            label: 'Type View',
            cypressLabel: 'types-nav-button',
            pageTitle:'OSEE - MIM - Type View',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/types',
            icon: 'view_module',
            children:[]
          },
          {
            label: 'Structure Names',
            cypressLabel: 'structure-nav-button',
            pageTitle:'OSEE - MIM - Structure Names',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/structureNames',
            icon: 'line_style',
            children:[]
          },
          {
            label: 'Find Elements by Type',
            cypressLabel: 'typesearch-nav-button',
            pageTitle:'OSEE - MIM - Element TypeSearch',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/typeSearch',
            icon: 'search',
            children:[]
          },
          {
            label: 'Transport Type Manager',
            cypressLabel: 'transports-nav-button',
            pageTitle:'OSEE - MIM - Transport Type Manager',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: true,
            routerLink: '/ple/messaging/transports',
            icon: 'timeline',
            children:[]
          },
          {
            label: 'Reports',
            cypressLabel: 'mimreport-nav-button',
            pageTitle:'OSEE - MIM - Reports',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/reports',
            icon: 'insert_drive_file',
            children:[]
          },
          {
            label: 'Import Page',
            cypressLabel: 'mimimport-nav-button',
            pageTitle:'OSEE - MIM - Importer',
            isDropdown: false,
            isDropdownOpen: false,
            isAdminRequired: true,
            routerLink: '/ple/messaging/import',
            icon: 'cloud_upload',
            children:[]
          },
          {
            label: 'Help',
            cypressLabel: 'help-nav-button',
            pageTitle:'OSEE - MIM - Help',
            isDropdown: true,
            isDropdownOpen: false,
            isAdminRequired: false,
            routerLink: '/ple/messaging/help',
            icon: 'help_outline',
            children: [
              {
                label: 'Help - Home',
                cypressLabel: 'help-nav-button',
                pageTitle: 'OSEE - MIM - Help',
                isDropdown: false,
                isDropdownOpen: false,
                isAdminRequired: false,
                routerLink: '/ple/messaging/help',
                icon: 'home_outline',
                children:[]
              },
              {
                label: 'Column Descriptions',
                cypressLabel: 'column-descriptions',
                pageTitle: 'OSEE - MIM - Help - Column Descriptions',
                isDropdown: false,
                isDropdownOpen: false,
                isAdminRequired: false,
                routerLink: '/ple/messaging/help/columnDescriptions',
                icon: 'view_column',
                children:[]
              }
            ]
          },
        ]
      }
    ]
  }
]; 
export default navigationStructure;