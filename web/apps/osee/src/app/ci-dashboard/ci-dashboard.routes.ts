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
const adminNav = ciNavigationStructure[0].children.find(
	(page) => page.routerLink === '/ci/admin'
);

export const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: 'timeline',
		title: 'Zenith',
		loadChildren: () =>
			import('./lib/components/timelines/timelines.routes'),
	},
	{
		path: 'allScripts',
		title: 'Zenith',
		loadChildren: () =>
			import('./lib/components/all-scripts/all-scripts.routes'),
	},
	{
		path: 'results',
		title: 'Zenith',
		loadChildren: () => import('./lib/components/results/results.routes'),
	},
	{
		path: 'dashboard',
		title: 'Zenith',
		loadChildren: () =>
			import('./lib/components/dashboard/dashboard.routes'),
	},
	{
		path: 'import',
		title: 'Zenith',
		canActivate: [RoleGuard],
		data: { requiredRoles: importNav?.requiredRoles },
		loadChildren: () =>
			import(
				'./lib/components/ci-dashboard-import/ci-dashboard-import.routes'
			),
	},
	{
		path: 'admin',
		title: 'Zenith',
		canActivate: [RoleGuard],
		data: { requiredRoles: adminNav?.requiredRoles },
		loadChildren: () => import('./lib/components/ci-admin/ci-admin.routes'),
	},
	{
		path: 'batches',
		title: 'Zenith',
		loadChildren: () => import('./lib/components/batches/batches.routes'),
	},
	{
		path: 'diffs',
		title: 'Zenith',
		loadChildren: () =>
			import('./lib/components/set-diffs/set-diffs.routes'),
	},
	{
		path: 'subsystems',
		title: 'Zenith',
		loadChildren: () =>
			import('./lib/components/subsystems/subsystems.routes'),
	},
];

export default routes;
