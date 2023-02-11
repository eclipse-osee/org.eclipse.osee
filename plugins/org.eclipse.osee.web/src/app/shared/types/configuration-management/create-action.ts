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
import { PRIORITY } from './priority.enum';
import { actionableItem } from './actionable-item';

export interface CreateActionInterface {
	originator: user;
	actionableItem: actionableItem;
	targetedVersion: string;
	title: string;
	description: string;
	priority: PRIORITY;
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
	constructor(currentUser: user) {
		this.originator = currentUser;
	}
	priority = PRIORITY.LowestPriority;
	originator: user;
	actionableItem: actionableItem = new actionableItem();
	targetedVersion: string = '';
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
