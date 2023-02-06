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
import { TransactionService } from '@osee/shared/transactions';
import {
	transactionServiceMock,
	transactionInfoMock,
} from '@osee/shared/transactions/testing';
import { TestScheduler } from 'rxjs/testing';
import { ActionService } from 'src/app/ple-services/http/action.service';
import { actionServiceMock } from 'src/app/ple-services/http/action.service.mock';
import { BranchInfoService } from 'src/app/ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from 'src/app/ple-services/http/branch-info.service.mock';
import { testBranchInfo } from '../../../testing/branch-info.response.mock';
import { testBranchActions } from '../../../testing/configuration-management.response.mock';
import { changeReportHttpServiceMock } from '../mocks/change-report-http.service.mock';
import { changeReportMock } from '../mocks/changeReportMock';
import { ChangeReportHttpService } from './change-report-http.service';

import { ChangeReportService } from './change-report.service';

describe('ChangeReportService', () => {
	let service: ChangeReportService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: ChangeReportHttpService,
					useValue: changeReportHttpServiceMock,
				},
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
			],
		});
		service = TestBed.inject(ChangeReportService);
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

	it('should get changes given two branch ids', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.getBranchChanges('10', '8')).toBe('(a|)', {
				a: changeReportMock,
			});
		});
	});

	it('should get changes given a branch id and two txs', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.getTxChanges('10', '1', '2')).toBe(
				'(a|)',
				{
					a: changeReportMock,
				}
			);
		});
	});

	it('should get branch info', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.getBranchInfo('10')).toBe('(a|)', {
				a: testBranchInfo,
			});
		});
	});

	it('should get the action related to a branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.getRelatedAction('15')).toBe('(a|)', {
				a: testBranchActions[0],
			});
		});
	});

	it('should get the latest transaction for a branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.getLatestTxInfo('10')).toBe('(a|)', {
				a: transactionInfoMock,
			});
		});
	});
});
