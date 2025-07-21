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

const messaging = navigationStructure[0].children.filter(
	(c) => c.label === 'MIM'
)[0];
const transports = messaging.children.find(
	(page) => page.label === 'Transport Types'
);
const imports = messaging.children.find((page) => page.label === 'Import');
const reports = messaging.children.find((page) => page.label === 'Reports');
const crossReference = messaging.children.find(
	(page) => page.label === 'Cross-References'
);
const types = messaging.children.find(
	(page) => page.label === 'Platform Types'
);
const structureNames = messaging.children.find(
	(page) => page.label === 'Structures'
);
const typeSearch = messaging.children.find(
	(page) => page.label === 'Find Elements By Type'
);
const connections = messaging.children.find(
	(page) => page.label === 'Connections'
);
const help = messaging.children.find((page) => page.label === 'MIM Help');
const lists = messaging.children.find(
	(page) => page.label === 'List Configuration'
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
		title: messaging?.pageTitle,
		loadComponent: () => import('./messaging.component'),
	},
	{
		path: 'types',
		title: types?.pageTitle,
		loadChildren: () => import('./types-interface/types-page.routes'),
	},
	{
		path: 'connections',
		title: connections?.pageTitle,
		loadChildren: () => import('./connection-view/connection-view.routes'),
	},
	{
		path: 'connections/:branchType/:branchId/:connection/messages',
		title: connections?.pageTitle,
		loadChildren: () => import('./message-tables/message-page.routes'),
	},
	{
		path: 'connections/:branchType/:branchId/:connection/messages/:messageId/:subMessageId/elements',
		title: connections?.pageTitle,
		loadChildren: () =>
			import('./structure-tables/structure-tables.routes'),
	},
	{
		path: 'typeSearch',
		title: typeSearch?.pageTitle,
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: ':branchType/typeSearch',
		title: typeSearch?.pageTitle,
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: ':branchType/:branchId/typeSearch',
		title: typeSearch?.pageTitle,
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: 'help',
		title: help?.pageTitle,
		loadChildren: () => import('./messaging-help/messaging-help.routes'),
	},
	{
		path: 'structureNames',
		title: structureNames?.pageTitle,
		loadChildren: () => import('./structure-names/structure-names.routes'),
	},
	{
		path: ':branchType/:branchId/type/:typeId',
		title: 'MIM - Type Detail View',
		loadChildren: () => import('./type-detail/type-detail.routes'),
	},
	{
		path: 'reports',
		title: reports?.pageTitle,
		loadChildren: () => import('./reports/reports.routes'),
	},
	{
		path: 'crossreference',
		title: crossReference?.pageTitle,
		loadChildren: () => import('./cross-reference/cross-reference.routes'),
	},
	{
		path: 'import',
		title: imports?.pageTitle,
		canActivate: [RoleGuard],
		data: { requiredRoles: imports?.requiredRoles },
		loadChildren: () => import('./import/import.routes'),
	},
	{
		path: 'transports',
		title: transports?.pageTitle,
		canActivate: [RoleGuard],
		data: { requiredRoles: imports?.requiredRoles },
		loadChildren: () => import('./pages/transports/transports.routes'),
	},
	{
		path: 'lists',
		title: lists?.pageTitle,
		canActivate: [RoleGuard],
		data: { requiredRoles: imports?.requiredRoles },
		loadChildren: () =>
			import('./list-configuration/list-configuration.routes'),
	},
];

export default routes;
