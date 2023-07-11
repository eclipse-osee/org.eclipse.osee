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
import { viewedId } from '@osee/shared/types';
export interface branch extends viewedId {
	idIntValue: number;
	name: string;
	associatedArtifact: string;
	baselineTx: string;
	parentTx: string;
	parentBranch: viewedId;
	branchState: string;
	branchType: string;
	inheritAccessControl: boolean;
	archived: boolean;
	shortName: string;
}
export interface branchInfo extends branchHeader {
	idIntValue: number;
	name: string;
}
export interface branchHeader extends viewedId {}
