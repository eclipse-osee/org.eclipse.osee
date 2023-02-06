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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TransactionBuilderService, relation } from '@osee/shared/transactions';
import {
	transactionBuilderMock,
	transactionMock,
} from '@osee/shared/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '../../../../../../environments/environment';

import { SubMessagesService } from './sub-messages.service';

describe('SubMessagesService', () => {
	let service: SubMessagesService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
			],
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(SubMessagesService);
		httpTestingController = TestBed.inject(HttpTestingController);
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

	it('should create a message relation', () => {
		scheduler.run(() => {
			let relation: relation = {
				typeName: 'Interface Message SubMessage Content',
				sideA: '10',
				sideB: '10',
				afterArtifact: 'end',
			};
			let expectedObservable = { a: relation };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createMessageRelation('10', '10'))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a submessage creation transaction', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createSubMessage('10', {}, []))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a submessage change transaction', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.changeSubMessage('10', {}))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a delete relation transaction', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.deleteRelation('10', {
						typeId: '12345',
						sideA: 'abcde',
						sideB: 'abcde',
					})
				)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a delete sub message transaction', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.deleteSubMessage('10', '20'))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a relation', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.addRelation('10', {
						typeName: 'Interface Message SubMessage Content',
						sideA: '10',
					})
				)
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should perform a mutation on the sub message endpoint', () => {
		service.performMutation('10', '10', '10', transactionMock).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a submessage', () => {
		service.getSubMessage('10', '20', '30', '40').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/connections/' +
				20 +
				'/messages/' +
				30 +
				'/submessages/' +
				40
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
