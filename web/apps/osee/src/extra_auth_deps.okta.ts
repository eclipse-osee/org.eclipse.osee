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
import { ImportProvidersSource } from '@angular/core';
import { OktaAuthModule } from '@okta/okta-angular';
import { OktaAuth } from '@okta/okta-auth-js';
import {
	clientId,
	issuer,
	pkce,
	postLogoutRedirectUri,
	redirectUri,
	responseType,
} from './okta';
const oktaAuth = new OktaAuth({
	issuer: issuer,
	clientId: clientId,
	redirectUri: redirectUri,
	postLogoutRedirectUri: postLogoutRedirectUri,
	pkce: pkce,
	responseType: responseType,
});
export const extra_auth_deps: ImportProvidersSource[] = [
	OktaAuthModule.forRoot({ oktaAuth }),
];
