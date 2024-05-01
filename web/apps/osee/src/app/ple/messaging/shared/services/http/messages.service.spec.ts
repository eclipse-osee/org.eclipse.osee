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
import { TransactionBuilderService } from '@osee/shared/transactions';
import { relation } from '@osee/shared/types';
import {
	transactionBuilderMock,
	transactionMock,
} from '@osee/shared/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';

import { MessagesService } from './messages.service';

describe('MessagesService', () => {
	let service: MessagesService;
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
		service = TestBed.inject(MessagesService);
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

	it('should create a connection relation', () => {
		scheduler.run(() => {
			let relation: relation = {
				typeName: 'Interface Connection Message',
				sideA: '10',
				sideB: undefined,
				afterArtifact: 'end',
			};
			let expectedObservable = { a: relation };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createConnectionRelation('10'))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for a message', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createMessage('10', {}, []))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for a message change', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.changeMessage('10', {}))
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

	it('should create a delete message transaction', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.deleteMessage('10', '20'))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should perform a mutation', () => {
		service.performMutation({ branch: '10', txComment: '' }).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get filtered messages', () => {
		service.getFilteredMessages('', '10', '10', '10', 1, 10).subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/connections/' +
				10 +
				'/messages' +
				'?pageNum=1&count=10&viewId=10'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a specific message', () => {
		service.getMessage('10', '10', '10', '10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/connections/' +
				10 +
				'/messages/' +
				10 +
				'?viewId=10'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a connection', () => {
		service.getConnectionName('10', '10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/connections/' + 10
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
