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
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OKTA_AUTH } from '@okta/okta-angular';
import { defer, switchMap, take, timer } from 'rxjs';

@Component({
	selector: 'osee-auto-login',
	standalone: true,
	imports: [CommonModule],
	templateUrl: './auto-login.component.html',
})
export class AutoLoginComponent {
	private _oktaAuth = inject(OKTA_AUTH);

	autoLogin = timer(1000).pipe(
		take(1),
		switchMap(() => defer(() => this.signIn())),
		take(1)
	);
	protected async signIn(): Promise<void> {
		await this._oktaAuth.signInWithRedirect();
	}
}
export default AutoLoginComponent;
