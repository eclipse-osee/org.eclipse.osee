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
import { NamedId, XResultData } from '@osee/shared/types';
import { user } from '@osee/shared/types/auth';
import { teamWorkflowDetails } from '@osee/shared/types/configuration-management';

export type actionBranchData = {
	branchName: string;
	parent: string;
	applyAccess: boolean;
	results?: XResultData;
	validate: boolean;
	branchType: number;
	associatedArt: NamedId;
	author: NamedId;
	creationComment: string;
};

export class ActionBranchDataImpl implements actionBranchData {
	branchName = '';
	parent = '-1';
	applyAccess = false;
	validate = false;
	branchType = 0;
	associatedArt: NamedId = { id: '-1', name: '' };
	author: NamedId = { id: '-1', name: '' };
	creationComment = '';

	constructor(teamWf: teamWorkflowDetails, user: user, validate: boolean) {
		this.branchName = teamWf.AtsId + ' - ' + teamWf.Name;
		this.parent = teamWf.parentBranch.id;
		this.validate = validate;
		this.branchType = 0;
		this.associatedArt = { id: `${teamWf.id}`, name: '' };
		this.author = { id: user.id, name: user.name };
		this.creationComment = 'Creating working branch for ' + this.branchName;
	}
}
