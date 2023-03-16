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
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';

import { TypesService } from './types.service';
import { platformTypesMock } from '@osee/messaging/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import {
	transactionBuilderMock,
	transactionMock,
} from '@osee/shared/transactions/testing';

describe('TypesService', () => {
	let service: TypesService;
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
		service = TestBed.inject(TypesService);
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

	it('should create a platform type transaction', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionMock };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.createPlatformType('10', platformTypesMock[0], [])
				)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should create a transaction to change a platform type', () => {
		scheduler.run(() => {
			const expectedfilterValues = { a: transactionMock };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.changePlatformType('10', platformTypesMock[0])
				)
				.toBe(expectedMarble, expectedfilterValues);
		});
	});

	it('should perform a mutation', () => {
		service.performMutation({ branch: '10', txComment: '' }).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});

	it('should fetch logical types', () => {
		service.logicalTypes.subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/logicalType'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should fetch logical type details', () => {
		service.getLogicalTypeFormDetail('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/logicalType/' + 10
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get filtered types', () => {
		service.getFilteredTypes('', '10', 1, 10).subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/types/filter/' +
				'' +
				'?count=10&pageNum=1'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a platform type', () => {
		service.getType('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/types/' + 20
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});
});
