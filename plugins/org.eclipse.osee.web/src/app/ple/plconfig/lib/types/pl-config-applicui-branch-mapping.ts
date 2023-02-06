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
import { NamedIdAndDescription } from '../../../../types/NamedId';
import { difference } from 'src/app/types/change-report/change-report';
import { NamedId } from '../../../../types/NamedId';
import { showable } from './base-types/showable';
import { extendedFeature, extendedFeatureWithChanges } from './features/base';
import {
	configGroup,
	configGroupWithChanges,
	configurationGroup,
} from './pl-config-configurations';
import { branchInfo } from '../../../../types/branches/branch';

export interface PlConfigApplicUIBranchMapping {
	associatedArtifactId: string;
	branch: branchInfo;
	editable: boolean;
	features: (extendedFeature | extendedFeatureWithChanges)[];
	groups: (configGroup | configGroupWithChanges)[];
	parentBranch: branchInfo;
	views: (view | viewWithChanges)[];
}
export class PlConfigApplicUIBranchMappingImpl
	implements PlConfigApplicUIBranchMapping
{
	associatedArtifactId: string = '-1';
	branch: branchInfo = {
		idIntValue: 0,
		name: '',
		id: '0',
		viewId: '-1',
	};
	editable: boolean = false;
	features: (extendedFeature | extendedFeatureWithChanges)[] = [];
	groups: (configGroup | configGroupWithChanges)[] = [];
	parentBranch: branchInfo = {
		idIntValue: 0,
		name: '',
		id: '0',
		viewId: '-1',
	};
	views: (view | viewWithChanges)[] = [];
}

export interface ConfigGroup extends NamedIdAndDescription, showable {}
export interface view extends NamedIdAndDescription, showable {
	hasFeatureApplicabilities: boolean;
	productApplicabilities?: string[];
}

export interface viewWithChanges extends view {
	deleted: boolean;
	added: boolean;
	changes: {
		name?: difference;
		hasFeatureApplicabilities?: difference;
		productApplicabilities?: difference[];
	};
}
export interface viewWithChangesAndGroups extends viewWithChanges {
	groups: (configGroup | configGroupWithChanges)[];
}
export interface viewWithGroups extends view {
	groups: (configGroup | configGroupWithChanges)[];
}
export interface viewWithDescription extends view {
	groups: (configGroup | configGroupWithChanges)[];
	description: string;
}
