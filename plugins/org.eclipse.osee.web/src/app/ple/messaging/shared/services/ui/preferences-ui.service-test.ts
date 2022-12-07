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
import { TestScheduler } from 'rxjs/testing';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { TransactionService } from '../../../../../transactions/transaction.service';
import { transactionServiceMock } from '../../../../../transactions/transaction.service.mock';
import { settingsDialogData } from '../../types/settingsdialog';
import { MimPreferencesService } from '../http/mim-preferences.service';

import { PreferencesUIService } from './preferences-ui.service';
import { userDataAccountServiceMock } from '../../../../../userdata/services/user-data-account.service.mock';

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
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
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
			scheduler
				.expectObservable(result)
				.toBe('(a|)', { a: transactionResultMock });
		});
	});
}
