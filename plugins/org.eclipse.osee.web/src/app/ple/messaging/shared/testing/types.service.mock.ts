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
import { transaction } from '../../../../transactions/transaction';
import {
	transactionMock,
	transactionResultMock,
} from '../../../../transactions/transaction.mock';
import { TypesService } from '../services/http/types.service';
import { PlatformType } from '../types/platformType';
import { logicalTypeMock } from './logical-type.response.mock';
import { logicalTypeFormDetailMock } from './logical-type-form-detail.response.mock';
import { platformTypes1 } from './platform-types.response.mock';

export const typesServiceMock: Partial<TypesService> = {
	performMutation(body: transaction) {
		return of(transactionResultMock);
	},
	getFilteredTypes(filter: string, branchId: string) {
		return of(platformTypes1);
	},
	getTypes(branchId: string) {
		return of(platformTypes1);
	},
	getType(branchId: string, platformTypeId: string) {
		return of(platformTypes1[0]);
	},
	changePlatformType(branchId: string, type: Partial<PlatformType>) {
		return of(transactionMock);
	},
	createPlatformType(
		branchId: string,
		type: PlatformType | Partial<PlatformType>
	) {
		return of(transactionMock);
	},
	getLogicalTypeFormDetail(id: string) {
		return of(logicalTypeFormDetailMock);
	},
	logicalTypes: of(logicalTypeMock),
};
