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
import { user } from '@osee/shared/types/auth';
import {
	PRIORITIES,
	Priority,
	actionableItem,
	targetedVersion,
	targetedVersionSentinel,
} from '@osee/shared/types/configuration-management';

export type CreateActionInterface = {
	createdByUser: user;
	defaultWorkType: string;
	originator: user;
	assignees: string;
	actionableItem: actionableItem;
	targetedVersion: targetedVersion;
	title: string;
	description: string;
	priority: Priority;
	points: string;
	unplanned: boolean;
	workPackage: string;
	featureGroup: string;
	sprint: string;
	attrValues: Record<string, string>;
	createBranchDefault: boolean;
	/** The change-type name string, e.g. "Improvement", "Bug", "Support". */
	changeType: string;
	parentAction: string;
};
// only used in action dropdown component
export class CreateAction implements CreateActionInterface {
	constructor(currentUser: user, allowParentAction = true, workType = '') {
		this.createdByUser = currentUser;
		this.originator = currentUser;
		this.defaultWorkType = workType;
		this.allowParentAction = allowParentAction;
	}
	createdByUser: user;
	defaultWorkType = '';
	priority = PRIORITIES.LowestPriority;
	originator: user;
	assignees = '';
	actionableItem: actionableItem = new actionableItem();
	targetedVersion: targetedVersion = targetedVersionSentinel;
	title = '';
	description = '';
	points = '';
	unplanned = false;
	workPackage = '';
	featureGroup = '';
	sprint = '';
	attrValues: Record<string, string> = {};
	createBranchDefault = false;
	changeType = '';
	allowParentAction = true;
	parentAction = '';
}
