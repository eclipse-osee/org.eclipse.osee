/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { navigationStructure } from '@osee/layout/routing';
import { auth_routes } from './auth.routes';

const serverHealth = navigationStructure[1];
export const routes: Routes = [
	{
		path: '', //todo remove when main app page is made
		redirectTo: 'ple',
		pathMatch: 'prefix',
	},
	{
		path: 'world',
		loadChildren: () => import('./world/world.routes'),
	},
	{
		path: 'ci',
		loadChildren: () => import('./ci-dashboard/ci-dashboard.routes'),
	},
	{
		path: 'diff-report',
		loadChildren: () => import('./diff-report/diff-report.routes'),
	},
	{
		path: 'ple',
		loadChildren: () => import('./ple/ple.routes'),
	},
	{
		path: 'mnc',
		loadChildren: () => import('./mnc/mnc.routes'),
	},
	{
		path: 'training',
		loadChildren: () => import('./training/training.routes'),
	},
	{
		path: '404',
		loadChildren: () => import('@osee/page-not-found'),
	},
	{
		path: 'about',
		loadChildren: () => import('@osee/about'),
	},
	{
		path: 'gc',
		loadChildren: () => import('./grid-commander/grid-commander.routes'),
	},
	{
		path: 'apiKey',
		loadChildren: () =>
			import('./api-key-management/api-key-management.routes'),
	},
	{
		path: 'server/health',
		canActivate: [RoleGuard],
		data: { requiredRoles: serverHealth?.requiredRoles },
		loadChildren: () => import('./server-health/server-health.routes'),
	},
	...auth_routes,
	{
		path: '',
		loadChildren: () => import('@osee/layout'),
		outlet: 'TopLevelNav',
	},
	{
		path: '**',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
	},
	{
		path: '**',
		redirectTo: '404',
	},
];

export default routes;
