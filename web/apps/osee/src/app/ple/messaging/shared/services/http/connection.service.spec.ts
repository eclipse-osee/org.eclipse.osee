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
import { transactionMock } from '@osee/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';

import { ConnectionService } from './connection.service';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('ConnectionService', () => {
	let service: ConnectionService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});
		service = TestBed.inject(ConnectionService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	describe('Core Functionality', () => {
		describe('Modifying data', () => {
			describe('should create a valid node relation', () => {
				it('should create a node relation', () => {
					scheduler.run(() => {
						const relation = {
							typeName: 'Interface Connection Node',
							sideB: '10',
							sideA: undefined,
						};
						const expectedObservable = { a: relation };
						const expectedMarble = '(a|)';
						scheduler
							.expectObservable(service.createNodeRelation('10'))
							.toBe(expectedMarble, expectedObservable);
					});
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

		it('should create a delete relation transaction', () => {
			scheduler.run(() => {
				const relation = {
					typeName: 'Interface Connection Secondary Node',
					sideB: '10',
					sideA: undefined,
				};
				const transaction = transactionMock;
				transaction.txComment = 'Unrelating Connection';
				transaction.deleteRelations = [
					{
						typeName: 'Interface Connection Secondary Node',
						typeId: undefined,
						aArtId: undefined,
						bArtId: '10',
						rationale: undefined,
					},
				];
				const expectedObservable = { a: transaction };
				const expectedMarble = '(a|)';
				scheduler
					.expectObservable(service.deleteRelation('10', relation))
					.toBe(expectedMarble, expectedObservable);
			});
		});
	});
});
