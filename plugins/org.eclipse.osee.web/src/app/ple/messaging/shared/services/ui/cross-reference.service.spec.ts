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
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { TransactionService } from '@osee/shared/transactions';
import {
	transactionResultMock,
	transactionServiceMock,
} from '@osee/shared/transactions/testing';
import { CrossReferenceHttpServiceMock } from '../../testing/cross-reference.http.service.mock';
import { CrossReferenceService } from './cross-reference.service';
import {
	crossReferencesMock,
	preferencesUiServiceMock,
	sharedConnectionServiceMock,
} from '@osee/messaging/shared/testing';
import {
	ConnectionService,
	CrossReferenceHttpService,
	PreferencesUIService,
} from '@osee/messaging/shared';

describe('CrossReferenceService', () => {
	let service: CrossReferenceService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: CrossReferenceHttpService,
					useValue: CrossReferenceHttpServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: ConnectionService,
					useValue: sharedConnectionServiceMock,
				},
				{
					provide: PreferencesUIService,
					useValue: preferencesUiServiceMock,
				},
			],
		});
		service = TestBed.inject(CrossReferenceService);
	});
	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should create a cross reference', () => {
		scheduler.run(() => {
			service.BranchId = '10';
			service.SelectedConnectionId = '123';
			const expected = { a: transactionResultMock };
			scheduler
				.expectObservable(
					service.createCrossReference(crossReferencesMock[0])
				)
				.toBe('(a|)', expected);
		});
	});

	it('should create a connection relation', () => {
		scheduler.run(() => {
			const connectionId = '123';
			const crossRefId = '456';
			const expected = {
				a: {
					typeName: 'Interface Connection Cross Reference',
					sideA: connectionId,
					sideB: crossRefId,
				},
			};
			scheduler
				.expectObservable(
					service.createConnectionRelation(connectionId, crossRefId)
				)
				.toBe('(a|)', expected);
		});
	});
});
