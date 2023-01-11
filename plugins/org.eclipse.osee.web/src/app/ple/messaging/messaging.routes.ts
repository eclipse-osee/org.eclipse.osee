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
import { RoleGuard } from 'src/app/auth/role.guard';
import navigationStructure from '../../layout/lib/navigation/top-level-navigation/top-level-navigation-structure';

const messaging = navigationStructure[0].children.filter(
	(c) => c.label === 'Messaging Configuration'
)[0];
const transports = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Transport Type Manager');
const imports = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Import Page');
const reports = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Reports');
const types = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Type View');
const structureNames = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Structure Names');
const typeSearch = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Find Elements By Type');
const connections = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Connection View');
const help = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Help');
const routes: Routes = [
	{
		path: '',
		title: messaging?.pageTitle,
		loadComponent: () => import('./messaging.component'),
	},
	{
		path: ':branchType/:branchId/:connection/messages/:messageId/:subMessageId/elements',
		title: connections?.pageTitle || 'OSEE',
		loadChildren: () =>
			import('./structure-tables/structure-tables.routes'),
	},
	{
		path: ':branchType/:branchId/:connection/messages',
		title: connections?.pageTitle || 'OSEE',
		loadChildren: () => import('./message-tables/message-page.routes'),
	},
	{
		path: 'types',
		title: types?.pageTitle || 'OSEE',
		loadChildren: () => import('./types-interface/types-interface.routes'),
	},
	{
		path: 'connections',
		title: connections?.pageTitle || 'OSEE',
		loadChildren: () => import('./connection-view/connection-view.routes'),
	},
	{
		path: 'typeSearch',
		title: typeSearch?.pageTitle || 'OSEE',
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: ':branchType/typeSearch',
		title: typeSearch?.pageTitle || 'OSEE',
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: ':branchType/:branchId/typeSearch',
		title: typeSearch?.pageTitle || 'OSEE',
		loadChildren: () =>
			import('./type-element-search/type-element-search.routes'),
	},
	{
		path: 'help',
		title: help?.pageTitle || 'OSEE',
		loadChildren: () => import('./messaging-help/messaging-help.routes'),
	},
	{
		path: 'structureNames',
		title: structureNames?.pageTitle || 'OSEE',
		loadChildren: () => import('./structure-names/structure-names.routes'),
	},
	{
		path: ':branchType/:branchId/type/:typeId',
		title: 'OSEE - MIM - Type Detail View',
		loadChildren: () => import('./type-detail/type-detail.routes'),
	},
	{
		path: 'reports',
		title: reports?.pageTitle || 'OSEE',
		loadChildren: () => import('./reports/reports.routes'),
	},
	{
		path: 'import',
		title: imports?.pageTitle || 'OSEE',
		canActivate: [RoleGuard],
		data: { requiredRoles: imports?.requiredRoles },
		loadChildren: () => import('./import/import.routes'),
	},
	{
		path: 'transports',
		title: transports?.pageTitle || 'OSEE',
		canActivate: [RoleGuard],
		data: { requiredRoles: imports?.requiredRoles },
		loadChildren: () => import('./transports/transports.routes'),
	},
];

export default routes;
