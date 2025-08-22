/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { CiDetailsService } from './ci-details.service';

@Injectable({
	providedIn: 'root',
})
export class CiDetailsListService extends CiDetailsService {
	set page(page: number) {
		this._currentPage.set(page);
	}

	set pageSize(size: number) {
		this._currentPageSize.set(size);
	}
}
