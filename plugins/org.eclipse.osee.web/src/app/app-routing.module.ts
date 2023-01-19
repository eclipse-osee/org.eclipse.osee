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
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('./layout/lib/toolbar/toolbar.routes'),
		outlet: 'toolbar',
	},
	{
		path: '',
		loadChildren: () => import('./layout/lib/navigation/navigation.routes'),
		outlet: 'TopLevelNav',
	},
	{
		path: 'ple',
		loadChildren: () => import('./ple/ple.module').then((m) => m.PleModule),
	},
	{
		path: '', //todo remove when main app page is made
		redirectTo: 'ple',
		pathMatch: 'full',
	},
	{
		path: '404',
		loadChildren: () =>
			import('./page-not-found/page-not-found.module').then(
				(m) => m.PageNotFoundModule
			),
	},
	{
		path: 'about',
		loadChildren: () =>
			import('./about/about.module').then((m) => m.AboutModule),
	},
	{
		path: 'gc',
		loadChildren: () =>
			import('./grid-commander/grid-commander.module').then(
				(m) => m.GridCommanderModule
			),
	},
	{
		path: '**',
		redirectTo: '404',
	},
];

@NgModule({
	imports: [
		RouterModule.forRoot(routes, {
			anchorScrolling: 'enabled',
			scrollPositionRestoration: 'enabled',
			scrollOffset: [0, 256],
		}),
	],
	exports: [RouterModule],
})
export class AppRoutingModule {}
