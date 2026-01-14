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
import { Component, input, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { combineLatest, of, switchMap, take, tap } from 'rxjs';
import { CommitBranchService } from '@osee/commit/services';
import { MergeManagerDialogComponent } from '../merge-manager-dialog/merge-manager-dialog.component';
import { toObservable } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
	selector: 'osee-commit-branch-button',
	imports: [MatButton, MatIcon, MatTooltip],
	template: `<div
		[matTooltip]="disabledMessage()"
		[matTooltipDisabled]="!disabled()">
		<button
			mat-flat-button
			class="tw-flex tw-justify-center tw-bg-osee-blue-7 tw-text-background disabled:tw-bg-background-selected-disabled-button dark:tw-bg-osee-blue-10 [&_*]:tw-m-0"
			[disabled]="disabled()"
			(click)="commitBranch()"
			aria-label="Commit Branch"
			matTooltip="Commit Branch">
			<mat-icon class="material-icons-outlined">check</mat-icon>
		</button>
	</div>`,
})
export class CommitBranchButtonComponent {
	dialog = inject(MatDialog);
	private commitBranchService = inject(CommitBranchService);
	private uiService = inject(UiService);
	private branchedRouter = inject(BranchRoutedUIService);

	sourceBranchId = input.required<string>();
	destBranchId = input.required<string>();
	disabled = input(false);
	disabledMessage = input<string>('Commit Blocked');
	teamWorkflowId = input<string>('');

	sourceBranchId$ = toObservable(this.sourceBranchId);
	destBranchId$ = toObservable(this.destBranchId);

	commitBranch() {
		combineLatest([this.sourceBranchId$, this.destBranchId$])
			.pipe(
				take(1),
				switchMap(([sourceBranchId, destBranchId]) =>
					this.commitBranchService.getBranch(sourceBranchId).pipe(
						switchMap((sourceBranch) =>
							this.commitBranchService
								.getBranch(destBranchId)
								.pipe(
									switchMap((destBranch) =>
										this.commitBranchService
											.validateCommit(
												sourceBranchId,
												destBranchId
											)
											.pipe(
												switchMap((results) => {
													if (
														results.conflictCount >
														0
													) {
														return this.dialog
															.open(
																MergeManagerDialogComponent,
																{
																	data: {
																		sourceBranch:
																			sourceBranch,
																		destBranch:
																			destBranch,
																		validateResults:
																			results,
																	},
																	minWidth:
																		'60%',
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
																destBranch.id
															)
															.pipe(
																tap(
																	(
																		commitResp
																	) => {
																		if (
																			commitResp.success
																		) {
																			this.uiService.updated =
																				true;
																			if (
																				this
																					.uiService
																					.id
																					.value ===
																				sourceBranchId
																			) {
																				this.branchedRouter.position =
																					{
																						type: 'baseline',
																						id: commitResp
																							.tx
																							.branchId,
																					};
																			}
																			if (
																				this.teamWorkflowId() !==
																				''
																			) {
																				this.uiService.updatedArtifact =
																					this.teamWorkflowId();
																			}
																		} else {
																			this.uiService.ErrorText =
																				'Error committing branch';
																		}
																	}
																)
															);
													}
													return of();
												})
											)
									)
								)
						)
					)
				)
			)
			.subscribe();
	}
}
