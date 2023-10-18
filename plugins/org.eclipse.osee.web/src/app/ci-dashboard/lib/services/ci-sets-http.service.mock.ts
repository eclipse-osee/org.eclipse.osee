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
import { of } from 'rxjs';
import { setsMock } from '../testing/tmo.response.mock';
import { CiSetsHttpService } from './ci-sets-http.service';

export const ciSetsHttpServiceMock: Partial<CiSetsHttpService> = {
	getCiSets(branchId, activeOnly) {
		return of(setsMock);
	},
};
