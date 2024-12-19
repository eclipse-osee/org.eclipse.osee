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
import { of } from 'rxjs';
import { TypesService } from '../services/http/types.service';
import { logicalTypeMock } from './logical-type.response.mock';
import { logicalTypeFormDetailMock } from './logical-type-form-detail.response.mock';
import { platformTypes1 } from './platform-types.response.mock';
import type { PlatformType } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import { legacyTransaction, transaction } from '@osee/transactions/types';

export const typesServiceMock: Partial<TypesService> = {
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
	getPaginatedFilteredTypes(filter: string, branchId: string) {
		return of(platformTypes1);
	},
	getTypes(branchId: string) {
		return of(platformTypes1);
	},
	getType(branchId: string, platformTypeId: string) {
		return of(platformTypes1[0]);
	},
	addNewPlatformTypeToTransaction(
		type: PlatformType,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	getLogicalTypeFormDetail(id: string) {
		return of(logicalTypeFormDetailMock);
	},
	logicalTypes: of(logicalTypeMock),
};
