/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { combineLatest } from 'rxjs';
import { filter, switchMap, debounceTime } from 'rxjs/operators';
import { PlatformTypesService } from './http/platform-types.service';
import { BranchIdService } from './router/branch-id.service';
import { SearchService } from './router/search.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentElementSearchService {
	private _elements = combineLatest([
		this.idService.BranchId,
		this.searchService.searchTerm,
	]).pipe(
		debounceTime(500),
		filter(
			([id, searchTerm]) =>
				id !== '' &&
				!isNaN(Number(id)) &&
				Number(id) > 0 &&
				searchTerm !== ''
		),
		switchMap(([id, search]) =>
			this.platformTypesService.getFilteredElements(search, id)
		)
	);
	constructor(
		private idService: BranchIdService,
		private platformTypesService: PlatformTypesService,
		private searchService: SearchService
	) {}

	get elements() {
		return this._elements;
	}
}
