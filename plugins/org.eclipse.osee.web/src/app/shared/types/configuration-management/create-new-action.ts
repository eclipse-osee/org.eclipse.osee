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
import { PRIORITIES, Priority } from './priority';
import { CreateAction } from './create-action';
import {
	targetedVersion,
	targetedVersionSentinel,
} from 'src/app/shared/types/configuration-management/targeted-version';

export interface CreateNewActionInterface {
	title: string;
	description: string;
	aiIds: string[];
	asUserId: string;
	createdByUserId: string;
	versionId: targetedVersion;
	priority: Priority;
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
		this.versionId =
			(config && config.targetedVersion) || targetedVersionSentinel;
		this.priority =
			(config && config.priority) || PRIORITIES.LowestPriority;
		this.changeType = (config && {
			id: config.changeType.id,
			name: config.changeType.name,
			description: config.changeType.description,
		}) || { id: '-1', name: '', description: '' };
	}
	changeType: NamedIdAndDescription = { id: '-1', name: '', description: '' };
	priority: Priority = PRIORITIES.LowestPriority;
	title: string = '';
	description: string = '';
	aiIds: string[] = [];
	asUserId: string = '';
	createdByUserId: string = '';
	versionId: targetedVersion = targetedVersionSentinel;
}
