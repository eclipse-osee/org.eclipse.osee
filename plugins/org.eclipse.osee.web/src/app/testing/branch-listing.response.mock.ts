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
import { branch } from '../types/branches/branch';

export const testBranchListing: branch[] = [
	{
		id: '890328402',
		name: 'Branch 1',
		viewId: '-1',
		associatedArtifact: '-1',
		baselineTx: '937',
		parentTx: '21',
		parentBranch: {
			id: '8',
			viewId: '-1',
		},
		branchState: '1',
		branchType: '2',
		inheritAccessControl: false,
		archived: false,
		shortName: 'Product Line',
		idIntValue: 890328402,
	},
	{
		id: '890328403',
		name: 'Branch 2',
		viewId: '-1',
		associatedArtifact: '-1',
		baselineTx: '937',
		parentTx: '21',
		parentBranch: {
			id: '890328402',
			viewId: '-1',
		},
		branchState: '1',
		branchType: '0',
		inheritAccessControl: false,
		archived: false,
		shortName: 'Working Branch',
		idIntValue: 890328403,
	},
	{
		id: '890328404',
		name: 'Branch 3',
		viewId: '-1',
		associatedArtifact: '46512388465',
		baselineTx: '937',
		parentTx: '21',
		parentBranch: {
			id: '890328402',
			viewId: '-1',
		},
		branchState: '0',
		branchType: '0',
		inheritAccessControl: false,
		archived: false,
		shortName: 'TW197- Actioned Branch',
		idIntValue: 890328404,
	},
];
