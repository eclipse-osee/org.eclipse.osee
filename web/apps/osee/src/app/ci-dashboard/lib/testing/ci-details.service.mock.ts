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
	scriptDef: of(defReferenceMock[0]),

	get scriptResults() {
		return of(resultReferenceMock);
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

	get currentDefFilter() {
		return signal('');
	},

	get currentPage() {
		return signal(1);
	},

	get currentPageSize() {
		return signal(10);
	},

	get scriptDefCount() {
		return of(defReferenceMock.length);
	},
};
