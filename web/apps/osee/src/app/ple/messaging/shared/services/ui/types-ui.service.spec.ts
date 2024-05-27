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
	typesServiceMock,
	enumerationSetServiceMock,
} from '@osee/messaging/shared/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { UiService } from '@osee/shared/services';
import { EnumerationSetService } from '../http/enumeration-set.service';
import { TypesService } from '../http/types.service';

import { TypesUIService } from './types-ui.service';
import { transactionMock } from '@osee/transactions/testing';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';

describe('TypesUIService', () => {
	let service: TypesUIService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: TypesService, useValue: typesServiceMock },
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
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
			expectObservable(
				service.changeType(new PlatformTypeSentinel())
			).toBe('(a|)', {
				a: transactionResultMock,
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
				.expectObservable(
					service.partialUpdate({
						createArtifacts: [],
						modifyArtifacts: [],
						deleteRelations: [],
					})
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to copy type', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.idValue = '10';
			scheduler
				.expectObservable(
					service.copyType({
						createArtifacts: [],
						modifyArtifacts: [],
						deleteRelations: [],
					})
				)
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
					service.createType(new PlatformTypeSentinel())
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
					service.createType(new PlatformTypeSentinel())
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
					service.createType(new PlatformTypeSentinel())
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
					service.createType(new PlatformTypeSentinel())
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});
});
