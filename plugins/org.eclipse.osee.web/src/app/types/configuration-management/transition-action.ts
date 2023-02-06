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
import { user } from 'src/app/types/user';
import { action } from './action';
import { NamedId } from '../NamedId';

export interface workItem extends NamedId {}
export interface transitionActionInterface {
	toStateName: string;
	name: string;
	transitionUserArtId: string;
	workItemIds: workItem[];
}

export class transitionAction implements transitionActionInterface {
	constructor(
		toStateName?: string,
		name?: string,
		actions?: action[],
		currentUser?: user
	) {
		this.toStateName = toStateName || '';
		this.name = name || '';
		this.transitionUserArtId = (currentUser && currentUser.id) || '';
		if (actions?.values) {
			actions?.forEach((element) => {
				this.workItemIds.push({
					id: element.id.toString(),
					name: element.Name,
				});
			});
		} else {
			this.workItemIds = [];
		}
	}
	toStateName: string = '';
	name: string = '';
	transitionUserArtId: string = '';
	workItemIds: workItem[] = [];
}
