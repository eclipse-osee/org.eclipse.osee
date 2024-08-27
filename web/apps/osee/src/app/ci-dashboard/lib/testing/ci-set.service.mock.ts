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
import { of } from 'rxjs';
import { CiSetsService } from '../services/ci-sets.service';
import { setsMock } from './tmo.response.mock';
import { transactionResultMock } from '@osee/transactions/testing';
import { CISet } from '../types';

export const ciSetServiceMock: Partial<CiSetsService> = {
	createCISet(ciSet: CISet) {
		return of(transactionResultMock);
	},
	get ciSets() {
		return of(setsMock);
	},
	get adminCiSets() {
		return of(setsMock);
	},
	get activeOnly() {
		return of(false);
	},
	set ActiveOnly(activeOnly: boolean) {},
};
