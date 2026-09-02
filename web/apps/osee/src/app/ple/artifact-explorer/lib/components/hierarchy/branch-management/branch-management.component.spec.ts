/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchManagementComponent } from './branch-management.component';
import {
	BranchInfoService,
	BranchRoutedUIService,
	CurrentBranchInfoService,
} from '@osee/shared/services';
import {
	BranchInfoServiceMock,
	branchRoutedUiServiceMock,
	testBranchInfo,
} from '@osee/shared/testing';
import { of } from 'rxjs';
import {
	ActionService,
	CreateActionService,
	CurrentActionService,
} from '@osee/configuration-management/services';
import {
	actionServiceMock,
	createActionServiceMock,
	currentActionServiceMock,
} from '@osee/configuration-management/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActionStateButtonService } from '../../../../../../configuration-management/components/internal/services/action-state-button.service';
import { actionStateButtonServiceMock } from '../../../../../../configuration-management/components/internal/services/action-state-button.service.mock';
import { CommitBranchService } from '@osee/commit/services';
import { commitBranchServiceMock } from '@osee/commit/testing';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

describe('BranchManagementComponent', () => {
	let component: BranchManagementComponent;
	let fixture: ComponentFixture<BranchManagementComponent>;
	let commitBranchService: CommitBranchService;
	let dialog: MatDialog;
	let afterClosed: ReturnType<typeof vi.fn>;

	beforeEach(async () => {
		afterClosed = vi.fn().mockReturnValue(of(true));

		await TestBed.configureTestingModule({
			imports: [BranchManagementComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						queryParamMap: of(new Map<string, string>()),
					},
				},
				{
					provide: CurrentBranchInfoService,
					useValue: {
						get currentBranch() {
							return of(testBranchInfo);
						},
						get parentBranch() {
							return of(testBranchInfo.parentBranch.id);
						},
					} as Partial<CurrentBranchInfoService>,
				},
				{ provide: ActionService, useValue: actionServiceMock },
				{
					provide: CurrentActionService,
					useValue: currentActionServiceMock,
				},
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
				{
					provide: ActionStateButtonService,
					useValue: actionStateButtonServiceMock,
				},
				{
					provide: CommitBranchService,
					useValue: commitBranchServiceMock,
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(BranchManagementComponent);
		component = fixture.componentInstance;
		commitBranchService = TestBed.inject(CommitBranchService);
		dialog = TestBed.inject(MatDialog);
		vi.spyOn(dialog, 'open').mockReturnValue({
			afterClosed,
		} as unknown as ReturnType<MatDialog['open']>);
		fixture.detectChanges();
	});

	afterEach(() => {
		// The commit service mock is a shared singleton, so restore spies to
		// avoid call-count leakage between tests.
		vi.restoreAllMocks();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should commit directly when there are no conflicts', () => {
		vi.spyOn(commitBranchService, 'validateCommit').mockReturnValue(
			of({
				commitable: true,
				conflictCount: 0,
				conflictsResolved: 0,
			})
		);
		const commitSpy = vi.spyOn(commitBranchService, 'commitBranch');

		component.commitBranch();

		// No conflicts: the merge manager is never opened and the commit runs.
		expect(dialog.open).not.toHaveBeenCalled();
		expect(commitSpy).toHaveBeenCalledTimes(1);
	});

	it('should open the merge manager before committing when conflicts exist', () => {
		vi.spyOn(commitBranchService, 'validateCommit').mockReturnValue(
			of({
				commitable: false,
				conflictCount: 3,
				conflictsResolved: 0,
			})
		);
		const commitSpy = vi.spyOn(commitBranchService, 'commitBranch');

		component.commitBranch();

		// The merge manager dialog is opened for the user to resolve conflicts.
		expect(dialog.open).toHaveBeenCalledTimes(1);
		const dialogConfig = vi.mocked(dialog.open).mock.calls[0][1] as {
			data: { validateResults: { conflictCount: number } };
		};
		expect(dialogConfig.data.validateResults.conflictCount).toBe(3);
		// It still commits after the dialog resolves truthy.
		expect(commitSpy).toHaveBeenCalledTimes(1);
	});

	it('should not commit when the merge manager dialog is cancelled', () => {
		afterClosed.mockReturnValue(of(false));
		vi.spyOn(commitBranchService, 'validateCommit').mockReturnValue(
			of({
				commitable: false,
				conflictCount: 2,
				conflictsResolved: 0,
			})
		);
		const commitSpy = vi.spyOn(commitBranchService, 'commitBranch');

		component.commitBranch();

		// Conflicts exist but the user cancels the merge manager: no commit.
		expect(dialog.open).toHaveBeenCalledTimes(1);
		expect(commitSpy).not.toHaveBeenCalled();
	});
});
