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

export const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
		pathMatch: 'full',
	},
	{
		path: '',
		title: 'CI Dashboard',
		redirectTo: 'allScripts',
		pathMatch: 'prefix',
	},
	{
		path: 'allScripts',
		title: 'CI Dashboard',
		loadComponent: () => import('./ci-dashboard.component'),
	},
	{
		path: 'dashboard',
		title: 'CI Dashboard',
		loadComponent: () =>
			import('./lib/components/dashboard/dashboard.component'),
	},
];

export default routes;
