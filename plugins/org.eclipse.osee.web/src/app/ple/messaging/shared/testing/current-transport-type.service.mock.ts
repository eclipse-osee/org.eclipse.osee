/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import { Observable, of } from 'rxjs';
import { transactionResult } from '@osee/shared/types/change-report';
import { CurrentTransportTypeService } from '../services/ui/current-transport-type.service';
import { transportType } from '@osee/messaging/shared/types';
import { transportTypes } from './transport-type.service.mock';
import { transactionResultMock } from '@osee/shared/transactions/testing';

export const CurrentTransportTypeServiceMock: Partial<CurrentTransportTypeService> =
	{
		getType: function (artId: string): Observable<Required<transportType>> {
			return of(transportTypes[0]);
		},
		getPaginatedTypes(pageNum, pageSize) {
			return of(transportTypes);
		},
		createType: function (
			type: transportType
		): Observable<transactionResult> {
			return of(transactionResultMock);
		},
		transportTypes: of(transportTypes),
		types: of(transportTypes),
	};
