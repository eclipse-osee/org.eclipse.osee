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
import { transaction } from '@osee/shared/types';
import { transactionMock } from '@osee/shared/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { apiURL } from '@osee/environments';

import { ConnectionService } from './connection.service';

describe('ConnectionService', () => {
	let service: ConnectionService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
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
						let relation = {
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

			it('should create a valid connection', () => {
				scheduler.run(() => {
					let extransaction: transaction = {
						branch: '10',
						txComment:
							'Create Connection and Relate to Node(s): ' +
							'Hello' +
							' , ' +
							'Hello' +
							' and Relate to Transport Type: ' +
							'Hello',
						createArtifacts: [
							{
								typeId: '126164394421696910',
								name: 'connection',
								applicabilityId: undefined,
								attributes: [
									{ typeName: 'Description', value: '' },
								],
								relations: [
									{ typeName: 'blah', sideB: 'Hello' },
									{ typeName: 'blah', sideB: 'Hello' },
									{ typeName: 'blah', sideB: 'Hello' },
								],
								key: undefined,
							},
						],
					};
					const expectedfilterValues = { a: extransaction };
					const expectedMarble = '(a|)';
					scheduler
						.expectObservable(
							service.createConnection(
								'10',
								{ name: 'connection', description: '' },
								[
									{ typeName: 'blah', sideB: 'Hello' },
									{ typeName: 'blah', sideB: 'Hello' },
									{ typeName: 'blah', sideB: 'Hello' },
								]
							)
						)
						.toBe(expectedMarble, expectedfilterValues);
				});
			});

			it('should create a valid connection with no relations', () => {
				scheduler.run(() => {
					let extransaction: transaction = {
						branch: '10',
						txComment: 'Create Connection',
						createArtifacts: [
							{
								typeId: '126164394421696910',
								name: 'connection',
								applicabilityId: undefined,
								attributes: [
									{ typeName: 'Description', value: '' },
									{
										typeName: 'Interface Transport Type',
										value: {
											name: 'ETHERNET',
											byteAlignValidation: false,
											byteAlignValidationSize: 0,
											messageGeneration: false,
											messageGenerationPosition: '',
											messageGenerationType: '',
										},
									},
								],
								relations: [],
								key: '10',
							},
						],
					};
					const expectedfilterValues = { a: extransaction };
					const expectedMarble = '(a|)';
					scheduler
						.expectObservable(
							service.createConnectionNoRelations(
								'10',
								{
									name: 'connection',
									description: '',
									transportType: {
										name: 'ETHERNET',
										byteAlignValidation: false,
										byteAlignValidationSize: 0,
										messageGeneration: false,
										messageGenerationPosition: '',
										messageGenerationType: '',
									},
								},
								undefined,
								'10'
							)
						)
						.toBe(expectedMarble, expectedfilterValues);
				});
			});

			it('should create a valid connection change', () => {
				scheduler.run(() => {
					let extransaction: transaction = {
						branch: '10',
						txComment: 'Change connection attributes',
						modifyArtifacts: [
							{
								id: '1',
								applicabilityId: undefined,
								setAttributes: [
									{ typeName: 'Name', value: 'connection' },
									{ typeName: 'Description', value: '' },
									{
										typeName: 'Interface Transport Type',
										value: {
											name: 'ETHERNET',
											byteAlignValidation: false,
											byteAlignValidationSize: 0,
											messageGeneration: false,
											messageGenerationPosition: '',
											messageGenerationType: '',
										},
									},
								],
							},
						],
					};
					const expectedfilterValues = { a: extransaction };
					const expectedMarble = '(a|)';
					scheduler
						.expectObservable(
							service.changeConnection('10', {
								id: '1',
								name: 'connection',
								description: '',
								transportType: {
									name: 'ETHERNET',
									byteAlignValidation: false,
									byteAlignValidationSize: 0,
									messageGeneration: false,
									messageGenerationPosition: '',
									messageGenerationType: '',
								},
							})
						)
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

		it('should create a delete relation transaction', () => {
			scheduler.run(() => {
				let relation = {
					typeName: 'Interface Connection Secondary Node',
					sideB: '10',
					sideA: undefined,
				};
				let transaction = transactionMock;
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
