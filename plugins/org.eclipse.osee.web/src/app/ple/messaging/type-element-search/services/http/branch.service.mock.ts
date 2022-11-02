/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import { of } from 'rxjs';
import { BranchListing } from 'src/app/types/branches/BranchListing';
import { BranchService } from './branch.service';

export const branchListingMock1 = {
	id: '8',
	viewId: '-1',
	idIntValue: 8,
	name: 'test',
	associatedArtifact: '-1',
	baselineTx: '-1',
	parentTx: '-1',
	parentBranch: {
		id: '10',
		viewId: '-1',
	},
	branchState: '0',
	branchType: '0',
	inheritAccessControl: false,
	archived: false,
	shortName: 'test',
};
export const branchServiceMock: Partial<BranchService> = {
	getBranches: function (type: string) {
		return of<BranchListing[]>([branchListingMock1]);
	},
};
