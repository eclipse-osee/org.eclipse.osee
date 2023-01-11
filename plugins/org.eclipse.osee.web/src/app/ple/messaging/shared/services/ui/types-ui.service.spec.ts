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
import { TestScheduler } from 'rxjs/testing';
import {
	transactionMock,
	transactionResultMock,
} from 'src/app/transactions/transaction.mock';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { enumerationSetServiceMock } from '../../testing/enumeration-set.service.mock';
import { enumsServiceMock } from '../../testing/enums.service.mock';
import { typesServiceMock } from '../../testing/types.service.mock';
import { EnumerationSetService } from '../http/enumeration-set.service';
import { EnumsService } from '../http/enums.service';
import { TypesService } from '../http/types.service';

import { TypesUIService } from './types-ui.service';

describe('TypesUIService', () => {
	let service: TypesUIService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: TypesService, useValue: typesServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
			],
		});
		service = TestBed.inject(TypesUIService);
		uiService = TestBed.inject(UiService);
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

	it('should create a transaction', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.changeType({})).toBe('(a|)', {
				a: transactionMock,
			});
		});
	});

	it('should perform a mutation', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.performMutation(transactionMock)).toBe(
				'(a|)',
				{ a: transactionResultMock }
			);
		});
	});

	it('should send a modification request', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(service.partialUpdate({}))
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to copy type', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(service.copyType({}))
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to create type,enum set, enum', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.createType({}, true, {
						enumSetId: '1',
						enumSetName: 'hello',
						enumSetApplicability: { id: '1', name: 'Base' },
						enumSetDescription: 'description',
						enums: [
							{
								name: 'Hello',
								ordinal: 0,
								applicability: { id: '1', name: 'base' },
							},
						],
					})
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to create type', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.createType({}, false, {
						enumSetId: '1',
						enumSetName: 'hello',
						enumSetApplicability: { id: '1', name: 'Base' },
						enumSetDescription: 'description',
						enums: [
							{
								name: 'Hello',
								ordinal: 0,
								applicability: { id: '1', name: 'base' },
							},
						],
					})
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to create type with new enum set', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.createType(
						{ interfaceLogicalType: 'enumeration' },
						true,
						{
							enumSetId: '1',
							enumSetName: 'hello',
							enumSetApplicability: { id: '1', name: 'Base' },
							enumSetDescription: 'description',
							enums: [
								{
									name: 'Hello',
									ordinal: 0,
									applicability: { id: '1', name: 'base' },
								},
							],
						}
					)
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to create type with existing enum set', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.createType(
						{ interfaceLogicalType: 'enumeration' },
						false,
						{
							enumSetId: '1',
							enumSetName: 'hello',
							enumSetApplicability: { id: '1', name: 'Base' },
							enumSetDescription: 'description',
							enums: [
								{
									name: 'Hello',
									ordinal: 0,
									applicability: { id: '1', name: 'base' },
								},
							],
						}
					)
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});
});
