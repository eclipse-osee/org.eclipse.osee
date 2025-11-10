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

const _currentDefFilter = signal('');
const _ciDefId = signal('1');
const _currentPage = signal(1);
const _currentPageSize = signal(10);

export const ciDetailsServiceMock: Partial<CiDetailsService> = {
	scriptDefs: of(defReferenceMock),
	scriptDef: of(defReferenceMock[0]),

	get scriptResults() {
		return of(resultReferenceMock);
	},

	get scriptResultsBySet() {
		return of(resultReferenceMock);
	},

	get ciDefId() {
		return _ciDefId;
	},

	get branchId() {
		return new BehaviorSubject<string>('1');
	},

	get branchType() {
		return new BehaviorSubject<'' | 'working' | 'baseline'>('working');
	},

	get currentDefFilter() {
		return _currentDefFilter;
	},

	get currentPage() {
		return _currentPage;
	},

	get currentPageSize() {
		return _currentPageSize;
	},

	get scriptDefCount() {
		return of(defReferenceMock.length);
	},
};
