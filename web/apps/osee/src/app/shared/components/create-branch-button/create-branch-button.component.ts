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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	BranchInfoService,
	BranchRoutedUIService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
import { filter, switchMap, take, tap } from 'rxjs';
import { CreateBranchDialogComponent } from '../create-branch-dialog/create-branch-dialog.component';
import { createWorkingBranchDetails } from '@osee/commit/types';

@Component({
	selector: 'osee-create-branch-button',
	imports: [MatButton, MatIcon],
	template: `<button
		mat-flat-button
		(click)="createBranch()"
		class="tw-bg-osee-blue-7 tw-text-background-background dark:tw-bg-osee-blue-10">
		<mat-icon>alt_route</mat-icon>Create Branch
	</button>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateBranchButtonComponent {
	private branchedRouter = inject(BranchRoutedUIService);
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private branchInfoService = inject(BranchInfoService);
	private uiService = inject(UiService);
	dialog = inject(MatDialog);

	currentBranch = this.currBranchInfoService.currentBranch;

	createBranch() {
		this.currentBranch
			.pipe(
				take(1),
				switchMap((currBranch) =>
					this.dialog
						.open(CreateBranchDialogComponent, {
							data: { branchName: '' },
							minWidth: '60vw',
						})
						.afterClosed()
						.pipe(
							take(1),
							filter(
								(branchName) => branchName && branchName !== ''
							),
							switchMap((branchName) => {
								const createBranchData =
									createWorkingBranchDetails(
										branchName,
										currBranch
									);
								return this.branchInfoService.createBranch(
									createBranchData
								);
							}),
							tap((resp) => {
								this.uiService.updated = true;
								if (resp.id && resp.id !== '-1') {
									const _branchType =
										resp.branchType === '2'
											? 'baseline'
											: 'working';
									this.branchedRouter.position = {
										type: _branchType,
										id: resp.id,
									};
								}
							})
						)
				)
			)
			.subscribe();
	}
}
