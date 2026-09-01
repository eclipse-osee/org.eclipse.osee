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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrentActionService } from '@osee/configuration-management/services';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { teamWorkflowTokenSentinel } from '@osee/shared/types/configuration-management';
import { map } from 'rxjs';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { CommitBranchService } from '@osee/commit/services';
import { MergeManagerDialogComponent } from '@osee/commit/components';
import {
	BranchInfoService,
	BranchRoutedUIService,
} from '@osee/shared/services';
import { branchSentinel } from '@osee/shared/types';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { CreateBranchDialogComponent } from '../../../../../../shared/components/create-branch-dialog/create-branch-dialog.component';
import { createWorkingBranchDetails } from '@osee/commit/types';
import { catchError, EMPTY, filter, of, switchMap, take, tap } from 'rxjs';

@Component({
	selector: 'osee-branch-management',
	imports: [MatIcon, MatButton, MatTooltip, RouterLink],
	template: `
		<div class="tw-flex tw-flex-col tw-gap-1 tw-px-2">
			@if (branchId() !== '' && branchId() !== '570') {
				@if (displayWorkflowButtons()) {
					@if (branchType() === 'working') {
						<a
							routerLink="/actra/workflow"
							[queryParams]="{
								id: branchWorkflowToken().id,
							}"
							target="_blank"
							class="tw-no-underline">
							<button
								mat-button
								class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
								aria-label="Open Team Workflow">
								<mat-icon class="material-icons-outlined"
									>assignment</mat-icon
								>
								Open Workflow
							</button>
						</a>
					} @else {
						<a
							routerLink="/actra/action/create"
							target="_blank"
							class="tw-no-underline">
							<button
								mat-button
								class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
								matTooltip="Create a new action for this branch"
								aria-label="Create Action">
								<mat-icon>add</mat-icon>
								Create Action
							</button>
						</a>
					}
				}
				@if (showCreateBranch()) {
					<button
						mat-button
						class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
						matTooltip="Create a new working branch from this baseline"
						aria-label="Create Working Branch"
						(click)="createBranch()">
						<mat-icon>alt_route</mat-icon>
						Create Working Branch
					</button>
					<span
						[matTooltip]="
							branchCommitButtonIsDisabled()
								? 'Only working branches can be committed.'
								: 'Commit this working branch to its baseline'
						">
						<button
							mat-button
							class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
							aria-label="Commit to Baseline"
							[disabled]="branchCommitButtonIsDisabled()"
							(click)="commitBranch()">
							<mat-icon class="material-icons-outlined"
								>check</mat-icon
							>
							Commit to Baseline
						</button>
					</span>
					<span
						[matTooltip]="
							isBaseline()
								? 'Baseline branches cannot be updated.'
								: 'Sync this working branch with the latest baseline changes'
						">
						<button
							mat-button
							class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
							aria-label="Update from Baseline"
							[disabled]="isBaseline()"
							(click)="updateFromBaseline()">
							<mat-icon>sync</mat-icon>
							Update from Baseline
						</button>
					</span>
					@if (branchId() && branchType()) {
						<a
							[routerLink]="'/ple/change-report/' + branchId()"
							target="_blank"
							class="tw-no-underline">
							<button
								mat-button
								class="tw-w-full tw-justify-start tw-text-foreground-text disabled:tw-opacity-50"
								matTooltip="View changes made on this branch"
								aria-label="Change Report">
								<mat-icon class="material-icons-outlined"
									>differences</mat-icon
								>
								Change Report
							</button>
						</a>
					}
				}
			} @else {
				<div class="tw-p-4 tw-text-sm tw-opacity-50">
					Select a branch to see management options.
				</div>
			}
		</div>
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BranchManagementComponent {
	private currBranchInfoService = inject(CurrentBranchInfoService);
	private currentActionService = inject(CurrentActionService);
	private uiService = inject(UiService);
	private branchInfoService = inject(BranchInfoService);
	private branchRoutedService = inject(BranchRoutedUIService);
	private commitBranchService = inject(CommitBranchService);
	private dialog = inject(MatDialog);

	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	protected branchWorkflowToken = toSignal(
		this.currentActionService.branchWorkflowToken,
		{ initialValue: teamWorkflowTokenSentinel }
	);

	branchCategories = this.currBranchInfoService.currentBranch.pipe(
		map((branch) => branch.categories)
	);
	private _branchCategories$ = toSignal(this.branchCategories, {
		initialValue: [],
	});
	categoryNames = computed(() => {
		return this._branchCategories$().map((category) => category.name);
	});
	hasAtsCategory = computed(() => {
		return this.categoryNames().some((name) => name == 'ATS');
	});
	hasPleCategory = computed(() => {
		return this.categoryNames().some((name) => name == 'PLE');
	});
	isBaseline = computed(() => this.branchType() === 'baseline');
	workflowTokenValid = computed(
		() =>
			this.branchWorkflowToken().id !== undefined &&
			this.branchWorkflowToken().id !== '-1'
	);

	displayWorkflowButtons = computed(
		() =>
			this.workflowTokenValid() ||
			(this.hasAtsCategory() && this.isBaseline())
	);

	showCreateBranch = computed(
		() => !this.displayWorkflowButtons() && !this.hasAtsCategory()
	);

	private readonly currentBranch = toSignal(
		this.currBranchInfoService.currentBranch,
		{
			initialValue: branchSentinel,
		}
	);
	private readonly currentBranchId = computed(() => this.currentBranch().id);

	baselineBranchId = toSignal(this.currBranchInfoService.parentBranch, {
		initialValue: branchSentinel.id,
	});

	// Do not allow commit if the branch is not a working branch (type 0)
	branchCommitButtonIsDisabled = computed(
		() => this.currentBranch().branchType !== '0'
	);

	createBranch(): void {
		this.currBranchInfoService.currentBranch
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
									const branchType =
										resp.branchType === '2'
											? 'baseline'
											: 'working';
									this.branchRoutedService.position = {
										type: branchType,
										id: resp.id,
									};
								}
							})
						)
				),
				catchError((err) => {
					this.uiService.ErrorText = `Error creating working branch: ${err.message || err}`;
					return EMPTY;
				})
			)
			.subscribe();
	}

	commitBranch(): void {
		const workingBranchId = this.currentBranchId();
		const baselineBranchId = this.baselineBranchId();
		this.commitBranchService
			.getBranch(workingBranchId)
			.pipe(
				take(1),
				switchMap((sourceBranch) =>
					this.commitBranchService.getBranch(baselineBranchId).pipe(
						switchMap((destBranch) =>
							this.commitBranchService
								.validateCommit(sourceBranch.id, destBranch.id)
								.pipe(
									switchMap((results) => {
										// When conflicts exist, force the user
										// through the merge manager before the
										// commit can proceed.
										if (results.conflictCount > 0) {
											return this.dialog
												.open(
													MergeManagerDialogComponent,
													{
														data: {
															sourceBranch,
															destBranch,
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
										if (!commit) {
											return of();
										}
										return this.commitBranchService
											.commitBranch(
												sourceBranch.id,
												destBranch.id
											)
											.pipe(
												tap((resp) => {
													if (resp.success) {
														this.uiService.updated =
															true;
														this.branchRoutedService.position =
															{
																type: 'baseline',
																id: resp.tx
																	.branchId,
															};
													} else {
														this.uiService.ErrorText =
															'Error committing working branch to baseline';
													}
												})
											);
									})
								)
						)
					)
				),
				catchError((err) => {
					this.uiService.ErrorText = `Error committing working branch to baseline: ${err.message || err}`;
					return EMPTY;
				})
			)
			.subscribe();
	}

	updateFromBaseline(): void {
		const workingBranchId = this.currentBranchId();
		this.commitBranchService
			.updateFromParent(workingBranchId)
			.pipe(
				take(1),
				tap((res) => {
					if (res.newBranchId.id !== '-1') {
						this.uiService.updated = true;
						this.branchRoutedService.position = {
							type: 'working',
							id: res.newBranchId.id,
						};
					}
				}),
				catchError((err) => {
					this.uiService.ErrorText = `Error updating working branch from baseline: ${err.message || err}`;
					return EMPTY;
				})
			)
			.subscribe();
	}
}
