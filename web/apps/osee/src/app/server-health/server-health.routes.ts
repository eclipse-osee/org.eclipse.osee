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

const health = navigationStructure.find(
	(page) => page.label === 'Server Health'
);
const status = health?.children.find((page) => page.label === 'Status');
const balancers = health?.children.find((page) => page.label === 'Balancers');
const headers = health?.children.find((page) => page.label === 'Http Headers');
const usage = health?.children.find((page) => page.label === 'Usage');
const db = health?.children.find((page) => page.label === 'Database');

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
		pathMatch: 'full',
	},
	{
		path: '',
		title: health?.pageTitle,
		loadComponent: () => import('./server-health.component'),
	},
	{
		path: 'status',
		title: status?.pageTitle,
		loadChildren: () =>
			import('./server-health-status/server-health-status.routes'),
	},
	{
		path: 'balancers',
		title: balancers?.pageTitle,
		loadChildren: () =>
			import('./server-health-balancers/server-health-balancers.routes'),
	},
	{
		path: 'headers',
		title: headers?.pageTitle,
		loadChildren: () =>
			import('./server-health-headers/server-health-headers.routes'),
	},
	{
		path: 'usage',
		title: usage?.pageTitle,
		loadChildren: () =>
			import('./server-health-usage/server-health-usage.routes'),
	},
	{
		path: 'database',
		title: db?.pageTitle,
		loadChildren: () =>
			import('./server-health-database/server-health-database.routes'),
	},
];

export default routes;
