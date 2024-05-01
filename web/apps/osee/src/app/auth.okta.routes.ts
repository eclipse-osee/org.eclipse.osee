/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { OktaCallbackComponent } from '@okta/okta-angular';

export const auth_routes: Routes = [
	{ path: 'login/callback', component: OktaCallbackComponent },
	{ path: 'login', loadComponent: () => import('@osee/auto-login') },
];
