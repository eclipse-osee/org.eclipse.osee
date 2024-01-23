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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { OKTA_AUTH, OktaAuthStateService } from '@okta/okta-angular';
import { AuthState } from '@okta/okta-auth-js';
import { filter, map } from 'rxjs';

@Component({
	selector: 'osee-okta-sign',
	standalone: true,
	imports: [AsyncPipe, MatButtonModule],
	templateUrl: './okta-sign.component.html',
	styles: [':host{ width: 100%; height: 100%; display:inline-block}'],
})
export class OktaSignComponent {
	private _oktaStateService = inject(OktaAuthStateService);

	private _oktaAuth = inject(OKTA_AUTH);

	protected isAuthenticated = this._oktaStateService.authState$.pipe(
		filter((s: AuthState) => !!s),
		map((s: AuthState) => s.isAuthenticated ?? false)
	);
	protected async signIn(): Promise<void> {
		await this._oktaAuth.signInWithRedirect();
	}

	protected async signOut(): Promise<void> {
		await this._oktaAuth.signOut();
	}
}
