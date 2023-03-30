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
import navigationStructure from '../../../layout/lib/navigation/top-level-navigation/top-level-navigation-structure';

const help = navigationStructure[0].children
	.filter((c) => c.label === 'Messaging Configuration')[0]
	.children.find((page) => page.label === 'Help');

const routes: Routes = [
	{
		path: '',
		loadChildren: () =>
			import('../../../layout/lib/toolbar/toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'overview',
	},
	{
		path: ':helpPage',
		loadComponent: () => import('./messaging-help.component'),
		title: help?.pageTitle,
	},
];

export default routes;
