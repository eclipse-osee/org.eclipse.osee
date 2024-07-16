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
import { navigationStructure } from '@osee/layout/routing';

const ple = navigationStructure[0].children.find(
	(page) => page.label === 'Product Line Engineering - Home'
);
const plconfig = navigationStructure[0].children.find(
	(page) => page.label === 'Product Line Configuration'
);
const messaging = navigationStructure[0].children.find(
	(page) => page.label === 'Messaging Configuration'
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
		title: ple?.pageTitle,
		loadComponent: () => import('./ple.component'),
	},
	{
		path: 'help',
		title: 'PLE Help',
		loadChildren: () => import('./ple-help/ple-help.routes'),
	},
	{
		path: 'plconfig',
		title: plconfig?.pageTitle,
		loadChildren: () => import('./plconfig/plconfig.routes'),
	},
	{
		path: 'messaging',
		title: messaging?.pageTitle,
		loadChildren: () => import('./messaging/messaging.routes'),
	},
	{
		path: 'artifact/explorer',
		loadChildren: () =>
			import('./artifact-explorer/artifact-explorer.routes'),
	},
];

export default routes;
