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
import { Injectable } from '@angular/core';
import { UserDataAccountService } from '@osee/auth';
import {
	ConflictUpdateData,
	CreateBranchDetails,
	CreateMergeBranchDetails,
	mergeData,
} from '@osee/commit/types';
import {
	BranchCommitEventService,
	BranchInfoService,
	UiService,
} from '@osee/shared/services';
import { TransactionService } from '@osee/shared/transactions';
import {
	attributeType,
	branch,
	modifyArtifact,
	transaction,
} from '@osee/shared/types';
import { Subject, of, switchMap, take, tap } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CommitBranchService {
	private _updatedMergeData = new Subject();

	constructor(
		private uiService: UiService,
		private branchInfoService: BranchInfoService,
		private transactionService: TransactionService,
		private accountService: UserDataAccountService,
		private eventService: BranchCommitEventService
	) {}

	getBranch(branchId: string) {
		return this.branchInfoService.getBranch(branchId);
	}

	getMergeData(branchId: string) {
		return this.branchInfoService.getMergeData(branchId);
	}

	getMergeBranch(sourceBranchId: string, destBranchId: string) {
		return this.branchInfoService.getMergeBranchId(
			sourceBranchId,
			destBranchId
		);
	}

	createMergeBranch(sourceBranch: branch, destBranch: branch) {
		const details: CreateBranchDetails = new CreateMergeBranchDetails(
			sourceBranch,
			destBranch
		);
		return this.branchInfoService.createBranch(details);
	}

	loadMergeConflicts(sourceBranchId: string, destBranchId: string) {
		return this.branchInfoService.loadMergeConflicts(
			sourceBranchId,
			destBranchId
		);
	}

	validateCommit(sourceBranchId: string, destBranchId: string) {
		return this.branchInfoService.validateCommit(
			sourceBranchId,
			destBranchId
		);
	}

	updateMergeConflicts(
		data: mergeData,
		mergeBranchId: string,
		branchId: string,
		parentBranchId: string
	) {
		const tx: transaction = {
			branch: mergeBranchId,
			txComment:
				'Update merge value for artifact: ' +
				data.name +
				', attribute: ' +
				data.attrMergeData.attrTypeName,
		};
		const attribute: attributeType = {
			typeId: data.attrMergeData.attrType,
			value: data.attrMergeData.mergeValue,
		};
		const modifyArtifact: modifyArtifact = {
			id: data.artId,
			setAttributes: [attribute],
		};
		tx.modifyArtifacts = [modifyArtifact];

		return this.transactionService.performMutation(tx).pipe(
			switchMap((res) => {
				if (!res.results.success) {
					return of();
				}

				const conflictUpdateData: ConflictUpdateData = {
					conflictId: data.conflictId,
					sourceGammaId: data.attrMergeData.sourceGammaId,
					destGammaId: data.attrMergeData.destGammaId,
					mergeBranchId: mergeBranchId,
					status: data.conflictStatus,
					type: data.conflictType,
				};
				return this.branchInfoService
					.updateMergeConflicts(branchId, parentBranchId, [
						conflictUpdateData,
					])
					.pipe(tap((_) => (this.updateMergeData = true)));
			})
		);
	}

	commitBranch(branchId: string, parentBranchId: string) {
		return this.accountService.user.pipe(
			take(1),
			switchMap((user) =>
				this.branchInfoService
					.commitBranch(branchId, parentBranchId, {
						committer: user.id,
						archive: 'false',
					})
					.pipe(
						tap((commitResp) => {
							if (!commitResp.success) {
								this.uiService.ErrorText =
									'Error committing branch';
							} else {
								this.eventService.sendEvent(branchId);
							}
						})
					)
			)
		);
	}

	updateFromParent(branchId: string) {
		return this.branchInfoService.updateFromParent(branchId);
	}

	set updateMergeData(value: boolean) {
		this._updatedMergeData.next(value);
	}

	get updatedMergeData() {
		return this._updatedMergeData;
	}
}
