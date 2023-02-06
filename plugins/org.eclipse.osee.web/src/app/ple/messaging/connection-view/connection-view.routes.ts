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
import { DiffReportResolver } from '@osee/shared/resolvers';

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./toolbar.routes'),
		outlet: 'toolbar',
	},
	{ path: '', loadComponent: () => import('./connection-view.component') },
	{
		path: ':branchType',
		loadComponent: () => import('./connection-view.component'),
	},
	{
		path: ':branchType/:branchId',
		loadComponent: () => import('./connection-view.component'),
	},
	{
		path: ':branchType/:branchId/diff',
		loadComponent: () => import('./connection-view.component'),
		resolve: {
			diff: DiffReportResolver,
		},
	},
	{
		path: '',
		loadComponent: () =>
			import('../../diff-views/single-diff/single-diff.component'),
		outlet: 'rightSideNav',
	},
	{
		path: '',
		loadComponent: () =>
			import('../shared/headers/usermenu/usermenu.component'),
		outlet: 'userMenu',
	},
	{
		path: '',
		loadComponent: () => import('@osee/messaging/shared/headers'),
		outlet: 'navigationHeader',
	},
];

export default routes;
