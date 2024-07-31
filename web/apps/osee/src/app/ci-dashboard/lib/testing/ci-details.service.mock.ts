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
import { BehaviorSubject, of } from 'rxjs';
import { defReferenceMock, resultReferenceMock } from './tmo.response.mock';
import { CiDetailsService } from '../services/ci-details.service';
import { signal } from '@angular/core';

export const ciDetailsServiceMock: Partial<CiDetailsService> = {
	scriptDefs: of(defReferenceMock),
	_scriptDefs: of(defReferenceMock),
	_scriptDefCount: of(0),
	scriptDef: of(defReferenceMock[0]),
	_scriptResults: of(resultReferenceMock),

	get scriptResults() {
		return this._scriptResults;
	},

	get ciDefId() {
		return signal('1');
	},

	get branchId() {
		return new BehaviorSubject<string>('1');
	},

	get branchType() {
		return new BehaviorSubject<'' | 'working' | 'baseline'>('working');
	},

	get currentPage() {
		return new BehaviorSubject<number>(1);
	},

	get currentPageSize() {
		return new BehaviorSubject<number>(10);
	},

	get scriptDefCount() {
		return this._scriptDefCount;
	},
};
