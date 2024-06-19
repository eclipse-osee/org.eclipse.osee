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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { branch } from '@osee/shared/types';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	repeat,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { MergeManagerTableComponent } from './merge-manager-table/merge-manager-table.component';
import { CommitBranchService } from '@osee/commit/services';
import { mergeDialogType, validateCommitResult } from '@osee/commit/types';

@Component({
	selector: 'osee-merge-manager-dialog',
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		MatDialogTitle,
		MatIcon,
		MatTooltip,
		MatDialogContent,
		MatButton,
		MatDialogActions,
		MergeManagerTableComponent,
	],
	templateUrl: './merge-manager-dialog.component.html',
	styles: ['tooltip-width {max-width: unset}'],
})
export class MergeManagerDialogComponent {
	private _updateMergeBranchId = new BehaviorSubject<boolean>(false);

	commitBranchService = inject(CommitBranchService);
	dialogRef = inject(MatDialogRef<MergeManagerDialogComponent>);
	dialogData = signal(
		inject<{
			sourceBranch: branch;
			destBranch: branch;
			validateResults: validateCommitResult;
			mergeDialogType: mergeDialogType;
		}>(MAT_DIALOG_DATA)
	);
	sourceBranch = computed(() => this.dialogData().sourceBranch);
	destBranch = computed(() => this.dialogData().destBranch);
	validateCommitResults = computed(() => this.dialogData().validateResults);
	mergeDialogType = computed(() => this.dialogData().mergeDialogType);

	sourceBranch$ = toObservable(this.sourceBranch);
	destBranch$ = toObservable(this.destBranch);
	validateCommitResults$ = toObservable(this.validateCommitResults);

	mergeBranchId = combineLatest([
		this.sourceBranch$,
		this.destBranch$,
		this._updateMergeBranchId,
	]).pipe(
		filter(
			([sourceBranch, destBranch, _]) =>
				sourceBranch.id != '' &&
				sourceBranch.id != '-1' &&
				destBranch.id != '' &&
				destBranch.id != '-1'
		),
		switchMap(([sourceBranch, destBranch, _]) =>
			this.commitBranchService.getMergeBranch(
				sourceBranch.id,
				destBranch.id
			)
		),
		map((id) => id.id),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	mergeData = this.mergeBranchId.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) =>
			this.commitBranchService.getMergeData(id).pipe(
				repeat({
					delay: () => this.commitBranchService.updatedMergeData,
				})
			)
		),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	mergeStatus = combineLatest([this.sourceBranch$, this.destBranch$]).pipe(
		filter(
			([sourceBranch, destBranch]) =>
				sourceBranch.id !== '' &&
				sourceBranch.id !== '-1' &&
				destBranch.id !== '' &&
				destBranch.id !== '-1'
		),
		switchMap(([sourceBranch, destBranch]) =>
			this.commitBranchService
				.validateCommit(sourceBranch.id, destBranch.id)
				.pipe(
					repeat({
						delay: () => this.commitBranchService.updatedMergeData,
					})
				)
		)
	);

	createMergeBranch() {
		combineLatest([this.sourceBranch$, this.destBranch$])
			.pipe(
				switchMap(([sourceBranch, destBranch]) =>
					this.commitBranchService
						.createMergeBranch(sourceBranch, destBranch)
						.pipe(
							switchMap((_) =>
								this.commitBranchService
									.loadMergeConflicts(
										sourceBranch.id,
										destBranch.id
									)
									.pipe(
										tap((_) => {
											this.commitBranchService.updateMergeData =
												true;
											this._updateMergeBranchId.next(
												true
											);
										})
									)
							)
						)
				)
			)
			.subscribe();
	}

	closeDialog() {
		this.dialogRef.close(false);
	}

	commitBranch() {
		this.dialogRef.close(true);
	}
}
