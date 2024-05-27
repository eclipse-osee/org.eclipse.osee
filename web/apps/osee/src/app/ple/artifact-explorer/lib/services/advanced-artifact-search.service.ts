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
import { BehaviorSubject } from 'rxjs';
import {
	AdvancedSearchCriteria,
	defaultAdvancedSearchCriteria,
} from '../types/artifact-search';

@Injectable({
	providedIn: 'root',
})
export class AdvancedArtifactSearchService {
	private _advancedSearchCriteria =
		new BehaviorSubject<AdvancedSearchCriteria>({
			...defaultAdvancedSearchCriteria,
		});

	get advancedSearchCriteria() {
		return this._advancedSearchCriteria.asObservable();
	}

	set AdvancedSearchCriteria(criteria: AdvancedSearchCriteria) {
		if (criteria) {
			this._advancedSearchCriteria.next(criteria);
		}
	}
}
