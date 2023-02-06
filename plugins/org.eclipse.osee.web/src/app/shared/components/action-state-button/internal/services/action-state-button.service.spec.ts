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
import { RouterTestingModule } from '@angular/router/testing';
import { TestScheduler } from 'rxjs/testing';
import { ActionService } from '../../../../../ple-services/http/action.service';
import { actionServiceMock } from '../../../../../ple-services/http/action.service.mock';
import { BranchInfoService } from '../../../../../ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from '../../../../../ple-services/http/branch-info.service.mock';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import {
	testCommitResponse,
	testDataTransitionResponse,
} from '../../../../../testing/configuration-management.response.mock';
import { MockXResultData } from '../../../../../testing/XResultData.response.mock';
import { testnewActionResponse } from '../../../../../testing/new-action.response.mock';
import { MockUserResponse } from '../../../../../testing/user/user.response.mock';
import { CreateAction } from '../../../../../types/configuration-management/create-action';

import { ActionStateButtonService } from './action-state-button.service';
import { BranchRoutedUIService } from '../../../internal/services/branch-routed-ui.service';
import { branchRoutedUiServiceMock } from '../../../internal/services/branch-routed-ui.service.mock';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';

describe('ActionStateButtonService', () => {
	let service: ActionStateButtonService;
	let uiService: UiService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [RouterTestingModule],
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
			],
		});
		service = TestBed.inject(ActionStateButtonService);
		uiService = TestBed.inject(UiService);
	});

	beforeEach(() => {
		uiService.idValue = '10';
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

	it('should add an action', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(
				service.doAddAction(new CreateAction(MockUserResponse), '3')
			).toBe('(a|)', { a: testnewActionResponse });
		});
	});
	it('should commit a branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(
				service.commitBranch({ committer: '', archive: '' })
			).toBe('(a|)', { a: testCommitResponse });
		});
	});
	it('should approve current branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.doApproveBranch).toBe('(a|)', {
				a: MockXResultData,
			});
		});
	});

	it('should transition current branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.doTransition).toBe('(a|)', {
				a: testDataTransitionResponse,
			});
		});
	});

	it('should execute a commit branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.doCommitBranch).toBe('(a|)', {
				a: testDataTransitionResponse,
			});
		});
	});

	it('should get transitionable state', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.branchTransitionable).toBe('a', {
				a: 'false',
			});
		});
	});

	it('should get team leads', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.teamsLeads).toBe('a', {
				a: [{ id: '0', name: 'name' }],
			});
		});
	});

	it('should get branch approved', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.branchApproved).toBe('a', { a: 'true' });
		});
	});
});
