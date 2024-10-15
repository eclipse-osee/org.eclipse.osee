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

export const routes: Routes = [
	{
		path: '',
		title: 'Zenith - Subsystems',
		loadComponent: () => import('./subsystems.component'),
	},
	{
		path: ':branchType',
		title: 'Zenith - Subsystems',
		loadComponent: () => import('./subsystems.component'),
	},
	{
		path: ':branchType/:branchId',
		title: 'Zenith - Subsystems',
		loadComponent: () => import('./subsystems.component'),
	},
	{
		path: ':branchType/:branchId/:ciSet',
		title: 'Zenith - Subsystems',
		loadComponent: () => import('./subsystems.component'),
	},
];

export default routes;
