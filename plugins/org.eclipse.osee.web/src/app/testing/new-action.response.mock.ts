/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { newActionResponse } from '@osee/shared/types/configuration-management';
import { MockXResultData } from './XResultData.response.mock';
import { branch } from '@osee/shared/types';

const BranchListingBranch: branch = {
	name: 'name',
	idIntValue: 0,
	id: '0',
	viewId: '0',
	associatedArtifact: '0',
	baselineTx: '0',
	parentTx: '0',
	parentBranch: { id: '0', viewId: '0' },
	branchState: '0',
	branchType: '0',
	inheritAccessControl: false,
	archived: false,
	shortName: 'name',
};

export const testnewActionResponse: newActionResponse = {
	action: null,
	results: MockXResultData,
	teamWfs: [],
	workingBranchId: BranchListingBranch,
};
