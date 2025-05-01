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
import { branch, storeType } from '@osee/shared/types';

export type validateCommitResult = {
	commitable: boolean;
	conflictCount: number;
	conflictsResolved: number;
};

export type mergeConflict = {
	artId: string;
	artType: string;
	attrTypeId: string;
	attrId: string;
	workingTxId: string;
	workingTxCurrent: string;
	workingModType: string;
	workingGammaId: string;
	currentDestTxId: string;
	currentDestTxCurrent: string;
	currentDestModType: string;
	currentDestGammaId: string;
	baselineTxTxId: string;
	baselineTxTxCurrent: string;
	baselineTxModType: string;
	baselineTxGammaId: string;
};

export type mergeData = {
	artId: string;
	artType: string;
	name: string;
	conflictType: ConflictType;
	conflictStatus: ConflictStatus;
	conflictId: number;
	attrMergeData: attrMergeData;
};

export type attrMergeData = {
	attrType: `${number}`;
	attrId: string;
	attrTypeName: string;
	sourceValue: string;
	mergeValue: string;
	destValue: string;
	sourceUri: string;
	mergeUri: string;
	destUri: string;
	sourceGammaId: string;
	destGammaId: string;
	storeType: storeType;
};

export type ConflictUpdateData = {
	sourceGammaId: string;
	destGammaId: string;
	status: ConflictStatus;
	type: ConflictType;
	conflictId: number;
	mergeBranchId: string;
};

export type CreateBranchResponse = {
	id: string;
	name: string;
	viewId: string;
	associatedArtifact: string;
	baselineTx: string;
	parentTx: string;
	parentBranch: {
		id: string;
		viewId: string;
	};
	branchState: string;
	branchType: string;
	inheritAccessControl: boolean;
	archived: boolean;
	shortName: string;
	idIntValue: number;
};

export type CreateBranchDetails = {
	branchName: string;
	parentBranch: string;
	associatedArtifact: string;
	branchType: string;
	sourceTransaction: { id: string; branchId: string };
	mergeBaselineTransaction: { id: string; branchId: string };
	creationComment: string;
	mergeAddressingQueryId: string;
	mergeDestinationBranchId: string;
	mergeSourceBranchId: string;
	txCopyBranchType: boolean;
};

export class CreateWorkingBranchDetails implements CreateBranchDetails {
	constructor(newBranchName: string, currentBranch: branch) {
		this.branchName = newBranchName;
		this.parentBranch = currentBranch.id;
		this.creationComment = 'Creating working branch: ' + this.branchName;
	}
	branchName = '';
	parentBranch = '-1';
	associatedArtifact = '-1';
	branchType = '0'; // 0 = working branch
	sourceTransaction: { id: string; branchId: string } = {
		id: '-1',
		branchId: '-1',
	};
	mergeBaselineTransaction: { id: string; branchId: string } = {
		id: '-1',
		branchId: '-1',
	};
	creationComment = 'Creating working branch';
	mergeAddressingQueryId = '0';
	mergeDestinationBranchId = '-1';
	mergeSourceBranchId = '-1';
	txCopyBranchType = false;
}

export class CreateMergeBranchDetails implements CreateBranchDetails {
	constructor(sourceBranch: branch, parentBranch: branch) {
		this.branchName = `Merge ${sourceBranch.name} <=> ${parentBranch.name}`;
		this.parentBranch = sourceBranch.id;
		this.associatedArtifact = sourceBranch.associatedArtifact;
		this.mergeBaselineTransaction = {
			id: sourceBranch.baselineTx,
			branchId: sourceBranch.id,
		};
		this.mergeDestinationBranchId = parentBranch.id;
		this.mergeSourceBranchId = sourceBranch.id;
	}
	branchName = '';
	parentBranch = '-1';
	associatedArtifact = '-1';
	branchType = '3';
	sourceTransaction: { id: string; branchId: string } = {
		id: '-1',
		branchId: '-1',
	};
	mergeBaselineTransaction: { id: string; branchId: string } = {
		id: '-1',
		branchId: '-1',
	};
	creationComment = 'Creating merge branch';
	mergeAddressingQueryId = '0';
	mergeDestinationBranchId = '-1';
	mergeSourceBranchId = '-1';
	txCopyBranchType = false;
}

export type ConflictType = 'ARTIFACT' | 'ATTRIBUTE' | 'RELATION';

export type ConflictStatus =
	| 'NOT_CONFLICTED'
	| 'UNTOUCHED'
	| 'EDITED'
	| 'RESOLVED'
	| 'OUT_OF_DATE_RESOLVED'
	| 'COMMITTED'
	| 'INFORMATIONAL'
	| 'OUT_OF_DATE'
	| 'PREVIOUS_MERGE_APPLIED_SUCCESS'
	| 'PREVIOUS_MERGE_APPLIED_CAUTION';
