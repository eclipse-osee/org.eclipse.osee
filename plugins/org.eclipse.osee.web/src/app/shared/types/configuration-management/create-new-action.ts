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
	originator: string;
	assignees: string;
	points: string;
	unplanned: boolean;
	workPackage: string;
	featureGroup: string;
	sprint: string;
	attrValues: { [key: string]: string };
}
export class CreateNewAction implements CreateNewActionInterface {
	constructor(config?: CreateAction) {
		if (config) {
			this.title = config.title;
			this.description = config.description;
			this.aiIds = [config.actionableItem.id];
			this.asUserId = config.createdByUser.id;
			this.createdByUserId = config.createdByUser.id;
			this.versionId = config.targetedVersion;
			this.priority = config.priority;
			this.changeType = {
				id: config.changeType.id,
				name: config.changeType.name,
				description: config.changeType.description,
			};
			this.originator = config.originator.id;
			this.assignees = config.assignees;
			this.points = config.points;
			this.unplanned = config.unplanned;
			this.workPackage = config.workPackage;
			this.featureGroup = config.featureGroup;
			this.sprint = config.sprint;
			this.attrValues = config.attrValues;
		}
	}
	changeType: NamedIdAndDescription = { id: '-1', name: '', description: '' };
	priority: Priority = PRIORITIES.LowestPriority;
	title: string = '';
	description: string = '';
	aiIds: string[] = [];
	asUserId: string = '';
	createdByUserId: string = '';
	versionId: targetedVersion = targetedVersionSentinel;
	originator: string = '';
	assignees: string = '';
	points: string = '';
	unplanned: boolean = false;
	workPackage: string = '';
	featureGroup: string = '';
	sprint: string = '';
	attrValues: { [key: string]: string } = {};
}
