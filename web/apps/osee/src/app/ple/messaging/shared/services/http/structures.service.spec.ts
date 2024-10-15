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
import { TestBed } from '@angular/core/testing';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';

import { StructuresService } from './structures.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';
import type { structure } from '@osee/messaging/shared/types';
import { structuresMock3 } from '@osee/messaging/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { transactionMock, txMock } from '@osee/transactions/testing';
import { legacyRelation } from '@osee/transactions/types';
import { applicabilitySentinel } from '@osee/applicability/types';

describe('StructuresService', () => {
	let service: StructuresService;
	let httpTestingController: HttpTestingController;
	let scheduler: TestScheduler;

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
		service = TestBed.inject(StructuresService);
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

	it('should get filtered structures', () => {
		const testData: structure[] = [
			{
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				nameAbbrev: {
					id: '-1',
					typeId: '8355308043647703563',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				interfaceMaxSimultaneity: {
					id: '-1',
					typeId: '2455059983007225756',
					gammaId: '-1',
					value: '',
				},
				interfaceMinSimultaneity: {
					id: '-1',
					typeId: '2455059983007225755',
					gammaId: '-1',
					value: '',
				},
				interfaceTaskFileType: {
					id: '-1',
					typeId: '2455059983007225760',
					gammaId: '-1',
					value: 0,
				},
				interfaceStructureCategory: {
					id: '-1',
					typeId: '2455059983007225764',
					gammaId: '-1',
					value: '',
				},
				applicability: applicabilitySentinel,
				elements: [],
			},
		];
		service
			.getFilteredStructures('0', '0', '1', '2', '3', '4', 1, 10)
			.subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				0 +
				'/connections/' +
				'3' +
				'/messages/' +
				1 +
				'/submessages/' +
				2 +
				'/structures' +
				'?count=10' +
				'&pageNum=1' +
				'&viewId=4' +
				'&filter=0'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});

	it('should create a transaction for a structure', () => {
		expect(
			service.addNewStructureToTransaction(
				structuresMock3[0],
				txMock,
				undefined
			)
		).toBe(txMock);
	});

	it('should create a transaction for deleting a submessage relation', () => {
		scheduler.run(({ expectObservable }) => {
			const expectedObservable = { a: transactionMock };
			const expectedMarble = '(a|)';
			expectObservable(
				service.deleteSubmessageRelation('10', '20', '30')
			).toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for deleting a structure', () => {
		scheduler.run(({ expectObservable }) => {
			const expectedObservable = { a: transactionMock };
			const expectedMarble = '(a|)';
			expectObservable(service.deleteStructure('10', '20')).toBe(
				expectedMarble,
				expectedObservable
			);
		});
	});

	it('should create a sub message relation', () => {
		scheduler.run(() => {
			const relation: legacyRelation = {
				typeName: 'Interface SubMessage Content',
				sideA: '10',
				sideB: undefined,
				afterArtifact: 'end',
			};
			const expectedObservable = { a: relation };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createSubMessageRelation('10'))
				.toBe(expectedMarble, expectedObservable);
		});
	});
	it('should create an add Relation transaction', () => {
		scheduler.run(() => {
			const relation: legacyRelation = {
				typeName: 'Interface SubMessage Content',
				sideA: '10',
				sideB: undefined,
				afterArtifact: 'end',
			};
			const expectedObservable = { a: transactionMock };
			const expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.addRelation('10', relation))
				.toBe(expectedMarble, expectedObservable);
		});
	});
	it('should fetch a structure', () => {
		service.getStructure('10', '10', '10', '10', '10', '10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL +
				'/mim/branch/' +
				10 +
				'/connections/' +
				10 +
				'/messages/' +
				10 +
				'/submessages/' +
				10 +
				'/structures/' +
				10 +
				'?viewId=10'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(structuresMock3[0]);
		httpTestingController.verify();
	});

	it('should perform a mutation on the structure endpoint', () => {
		service.performMutation(transactionMock).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
		req.flush({});
		httpTestingController.verify();
	});
});
