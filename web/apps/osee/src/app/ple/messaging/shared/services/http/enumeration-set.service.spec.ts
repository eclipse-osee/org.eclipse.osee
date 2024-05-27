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
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { TestScheduler } from 'rxjs/testing';

import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { enumerationSetMock } from '@osee/messaging/shared/testing';
import { txMock } from '@osee/transactions/testing';
import { legacyRelation } from '@osee/transactions/types';
import { EnumerationSetService } from './enumeration-set.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('EnumerationSetService', () => {
	let service: EnumerationSetService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(EnumerationSetService);
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

	it('should create an enum set', () => {
		expect(
			service.createEnumSet(enumerationSetMock[0], txMock, undefined)
		).toBe(txMock);
	});

	it('should create an enum', () => {
		expect(
			service.createEnum(
				enumerationSetMock[0].enumerations[0],
				txMock,
				undefined
			)
		).toBe(txMock);
	});

	it('should create PlatformType To EnumSet Relation', () => {
		scheduler.run(() => {
			const relation: legacyRelation = {
				typeName: 'Interface Platform Type Enumeration Set',
				sideB: '10',
				sideA: '11',
			};
			const expectedFilterValues = { a: relation };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.createPlatformTypeToEnumSetRelation('10', '11')
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should create Enum To EnumSet Relation', () => {
		scheduler.run(() => {
			const relation: legacyRelation = {
				typeName: 'Interface Enumeration Definition',
				sideA: '10',
				sideB: '11',
			};
			const expectedFilterValues = { a: relation };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(
					service.createEnumToEnumSetRelation('10', '11')
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should fetch an array of enumsets', () => {
		service.getEnumSets('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/enumerations/?orderBy=' +
				ATTRIBUTETYPEIDENUM.NAME
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get a single enum set', () => {
		service.getEnumSet('10', '20').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/branch/' + 10 + '/types/' + 20 + '/enumeration'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should perform a mutation', () => {
		service.performMutation({ branch: '10', txComment: '' }).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});
});
