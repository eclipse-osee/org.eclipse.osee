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
import { TypesUIService } from '../services/ui/types-ui.service';
import {
	transactionMock,
	transactionResultMock,
} from '../../../../transactions/transaction.mock';
import { response } from '../../connection-view/mocks/Response.mock';
import { platformTypes1 } from '../../type-element-search/testing/MockResponses/PlatformType';
import { TypesService } from '../services/http/types.service';
import { PlatformType } from '../types/platformType';
import { transaction } from '../../../../transactions/transaction';
export const typesUIServiceMock: Partial<TypesUIService> = {
	get types() {
		return this._types;
	},
	getType(typeId: string) {
		return of(platformTypes1[0]);
	},

	getTypeFromBranch(branchId: string, typeId: string) {
		return of(platformTypes1[0]);
	},
	changeType(type: Partial<PlatformType>) {
		return of(transactionMock);
	},
	performMutation(body: transaction) {
		return of(transactionResultMock);
	},
};
