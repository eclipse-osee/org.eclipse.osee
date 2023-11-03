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
import { RoleGuard } from '@osee/auth';
import { ciNavigationStructure } from '@osee/ci-dashboard/navigation';

const importNav = ciNavigationStructure[0].children.find(
	(page) => page.routerLink === '/ci/import'
);

export const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: 'allScripts',
		title: 'CI Dashboard',
		loadChildren: () =>
			import('./lib/components/all-scripts/all-scripts.routes'),
	},
	{
		path: 'dashboard',
		title: 'CI Dashboard',
		loadChildren: () =>
			import('./lib/components/dashboard/dashboard.routes'),
	},
	{
		path: 'import',
		title: 'CI Dashboard',
		canActivate: [RoleGuard],
		data: { requiredRoles: importNav?.requiredRoles },
		loadChildren: () =>
			import(
				'./lib/components/ci-dashboard-import/ci-dashboard-import.routes'
			),
	},
	{
		path: 'results',
		title: 'CI Dashboard',
		loadChildren: () => import('./lib/components/results/results.routes'),
	},
	{
		path: 'diffs',
		title: 'CI Dashboard',
		loadChildren: () =>
			import('./lib/components/set-diffs/set-diffs.routes'),
	},
	{
		path: 'subsystems',
		title: 'CI Dashboard',
		loadChildren: () =>
			import('./lib/components/subsystems/subsystems.routes'),
	},
];

export default routes;
