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
import { TestScheduler } from 'rxjs/testing';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { transactionBuilderMock } from '../../../../../transactions/transaction-builder.service.mock';
import { applicabilityListServiceMock } from '../../../shared/testing/applicability-list.service.mock';
import { MimPreferencesMock } from '../../../shared/testing/mim-preferences.response.mock';
import { MimPreferencesServiceMock } from '../../../shared/testing/mim-preferences.service.mock';
import { ApplicabilityListService } from '../../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../../shared/services/http/mim-preferences.service';
import { platformTypes1 } from '../../../shared/testing/platform-types.response.mock';
import { enumerationSetServiceMock } from '../../../shared/testing/enumeration-set.service.mock';
import { typesServiceMock } from '../../../shared/testing/types.service.mock';

import { CurrentTypesService } from './current-types.service';
import { EnumerationSetService } from '../../../shared/services/http/enumeration-set.service';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from '../../../shared/services/http/types.service';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { enumsServiceMock } from '../../../shared/testing/enums.service.mock';
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../../userdata/services/user-data-account.service.mock';

describe('CurrentTypesServiceService', () => {
	let service: CurrentTypesService;
	let uiService: PlMessagingTypesUIService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

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
				{ provide: EnumsService, useValue: enumsServiceMock },
				{
					provide: EnumerationSetService,
					useValue: enumerationSetServiceMock,
				},
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(CurrentTypesService);
		uiService = TestBed.inject(PlMessagingTypesUIService);
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

	it('should fetch data from backend', () => {
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
	it('should set singleLineAdjustment to 30', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: 30, b: 0 };
			const expectedMarble = 'b 499ms a';
			uiService.columnCountNumber = 9;
			uiService.BranchIdString = '10';
			uiService.filterString = 'A filter';
			scheduler
				.expectObservable(uiService.singleLineAdjustment)
				.toBe(expectedMarble, expectedFilterValues);
			const expectedFilterValues2 = { a: platformTypes1 };
			const expectedMarble2 = '500ms a';
			scheduler
				.expectObservable(service.typeData)
				.toBe(expectedMarble2, expectedFilterValues2);
		});
	});

	it('should send a post request to create type,enum set, enum', () => {
		scheduler.run(() => {
			const expectedFilterValues = { a: transactionResultMock };
			const expectedMarble = '(a|)';
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
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
			uiService.BranchIdString = '10';
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
