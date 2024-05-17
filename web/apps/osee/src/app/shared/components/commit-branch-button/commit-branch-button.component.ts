/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { NgClass } from '@angular/common';
import { Component, Input, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import {
	BranchRoutedUIService,
	CommitBranchService,
	UiService,
} from '@osee/shared/services';
import { of, switchMap, take, tap } from 'rxjs';
import { MergeManagerDialogComponent } from '../merge-manager-dialog/merge-manager-dialog.component';

@Component({
	selector: 'osee-commit-branch-button',
	standalone: true,
	imports: [NgClass, MatButton],
	templateUrl: './commit-branch-button.component.html',
})
export class CommitBranchButtonComponent {
	@Input({ required: true }) branchId!: string;
	@Input() set disabled(value: boolean) {
		this._disabled.set(value);
	}

	_disabled = signal(false);

	constructor(
		public dialog: MatDialog,
		private commitBranchService: CommitBranchService,
		private uiService: UiService,
		private branchedRouter: BranchRoutedUIService
	) {}

	commitBranch() {
		return this.commitBranchService
			.getBranch(this.branchId)
			.pipe(
				switchMap((sourceBranch) =>
					this.commitBranchService
						.getBranch(sourceBranch.parentBranch.id)
						.pipe(
							switchMap((parentBranch) =>
								this.commitBranchService
									.validateCommit(sourceBranch)
									.pipe(
										switchMap((results) => {
											if (results.conflictCount > 0) {
												return this.dialog
													.open(
														MergeManagerDialogComponent,
														{
															data: {
																sourceBranch:
																	sourceBranch,
																parentBranch:
																	parentBranch,
																validateResults:
																	results,
															},
															minWidth: '60%',
														}
													)
													.afterClosed()
													.pipe(take(1));
											}
											return of(true);
										}),
										switchMap((commit) => {
											if (commit) {
												return this.commitBranchService
													.commitBranch(
														sourceBranch.id,
														sourceBranch
															.parentBranch.id
													)
													.pipe(
														tap((commitResp) => {
															if (
																commitResp.success
															) {
																this.uiService.updated =
																	true;
																this.branchedRouter.position =
																	{
																		type: 'baseline',
																		id: commitResp
																			.tx
																			.branchId,
																	};
															} else {
																this.uiService.ErrorText =
																	'Error committing branch';
															}
														})
													);
											}
											return of();
										})
									)
							)
						)
				)
			)
			.subscribe();
	}
}
