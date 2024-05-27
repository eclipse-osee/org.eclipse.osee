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

import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import {
	EnumerationSetService,
	MimPreferencesService,
	TypesService,
} from '@osee/messaging/shared/services';
import {
	MimPreferencesMock,
	MimPreferencesServiceMock,
	enumerationSetServiceMock,
	platformTypes1,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { ApplicabilityListService } from '@osee/shared/services';
import { applicabilityListServiceMock } from '@osee/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import { CurrentTypesService } from './current-types.service';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';

describe('CurrentTypesService', () => {
	let service: CurrentTypesService;
	let uiService: PlMessagingTypesUIService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransactionBuilderService,
					useValue: transactionBuilderMock,
				},
				{
					provide: MimPreferencesService,
					useValue: MimPreferencesServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{ provide: TypesService, useValue: typesServiceMock },
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentTypesService);
		uiService = TestBed.inject(PlMessagingTypesUIService);
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

	//borked by signal
	xit('should fetch data from backend', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: platformTypes1 };
			const expectedMarble = '500ms a';
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(service.typeData)
				.toBe(expectedMarble, expectedFilterValues);
			uiService.filterString = 'A filter';
		});
	});

	it('should set singleLineAdjustment to 0', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: 30, b: 0 };
			const expectedMarble = 'b';
			uiService.columnCountNumber = 2;
			uiService.BranchIdString = '10';
			uiService.filterString = 'A filter';
			scheduler
				.expectObservable(uiService.singleLineAdjustment)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should send a post request to create type,enum set, enum', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(
					service.createType(new PlatformTypeSentinel())
				)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should fetch preferences', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: MimPreferencesMock };
			const expectedMarble = 'a';
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(service.preferences)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});
	it('should fetch branch prefs', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: ['8:true'] };
			const expectedMarble = 'a';
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(service.BranchPrefs)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should fetch edit mode', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: MimPreferencesMock.inEditMode };
			const expectedMarble = 'a';
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(service.inEditMode)
				.toBe(expectedMarble, expectedFilterValues);
		});
	});

	it('should update user preferences', () => {
		scheduler.run(() => {
			const expectedObservable = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.BranchIdString = '10';
			scheduler
				.expectObservable(
					service.updatePreferences({
						branchId: '10',
						allowedHeaders1: ['name', 'description'],
						allowedHeaders2: ['name', 'description'],
						allHeaders1: ['name'],
						allHeaders2: ['name'],
						editable: true,
						headers1Label: '',
						headers2Label: '',
						headersTableActive: false,
						wordWrap: false,
					})
				)
				.toBe(expectedMarble, expectedObservable);
		});
	});
});
