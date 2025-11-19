/*********************************************************************
 * Copyright (c) 2025 Boeing
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
		loadChildren: () => import('./toolbar.routes'),
		outlet: 'toolbar',
	},
	{ path: '', redirectTo: 'world', pathMatch: 'prefix' },
	{
		path: 'world',
		loadChildren: () => import('./world/world.routes'),
	},
	{
		path: 'workflow',
		loadChildren: () => import('./workflow/workflow.routes'),
	},
	{
		path: 'action/create',
		loadChildren: () =>
			import(
				'./actra-create-action-page/actra-create-action-page.routes'
			),
	},
];

export default routes;
