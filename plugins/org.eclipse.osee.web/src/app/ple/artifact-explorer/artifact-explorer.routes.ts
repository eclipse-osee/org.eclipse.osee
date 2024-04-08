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
];

export default routes;
