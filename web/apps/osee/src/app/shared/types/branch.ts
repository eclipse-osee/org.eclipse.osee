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

export type branch = {
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
	categories: branchCategory[];
} & viewedId;

export type branchCategory = {
	id: string;
	name: string;
};

export type branchInfo = {
	idIntValue: number;
	name: string;
} & branchHeader;

export type branchHeader = {} & viewedId;

export const branchCategorySentinel: branchCategory = {
	id: '-1',
	name: 'Sentinel',
};

export const branchSentinel: branch = {
	id: '-1',
	idIntValue: -1,
	name: '',
	associatedArtifact: '-1',
	baselineTx: '-1',
	parentTx: '-1',
	parentBranch: { id: '-1', viewId: '-1' },
	branchState: '',
	branchType: '',
	inheritAccessControl: false,
	archived: false,
	shortName: '',
	categories: [branchCategorySentinel],
	viewId: '-1',
};
