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
import { apiURL } from 'src/environments/environment';
import { MockUserResponse } from '../../../../../testing/user/user.response.mock';

import { MimPreferencesService } from './mim-preferences.service';
import { MimUserGlobalPreferences } from '@osee/messaging/shared/types';
import { TransactionService } from '@osee/shared/transactions';
import {
	transactionServiceMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';

const testGlobalUserPrefs: MimUserGlobalPreferences = {
	id: '1',
	name: 'MIM Global User Preferences',
	wordWrap: false,
};

describe('MimPreferencesService', () => {
	let service: MimPreferencesService;
	let scheduler: TestScheduler;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
			providers: [
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(MimPreferencesService);
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

	it('should get user prefs', () => {
		service.getUserPrefs('10', MockUserResponse).subscribe();
		const req = httpTestingController.expectOne(apiURL + '/mim/user/' + 10);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should get branch prefs', () => {
		service.getBranchPrefs(MockUserResponse).subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/mim/user/branches'
		);
		expect(req.request.method).toEqual('GET');
		req.flush({});
		httpTestingController.verify();
	});

	it('should create global user preferences', () => {
		scheduler.run(({ expectObservable }) => {
			const result = service.createGlobalUserPrefs(
				MockUserResponse,
				testGlobalUserPrefs
			);
			scheduler
				.expectObservable(result)
				.toBe('(a|)', { a: transactionResultMock });
		});
	});

	it('should update global user preferences', () => {
		scheduler.run(({ expectObservable }) => {
			const result = service.updateGlobalUserPrefs(
				testGlobalUserPrefs,
				testGlobalUserPrefs
			);
			scheduler
				.expectObservable(result)
				.toBe('(a|)', { a: transactionResultMock });
		});
	});
});
