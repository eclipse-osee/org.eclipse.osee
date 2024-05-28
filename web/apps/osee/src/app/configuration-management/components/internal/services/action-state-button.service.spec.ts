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
import {
	BranchInfoService,
	BranchRoutedUIService,
	UiService,
} from '@osee/shared/services';
import { ActionStateButtonService } from './action-state-button.service';
import { UserDataAccountService } from '@osee/auth';
import {
	BranchInfoServiceMock,
	testCommitResponse,
	testDataTransitionResponse,
	branchRoutedUiServiceMock,
	testBranchActions,
	testWorkFlow,
} from '@osee/shared/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';
import { actionServiceMock } from '@osee/configuration-management/testing';
import { ActionService } from '@osee/configuration-management/services';

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
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
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

	it('should commit a branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(
				service.commitBranch({ committer: '', archive: '' })
			).toBe('(a|)', { a: testCommitResponse });
		});
	});
	it('should approve current branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(service.approveBranch(testBranchActions[0])).toBe(
				'(a|)',
				{
					a: true,
				}
			);
		});
	});

	it('should transition current branch', () => {
		scheduler.run(({ expectObservable }) => {
			expectObservable(
				service.transition(
					testWorkFlow.currentState,
					testBranchActions[0]
				)
			).toBe('(a|)', {
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
			expectObservable(
				service.isTransitionApproved(testBranchActions[0])
			).toBe('(a|)', {
				a: true,
			});
		});
	});
});
