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
import { Component, Inject, OnInit } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { AsyncPipe, NgClass } from '@angular/common';
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
import { MatButtonModule } from '@angular/material/button';
import {
	branch,
	branchSentinel,
	validateCommitResult,
} from '@osee/shared/types';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommitBranchService } from '@osee/shared/services';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-merge-manager-dialog',
	standalone: true,
	imports: [
		AsyncPipe,
		NgClass,
		MatDialogModule,
		MatButtonModule,
		MatIconModule,
		MatTooltipModule,
		MergeManagerTableComponent,
	],
	templateUrl: './merge-manager-dialog.component.html',
	styles: ['tooltip-width {max-width: unset}'],
})
export class MergeManagerDialogComponent implements OnInit {
	sourceBranch = new BehaviorSubject<branch>(branchSentinel);
	parentBranch = new BehaviorSubject<branch>(branchSentinel);
	validateCommitResults = new BehaviorSubject<validateCommitResult>({
		commitable: false,
		conflictCount: 0,
		conflictsResolved: 0,
	});
	private _updateMergeBranchId = new BehaviorSubject<boolean>(false);

	mergeBranchId = combineLatest([
		this.sourceBranch,
		this.parentBranch,
		this._updateMergeBranchId,
	]).pipe(
		filter(
			([sourceBranch, parentBranch, _]) =>
				sourceBranch.id != '' &&
				sourceBranch.id != '-1' &&
				parentBranch.id != '' &&
				parentBranch.id != '-1'
		),
		switchMap(([sourceBranch, parentBranch, _]) =>
			this.commitBranchService.getMergeBranch(
				sourceBranch.id,
				parentBranch.id
			)
		),
		map((id) => id.id),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	mergeData = this.mergeBranchId.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) => this.commitBranchService.getMergeData(id)),
		takeUntilDestroyed(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	mergeStatus = this.sourceBranch.pipe(
		filter((branch) => branch.id !== '' && branch.id !== '-1'),
		switchMap((branch) =>
			this.commitBranchService.validateCommit(branch).pipe(
				repeat({
					delay: () => this.commitBranchService.updatedMergeData,
				})
			)
		)
	);

	constructor(
		public dialogRef: MatDialogRef<MergeManagerDialogComponent>,
		@Inject(MAT_DIALOG_DATA)
		public data: {
			sourceBranch: branch;
			parentBranch: branch;
			validateResults: validateCommitResult;
		},
		private commitBranchService: CommitBranchService
	) {}

	ngOnInit(): void {
		this.sourceBranch.next(this.data.sourceBranch);
		this.parentBranch.next(this.data.parentBranch);
		this.validateCommitResults.next(this.data.validateResults);
	}

	createMergeBranch() {
		combineLatest([this.sourceBranch, this.parentBranch])
			.pipe(
				switchMap(([sourceBranch, parentBranch]) =>
					this.commitBranchService
						.createMergeBranch(sourceBranch, parentBranch)
						.pipe(
							switchMap((_) =>
								this.commitBranchService
									.loadMergeConflicts(
										sourceBranch.id,
										parentBranch.id
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
