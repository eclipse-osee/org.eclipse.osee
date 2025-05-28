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
import { RoleGuard } from '@osee/auth';
import { UserRoles } from '@osee/shared/types/auth';
export const routes: Routes = [
	{
		path: '',
		loadComponent: () => import('@osee/toolbar/component'),
		children: [
			{
				path: '',
				loadComponent: () =>
					import('@osee/messaging/shared/preferences'),
				outlet: 'userMenu',
			},
			{
				path: '',
				canMatch: [RoleGuard],
				data: { requiredRoles: [UserRoles.OSEE_CUSTOMER] },
				loadComponent: () => import('@osee/toolbar/logo'),
				outlet: 'toolbarLogo',
			},
			{
				path: '',
				loadComponent: () => import('@osee/messaging/shared/headers'),
				outlet: 'navigationHeader',
			},
		],
	},
	{
		path: '**',
		redirectTo: '',
	},
];

export default routes;
