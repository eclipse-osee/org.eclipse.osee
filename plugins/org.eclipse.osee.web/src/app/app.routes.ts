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

export const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./layout/lib/toolbar/toolbar.routes'),
		outlet: 'toolbar',
		pathMatch: 'full',
	},
	{
		path: '',
		loadChildren: () => import('./layout/lib/navigation/navigation.routes'),
		outlet: 'TopLevelNav',
	},
	{
		path: 'ple',
		loadChildren: () => import('./ple/ple.routes'),
	},
	{
		path: 'training',
		loadChildren: () => import('./training/training.routes'),
	},
	{
		path: '', //todo remove when main app page is made
		redirectTo: 'ple',
		pathMatch: 'full',
	},
	{
		path: '404',
		loadChildren: () => import('./page-not-found/page-not-found.routes'),
	},
	{
		path: 'about',
		loadChildren: () => import('./about/about.routes'),
	},
	{
		path: 'gc',
		loadChildren: () => import('./grid-commander/grid-commander.routes'),
	},
	{
		path: '**',
		loadChildren: () => import('./layout/lib/toolbar/toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: '**',
		redirectTo: '404',
	},
];

export default routes;
