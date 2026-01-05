/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import type { settingsDialogData } from '@osee/messaging/shared/types';
import { TestScheduler } from 'rxjs/testing';
import { MimPreferencesService } from '../http/mim-preferences.service';

import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { transactionResultMock } from '@osee/transactions/testing';
import { PreferencesUIService } from './preferences-ui.service';

export function preferencesTest(
	mimPreferencesServiceMock: Partial<MimPreferencesService>
) {
	let service: PreferencesUIService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: MimPreferencesService,
					useValue: mimPreferencesServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		service = TestBed.inject(PreferencesUIService);
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

	it('should create or update global user preferences', () => {
		// This needs to really be tested in e2e tests
		scheduler.run(({ expectObservable }) => {
			const testData: settingsDialogData = {
				allowedHeaders1: [],
				allHeaders1: [],
				allowedHeaders2: [],
				allHeaders2: [],
				branchId: '10',
				editable: false,
				headers1Label: '',
				headers2Label: '',
				headersTableActive: false,
				wordWrap: false,
			};
			const result = service.createOrUpdateGlobalUserPrefs(testData);
			expectObservable(result).toBe('(a|)', { a: transactionResultMock });
		});
	});

	it('should update user preferences', () => {
		scheduler.run(() => {
			service.BranchId = '10';
			const expectedObservable = { a: transactionResultMock };
			const expectedMarble = '(a|)';
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
}
