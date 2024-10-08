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
		loadComponent: () => import('@osee/toolbar/component'),
		children: [
			{
				path: '',
				loadComponent: () =>
					import(
						'./lib/components/ci-nav-header/ci-nav-header.component'
					),
				outlet: 'navigationHeader',
			},
			{
				path: '',
				loadComponent: () => import('./ci-logo/ci-logo.component'),
				outlet: 'toolbarLogo',
			},
		],
	},
	{
		path: '**',
		redirectTo: '',
	},
];

export default routes;
