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
import { MockXResultData } from '@osee/shared/testing';
import { branchCategorySentinel } from '@osee/shared/types';
import { newActionResponse } from '@osee/shared/types/configuration-management';

export const testnewActionResponse: newActionResponse = {
	action: null,
	results: MockXResultData,
	teamWfs: [],
	workingBranchId: {
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
		categories: [branchCategorySentinel],
	},
};
