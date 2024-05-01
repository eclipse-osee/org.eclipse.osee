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
import { Injectable } from '@angular/core';
import { user } from '@osee/shared/types/auth';
import { of } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class AdditionalAuthService {
	constructor() {}

	public getAuth() {
		return of<user>();
	}
}
