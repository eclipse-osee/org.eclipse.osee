/*********************************************************************
 * Copyright (c) 2026 Boeing
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

const profile = navigationStructure.find((page) => page.label === 'Profile');

const certs = profile?.children.find(
	(page) => page.label === 'Certificate Management'
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
		title: certs?.pageTitle || 'OSEE',
		loadComponent: () =>
			import(
				'./components/user-public-certificate-management/user-public-certificate-management.component'
			).then((m) => m.UserPublicCertificateManagementComponent),
	},
];

export default routes;
