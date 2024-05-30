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
import { branch } from '@osee/shared/types';

export type branchCommitStatus = {
	branch: branch;
	commitStatus: commitStatus;
};

export const COMMITSTATUS = {
	Branch_Commit_Disabled: 'Branch Commit Disabled',
	Branch_Not_Configured: 'Branch Not Configured',
	Commit_Needed: 'Uncommitted',
	Commit_Overridden: 'Commit Overridden',
	Committed: 'Committed',
	Committed_With_Merge: 'Committed With Merge',
	Merge_In_Progress: 'Merge In Progress',
	No_Commit_Needed: 'No Commit Needed',
	Rebaseline_In_Progress: 'Rebaseline in Progress',
	Working_Branch_Not_Created: 'Working Branch Not Created',
} as const;

export type commitStatus = keyof typeof COMMITSTATUS;
