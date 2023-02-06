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
import { CrossReference } from '@osee/messaging/shared';
import { connectionMock } from '@osee/messaging/shared/testing';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { BehaviorSubject, of } from 'rxjs';
import { CrossReferenceService } from '../services/cross-reference.service';
import { crossReferencesMock } from './cross-references.mock';

export const CrossReferenceServiceMock: Partial<CrossReferenceService> = {
	createCrossReference(crossRef: CrossReference) {
		return of(transactionResultMock);
	},
	get crossReferences() {
		return of(crossReferencesMock);
	},
	get selectedConnectionId() {
		return new BehaviorSubject<string>('123');
	},
	connections: of([connectionMock]),
};
