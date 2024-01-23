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
import { user } from '../auth';
import { PRIORITIES, Priority } from './priority';
import { actionableItem } from './actionable-item';
import { targetedVersion, targetedVersionSentinel } from './targeted-version';

export interface CreateActionInterface {
	defaultWorkType: string;
	originator: user;
	actionableItem: actionableItem;
	targetedVersion: targetedVersion;
	title: string;
	description: string;
	priority: Priority;
	changeType: {
		id: string;
		name: string;
		idString: string;
		idIntValue: number;
		description: string;
	};
}
// only used in action dropdown component
export class CreateAction implements CreateActionInterface {
	constructor(currentUser: user, workType: string = '') {
		this.originator = currentUser;
		this.defaultWorkType = workType;
	}
	defaultWorkType = '';
	priority = PRIORITIES.LowestPriority;
	originator: user;
	actionableItem: actionableItem = new actionableItem();
	targetedVersion: targetedVersion = targetedVersionSentinel;
	title: string = '';
	description: string = '';
	changeType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
		description: '',
	};
}
