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
export interface BranchListing {
	id: string;
	viewId: string;
	idIntValue: number;
	name: string;
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
}
