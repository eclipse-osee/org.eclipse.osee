/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { difference } from '@osee/shared/types/change-report';
import { showable } from './base-types/showable';
import { extendedFeature, extendedFeatureWithChanges } from './features/base';
import { configGroup } from './pl-config-configurations';
import { branchInfo } from '@osee/shared/types';

export type PlConfigApplicUIBranchMapping = {
	associatedArtifactId: string;
	branch: branchInfo;
	editable: boolean;
	features: (extendedFeature | extendedFeatureWithChanges)[];
	groups: configGroup[];
	parentBranch: branchInfo;
	views: (view | viewWithChanges)[];
};
export class PlConfigApplicUIBranchMappingImpl
	implements PlConfigApplicUIBranchMapping
{
	associatedArtifactId = '-1';
	branch: branchInfo = {
		idIntValue: 0,
		name: '',
		id: '0',
		viewId: '-1',
	};
	editable = false;
	features: (extendedFeature | extendedFeatureWithChanges)[] = [];
	groups: configGroup[] = [];
	parentBranch: branchInfo = {
		idIntValue: 0,
		name: '',
		id: '0',
		viewId: '-1',
	};
	views: (view | viewWithChanges)[] = [];
}

export type ConfigGroup = {} & NamedIdAndDescription & showable;
export type view = {
	hasFeatureApplicabilities: boolean;
	productApplicabilities?: string[];
} & NamedIdAndDescription &
	showable;

export type viewWithChanges = {
	deleted: boolean;
	added: boolean;
	changes: {
		name?: difference;
		hasFeatureApplicabilities?: difference;
		description?: difference<string>;
		productApplicabilities?: difference[];
	};
} & view;
export type viewWithChangesAndGroups = {
	groups: configGroup[];
} & viewWithChanges;
export type viewWithGroups = {
	groups: configGroup[];
} & view;
export type viewWithDescription = {
	groups: configGroup[];
	description: string;
} & view;
