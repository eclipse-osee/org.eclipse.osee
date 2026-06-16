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
import { Routes } from '@angular/router';
import { navigationStructure } from '@osee/layout/routing';

const explorer = navigationStructure[0].children.find(
	(page) => page.label === 'Artifact Explorer'
);

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: '',
		children: [
			/**
			 * Author: Kris Graham (kgraha16)
			 * Task 160 - Created route for Advanced Search Page
			 *
			 * Author: Eihab Khudhair (ekhudhai)
			 * Task 162 - Move the Advanced Search Form implementations into the Advanced Search Page
			 * Ensure Advanced Search route is registered in the same route scope as Artifact Explorer
			 */
			{
				path: 'search',
				title: 'Advanced Search',
				loadComponent: () =>
					import('./advanced-search-page/advanced-search-page.component').then(
						(m) => m.AdvancedSearchPageComponent
					),
			},
			/**
			 * Author: Eihab Khudhair (ekhudhai)
			 * Task 162 - Support Advanced Search navigation when usesBranch=true appends branch segments to the URL.
			 * This allows routes like: /ple/artifact/explorer/search/<branchType>/<branchId>
			 */
			{
				path: 'search/:branchType',
				title: 'Advanced Search',
				loadComponent: () =>
					import('./advanced-search-page/advanced-search-page.component').then(
						(m) => m.AdvancedSearchPageComponent
					),
			},
			/**
			 * Author: Eihab Khudhair (ekhudhai)
			 * Task 162 - Support Advanced Search navigation when usesBranch=true appends branch type + id.
			 */
			{
				path: 'search/:branchType/:branchId',
				title: 'Advanced Search',
				loadComponent: () =>
					import('./advanced-search-page/advanced-search-page.component').then(
						(m) => m.AdvancedSearchPageComponent
					),
			},
			{
				path: '',
				title: explorer?.pageTitle,
				loadComponent: () => import('./artifact-explorer.component'),
			},
			{
				path: ':branchType',
				title: explorer?.pageTitle,
				loadComponent: () => import('./artifact-explorer.component'),
			},
			{
				path: ':branchType/:branchId',
				title: explorer?.pageTitle,
				loadComponent: () => import('./artifact-explorer.component'),
			},
		],
	},
];

export default routes;
