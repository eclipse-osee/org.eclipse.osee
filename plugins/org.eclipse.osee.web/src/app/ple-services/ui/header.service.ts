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

import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { headerDetail } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class HeaderService {
	getHeaderByName<T>(headers: headerDetail<T>[], key: keyof T) {
		return of(headers.find((h) => h.header === key));
	}
}
