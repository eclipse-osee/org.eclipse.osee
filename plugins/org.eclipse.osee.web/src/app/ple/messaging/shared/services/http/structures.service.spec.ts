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
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';

import { StructuresService } from './structures.service';
import { HttpClient } from '@angular/common/http';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from 'src/environments/environment';
import { structure } from '@osee/messaging/shared/types';
import { structuresMock3 } from '@osee/messaging/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { relation } from '@osee/shared/types';
import {
	transactionBuilderMock,
	transactionMock,
} from '@osee/shared/transactions/testing';

describe('StructuresService', () => {
	let service: StructuresService;
	let httpClient: HttpClient;
	let httpTestingController: HttpTestingController;
	let scheduler: TestScheduler;

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
		service = TestBed.inject(StructuresService);
		httpClient = TestBed.inject(HttpClient);
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
		let testData: structure[] = [
			{
				id: '0',
				name: 'name',
				elements: [],
				description: 'description',
				interfaceMaxSimultaneity: '1',
				interfaceMinSimultaneity: '0',
				interfaceStructureCategory: '1',
				interfaceTaskFileType: 1,
			},
		];
		service.getFilteredStructures('0', '0', '1', '2', '3').subscribe();
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
				'/structures/filter/' +
				0
		);
		expect(req.request.method).toEqual('GET');
		req.flush(testData);
		httpTestingController.verify();
	});

	it('should create a transaction for a structure', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createStructure({}, '10', []))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for a structure modification', () => {
		scheduler.run(() => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.changeStructure({}, '10'))
				.toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for deleting a submessage relation', () => {
		scheduler.run(({ expectObservable }) => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			expectObservable(
				service.deleteSubmessageRelation('10', '20', '30')
			).toBe(expectedMarble, expectedObservable);
		});
	});

	it('should create a transaction for deleting a structure', () => {
		scheduler.run(({ expectObservable }) => {
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			expectObservable(service.deleteStructure('10', '20')).toBe(
				expectedMarble,
				expectedObservable
			);
		});
	});

	it('should create a sub message relation', () => {
		scheduler.run(() => {
			let relation: relation = {
				typeName: 'Interface SubMessage Content',
				sideA: '10',
				sideB: undefined,
				afterArtifact: 'end',
			};
			let expectedObservable = { a: relation };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.createSubMessageRelation('10'))
				.toBe(expectedMarble, expectedObservable);
		});
	});
	it('should create an add Relation transaction', () => {
		scheduler.run(() => {
			let relation: relation = {
				typeName: 'Interface SubMessage Content',
				sideA: '10',
				sideB: undefined,
				afterArtifact: 'end',
			};
			let expectedObservable = { a: transactionMock };
			let expectedMarble = '(a|)';
			scheduler
				.expectObservable(service.addRelation('10', relation))
				.toBe(expectedMarble, expectedObservable);
		});
	});
	it('should fetch a structure', () => {
		service.getStructure('10', '10', '10', '10', '10').subscribe();
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
				10
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
