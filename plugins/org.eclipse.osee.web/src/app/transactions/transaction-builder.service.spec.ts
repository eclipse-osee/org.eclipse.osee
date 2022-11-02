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
import { transaction } from './transaction';

import { TransactionBuilderService } from './transaction-builder.service';

describe('TransactionBuilderService', () => {
	let service: TransactionBuilderService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(TransactionBuilderService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('Creating artifacts', () => {
		let testTransaction: transaction;
		let artifact1: any;
		beforeEach(() => {
			testTransaction = {
				branch: '10',
				txComment: 'Comment',
				createArtifacts: [
					{
						name: 'artifact123',
						typeId: '23456',
						applicabilityId: '25',
						attributes: [
							{
								typeName: 'Random Property',
								value: 'random',
							},
							{
								typeName: 'Interface Transport Type',
								value: 'ETHERNET',
							},
						],
						relations: [],
						key: undefined,
					},
				],
			};
			artifact1 = {
				name: 'artifact123',
				applicabilityId: '25',
				randomProperty: 'random',
				transportType: 'ETHERNET',
			};
		});
		it('should create a new transaction', () => {
			expect(
				service.createArtifact(
					artifact1,
					'23456',
					[],
					undefined,
					'10',
					'Comment'
				)
			).toEqual(testTransaction);
		});
		it('should improperly create a new transaction', () => {
			expect(service.createArtifact(artifact1, '23456', [])).toEqual({
				branch: '',
				txComment: '',
				createArtifacts: testTransaction.createArtifacts,
			});
		});
		it('should create a new transaction containing two artifacts', () => {
			expect(
				service.createArtifacts(
					[artifact1, { name: 'artifact456', applicabilityId: '10' }],
					[[], []],
					'10',
					'Comment',
					['23456', '23456']
				)
			).toEqual({
				branch: '10',
				txComment: 'Comment',
				createArtifacts: [
					{
						name: 'artifact123',
						typeId: '23456',
						applicabilityId: '25',
						attributes: [
							{
								typeName: 'Random Property',
								value: 'random',
							},
							{
								typeName: 'Interface Transport Type',
								value: 'ETHERNET',
							},
						],
						relations: [],
						key: undefined,
					},
					{
						name: 'artifact456',
						applicabilityId: '10',
						typeId: '23456',
						attributes: [],
						relations: [],
						key: undefined,
					},
				],
			});
		});
	});

	describe('Modifying artifacts', () => {
		let testTransaction: transaction;
		let blankTransaction: transaction;
		let artifact1: any;
		beforeEach(() => {
			blankTransaction = {
				branch: '',
				txComment: '',
				modifyArtifacts: [
					{
						id: '10',
						applicabilityId: '5',
						setAttributes: [
							{
								typeName: 'Name',
								value: 'artifact123',
							},
							{
								typeName: 'Random Property',
								value: 'random',
							},
							{
								typeName: 'Interface Transport Type',
								value: 'ETHERNET',
							},
						],
					},
				],
			};
			testTransaction = {
				branch: '10',
				txComment: 'Comment',
				modifyArtifacts: [
					{
						id: '10',
						applicabilityId: '5',
						setAttributes: [
							{
								typeName: 'Name',
								value: 'artifact123',
							},
							{
								typeName: 'Random Property',
								value: 'random',
							},
							{
								typeName: 'Interface Transport Type',
								value: 'ETHERNET',
							},
						],
					},
				],
			};
			artifact1 = {
				id: '10',
				name: 'artifact123',
				applicability: {
					id: '5',
					name: 'applic',
				},
				randomProperty: 'random',
				transportType: 'ETHERNET',
			};
		});
		it('should create a new modification to an artifact', () => {
			expect(
				service.modifyArtifact(artifact1, undefined, '10', 'Comment')
			).toEqual(testTransaction);
		});
		it('should create a modification to an artifact without branch, or txComment', () => {
			expect(
				service.modifyArtifact(
					artifact1,
					undefined,
					undefined,
					undefined
				)
			).toEqual(blankTransaction);
		});
		it('should create only 1 artifact', () => {
			expect(
				service.modifyArtifacts([artifact1], '10', 'Comment')
			).toEqual(testTransaction);
		});
	});

	describe('Deleting artifacts', () => {
		it('should delete an artifact', () => {
			expect(
				service.deleteArtifact('12345', undefined, '10', 'Comment')
			).toEqual({
				branch: '10',
				txComment: 'Comment',
				deleteArtifacts: ['12345'],
			});
		});
		it('should improperly delete an artifact', () => {
			expect(service.deleteArtifact('12345')).toEqual({
				branch: '',
				txComment: '',
				deleteArtifacts: ['12345'],
			});
		});
		it('should delete an artifact reusing a previous transaction', () => {
			let prevTransaction = {
				branch: '10',
				txComment: 'Comment',
				deleteArtifacts: ['12345'],
			};
			expect(service.deleteArtifact('456', prevTransaction)).toEqual({
				branch: '10',
				txComment: 'Comment',
				deleteArtifacts: ['12345', '456'],
			});
		});

		it('should delete multiple artifacts', () => {
			expect(
				service.deleteArtifacts(['12345', '456'], '10', 'Comment')
			).toEqual({
				branch: '10',
				txComment: 'Comment',
				deleteArtifacts: ['12345', '456'],
			});
		});
	});

	it('should add a relation', () => {
		expect(
			service.addRelation(
				'hello',
				'9',
				'7',
				'8',
				'blah',
				undefined,
				'10',
				'Comment'
			)
		).toEqual({
			branch: '10',
			txComment: 'Comment',
			addRelations: [
				{
					typeName: 'hello',
					typeId: '9',
					aArtId: '7',
					bArtId: '8',
					rationale: 'blah',
				},
			],
		});
	});

	it('should add a relation and delete an artifact', () => {
		let prevTransaction = {
			branch: '10',
			txComment: 'Comment',
			deleteArtifacts: ['12345'],
		};
		expect(
			service.addRelation(
				'hello',
				'9',
				'7',
				'8',
				'blah',
				prevTransaction,
				'10',
				'Comment'
			)
		).toEqual({
			branch: '10',
			txComment: 'Comment',
			deleteArtifacts: ['12345'],
			addRelations: [
				{
					typeName: 'hello',
					typeId: '9',
					aArtId: '7',
					bArtId: '8',
					rationale: 'blah',
				},
			],
		});
	});

	it('should delete a relation', () => {
		expect(
			service.deleteRelation(
				'hello',
				'9',
				'7',
				'8',
				'blah',
				undefined,
				'10',
				'Comment'
			)
		).toEqual({
			branch: '10',
			txComment: 'Comment',
			deleteRelations: [
				{
					typeName: 'hello',
					typeId: '9',
					aArtId: '7',
					bArtId: '8',
					rationale: 'blah',
				},
			],
		});
	});

	it('should delete a relation and delete an artifact', () => {
		let prevTransaction = {
			branch: '10',
			txComment: 'Comment',
			deleteArtifacts: ['12345'],
		};
		expect(
			service.deleteRelation(
				'hello',
				'9',
				'7',
				'8',
				'blah',
				prevTransaction,
				'10',
				'Comment'
			)
		).toEqual({
			branch: '10',
			txComment: 'Comment',
			deleteArtifacts: ['12345'],
			deleteRelations: [
				{
					typeName: 'hello',
					typeId: '9',
					aArtId: '7',
					bArtId: '8',
					rationale: 'blah',
				},
			],
		});
	});
});
