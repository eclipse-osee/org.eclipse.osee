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
import { NamedIdAndDescription } from '../named-id';
import { PRIORITY } from './priority.enum';
import { CreateAction } from './create-action';

export interface CreateNewActionInterface {
	title: string;
	description: string;
	aiIds: string[];
	asUserId: string;
	createdByUserId: string;
	versionId: string;
	priority: PRIORITY;
	changeType: NamedIdAndDescription;
}
export class CreateNewAction implements CreateNewActionInterface {
	constructor(config?: CreateAction) {
		this.title = (config && config.title) || '';
		this.description = (config && config.description) || '';
		if (config) {
			this.aiIds = [config.actionableItem.id];
		}
		this.asUserId = (config && config.originator.id) || '';
		this.createdByUserId = (config && config.originator.id) || '';
		this.versionId = (config && config.targetedVersion) || '';
		this.priority = (config && config.priority) || PRIORITY.LowestPriority;
		this.changeType = (config && {
			id: config.changeType.id,
			name: config.changeType.name,
			description: config.changeType.description,
		}) || { id: '-1', name: '', description: '' };
	}
	changeType: NamedIdAndDescription = { id: '-1', name: '', description: '' };
	priority: PRIORITY = PRIORITY.LowestPriority;
	title: string = '';
	description: string = '';
	aiIds: string[] = [];
	asUserId: string = '';
	createdByUserId: string = '';
	versionId: string = '';
}
