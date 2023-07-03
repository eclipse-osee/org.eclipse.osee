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
import { diffReportResolverFn } from '@osee/shared/resolvers';

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
	},
	{ path: '', loadComponent: () => import('./list-configuration.component') },
	{
		path: ':branchType',
		loadComponent: () => import('./list-configuration.component'),
	},
	{
		path: ':branchType/:branchId',
		redirectTo: ':branchType/:branchId/-1',
	},
	{
		path: ':branchType/:branchId/:viewId',
		loadComponent: () => import('./list-configuration.component'),
	},
	{
		path: '',
		loadComponent: () => import('@osee/diff/views'),
		outlet: 'rightSideNav',
	},
];

export default routes;
