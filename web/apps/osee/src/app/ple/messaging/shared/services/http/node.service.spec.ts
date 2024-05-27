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
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';

import { NodeService } from './node.service';
import { transactionMock, txMock } from '@osee/transactions/testing';
import { nodesMock } from '@osee/messaging/shared/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('NodeService', () => {
	let service: NodeService;
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
		service = TestBed.inject(NodeService);
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

	describe('Core Functionality', () => {
		describe('Fetching data', () => {
			it('should get all nodes', () => {
				service.getNodes('10').subscribe();
				const req = httpTestingController.expectOne(
					apiURL + '/mim/branch/' + 10 + '/nodes/'
				);
				expect(req.request.method).toEqual('GET');
				req.flush([]);
				httpTestingController.verify();
			});

			it('should get a node', () => {
				service.getNode('10', '10').subscribe();
				const req = httpTestingController.expectOne(
					apiURL + '/mim/branch/' + 10 + '/nodes/' + 10
				);
				expect(req.request.method).toEqual('GET');
				req.flush({});
				httpTestingController.verify();
			});
		});
		describe('Adding data', () => {
			it('should add a node', () => {
				expect(
					service.addNewNodeToTransaction(
						nodesMock[0],
						txMock,
						undefined
					)
				).toBe(txMock);
			});
		});

		describe('Modifying data', () => {
			it('should create a transaction to delete a node', () => {
				scheduler.run(() => {
					const expectedfilterValues = { a: transactionMock };
					const expectedMarble = '(a|)';
					scheduler
						.expectObservable(service.deleteArtifact('10', '15'))
						.toBe(expectedMarble, expectedfilterValues);
				});
			});

			it('should perform a mutation', () => {
				service
					.performMutation({ branch: '10', txComment: '' })
					.subscribe();
				const req = httpTestingController.expectOne(
					apiURL + '/orcs/txs'
				);
				expect(req.request.method).toEqual('POST');
				req.flush({});
				httpTestingController.verify();
			});
		});
	});
});
