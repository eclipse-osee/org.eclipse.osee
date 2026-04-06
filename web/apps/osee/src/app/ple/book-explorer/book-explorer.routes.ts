/*********************************************************************
 * Copyright (c) 2026 Boeing
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

const publishing = navigationStructure[0].children.find(
	(page) => page.label === 'Publishing'
);
const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
		pathMatch: 'full',
	},
	{
		path: '',
		loadComponent: () => import('./book-explorer.component'),
		title: publishing?.pageTitle,
	},
	{
		path: ':branchType',
		title: publishing?.pageTitle,
		loadComponent: () => import('./book-explorer.component'),
	},
	{
		path: ':branchType/:branchId',
		title: publishing?.pageTitle,
		loadComponent: () => import('./book-explorer.component'),
	},
];

export default routes;
