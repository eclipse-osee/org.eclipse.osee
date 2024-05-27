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
import type { PlatformType } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import type { legacyTransaction } from '@osee/transactions/types';
import { of } from 'rxjs';
import { TypesUIService } from '../services/ui/types-ui.service';
import { platformTypes1 } from './platform-types.response.mock';
export const typesUIServiceMock: Partial<TypesUIService> = {
	get types() {
		return of(platformTypes1);
	},
	getType(typeId: string) {
		return of(platformTypes1[0]);
	},

	getTypeFromBranch(branchId: string, typeId: string) {
		return of(platformTypes1[0]);
	},
	changeType(type: PlatformType) {
		return of(transactionResultMock);
	},
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
	searchLocation: of(''),
	detailLocation: of(''),
};
