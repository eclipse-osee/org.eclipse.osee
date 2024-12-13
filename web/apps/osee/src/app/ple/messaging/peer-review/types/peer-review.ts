/*********************************************************************
 * Copyright (c) 2021 Boeing
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

export type branchSelected = {
	branch: branch;
	selected: boolean;
	selectable: boolean;
	committedToBaseline: boolean;
};

export type peerReviewApplyData = {
	addBranches: `${number}`[];
	removeBranches: `${number}`[];
};

export type applyResult = {
	success: boolean;
	statusText: string;
};
