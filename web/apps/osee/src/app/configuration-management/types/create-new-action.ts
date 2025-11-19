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
import { NamedIdAndDescription } from '@osee/shared/types';
import {
	PRIORITIES,
	Priority,
	targetedVersion,
	targetedVersionSentinel,
} from '@osee/shared/types/configuration-management';
import { CreateAction } from './create-action';

export type CreateNewActionInterface = {
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
	opName: string;
	unplanned: boolean;
	workPackage: string;
	featureGroup: string;
	sprint: string;
	attrValues: Record<string, string>;
	parentAction: string;
};
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
			this.parentAction = config.parentAction;
		}
	}
	changeType: NamedIdAndDescription = { id: '-1', name: '', description: '' };
	priority: Priority = PRIORITIES.LowestPriority;
	title = '';
	opName = 'Create Action Web';
	description = '';
	aiIds: string[] = [];
	asUserId = '';
	createdByUserId = '';
	versionId: targetedVersion = targetedVersionSentinel;
	originator = '';
	assignees = '';
	points = '';
	unplanned = false;
	workPackage = '';
	featureGroup = '';
	sprint = '';
	attrValues: Record<string, string> = {};
	parentAction = '';
}
