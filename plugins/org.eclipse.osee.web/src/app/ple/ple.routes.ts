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
const asciidoc = navigationStructure[0].children.find(
	(page) => page.label === 'AsciiDoc Editor'
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
		path: 'asciidoc',
		title: asciidoc?.pageTitle,
		loadChildren: () => import('./asciidoc/asciidoc.routes'),
	},
	{
		path: 'changes',
		loadChildren: () => import('./change-report/change-report.routes'),
	},
];

export default routes;
