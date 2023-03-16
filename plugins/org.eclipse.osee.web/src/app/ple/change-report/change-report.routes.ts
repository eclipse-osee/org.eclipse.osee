/*********************************************************************
 * Copyright (c) 2022 Boeing
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

const routes: Routes = [
	{
		path: '',
		loadChildren: () => import('@osee/toolbar'),
		outlet: 'toolbar',
	},
	{ path: '', loadComponent: () => import('./change-report.component') },
	{
		path: ':branchType',
		loadComponent: () => import('./change-report.component'),
	},
	{
		path: ':branchType/:branchId',
		loadComponent: () => import('./change-report.component'),
	},
];

export default routes;
