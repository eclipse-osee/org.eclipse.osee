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
import {
	Component,
	computed,
	inject,
	input,
	output,
	signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import { CommitBranchService } from '@osee/commit/services';
import { branch, branchSentinel } from '@osee/shared/types';
import { filter, iif, of, switchMap, take, tap } from 'rxjs';
import { MergeManagerDialogComponent } from '../merge-manager-dialog/merge-manager-dialog.component';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-update-from-parent-button',
	imports: [MatButton, MatTooltip, MatIcon],
	template: `<div
		[matTooltip]="loading() ? 'Disabled while loading.' : disabledMessage()"
		[matTooltipDisabled]="!disabledOrLoading()">
		<button
			mat-flat-button
			class="tw-flex tw-justify-center tw-bg-osee-blue-7 tw-text-background disabled:tw-bg-background-selected-disabled-button dark:tw-bg-osee-blue-10 [&_*]:tw-m-0"
			(click)="updateFromParent()"
			[disabled]="disabledOrLoading()"
			matTooltip="Update Branch From Parent">
			<mat-icon [class.tw-animate-spin]="loading()">sync</mat-icon>
		</button>
	</div>`,
})
export class UpdateFromParentButtonComponent {
	workingBranch = input.required<Pick<branch, 'id' | 'branchState'>>();
	disabled = input(false);
	disabledMessage = input<string>('Update Blocked');
	updated = output();

	workingBranch$ = toObservable(this.workingBranch);

	commitBranchService = inject(CommitBranchService);
	branchedRouter = inject(BranchRoutedUIService);
	uiService = inject(UiService);
	dialog = inject(MatDialog);
	snackbar = inject(MatSnackBar);

	loading = signal(false);

	disabledOrLoading = computed(() => this.disabled() || this.loading());

	updateFromParent() {
		this.workingBranch$
			.pipe(
				take(1),
				filter((workingBranch) => workingBranch != branchSentinel),
				tap(() => this.loading.set(true)),
				switchMap((branch) =>
					this.commitBranchService.updateFromParent(branch.id).pipe(
						tap(() => this.loading.set(false)),
						switchMap((res) =>
							iif(
								() => res.needsMerge,
								of(res).pipe(
									switchMap((res) =>
										this.commitBranchService
											.validateCommit(
												branch.id,
												res.newBranchId.id
											)
											.pipe(
												switchMap((validateResults) =>
													this.dialog
														.open(
															MergeManagerDialogComponent,
															{
																data: {
																	sourceBranch:
																		branch,
																	destBranch:
																		res.newBranchId,
																	validateResults:
																		validateResults,
																},
																minWidth: '60%',
															}
														)
														.afterClosed()
														.pipe(take(1))
												)
											)
									),
									switchMap((commit) => {
										if (commit) {
											return this.commitBranchService
												.updateFromParent(branch.id)
												.pipe(
													tap((res) => {
														this.snackbar.open(
															res.results
																.results[0],
															'',
															{
																verticalPosition:
																	'top',
																horizontalPosition:
																	'center',
																duration: 3000,
																panelClass:
																	'tw-text-center',
															}
														);
														if (
															res.newBranchId
																.id !== '-1'
														) {
															this.updated.emit();
															this.branchedRouter.position =
																{
																	type: 'working',
																	id: res
																		.newBranchId
																		.id,
																};
															this.uiService.updated =
																true;
														}
													})
												);
										}
										return of();
									})
								),
								of(res).pipe(
									tap((res) => {
										this.snackbar.open(
											res.results.results[0],
											'',
											{
												verticalPosition: 'top',
												horizontalPosition: 'center',
												duration: 3000,
												panelClass: 'tw-text-center',
											}
										);
										if (res.newBranchId.id !== '-1') {
											this.updated.emit();
											this.branchedRouter.position = {
												type: 'working',
												id: res.newBranchId.id,
											};
											this.uiService.updated = true;
										}
									})
								)
							)
						)
					)
				)
			)

			.subscribe();
	}
}
