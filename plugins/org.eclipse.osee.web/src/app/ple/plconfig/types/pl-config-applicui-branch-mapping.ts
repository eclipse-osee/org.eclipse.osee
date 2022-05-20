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
import { difference } from 'src/app/types/change-report/change-report';
import { NameValuePair } from './base-types/NameValuePair';
import { showable } from './base-types/showable';
import { extendedFeature, extendedFeatureWithChanges } from './features/base';
import { PlConfigApplicUIBranch } from './pl-config-branch';
import {
  configGroup,
  configGroupWithChanges,
  configurationGroup,
} from './pl-config-configurations';

export interface PlConfigApplicUIBranchMapping {
  associatedArtifactId: string;
  branch: PlConfigApplicUIBranch;
  editable: boolean;
  features: (extendedFeature | extendedFeatureWithChanges)[];
  groups: (configGroup | configGroupWithChanges)[];
  parentBranch: PlConfigApplicUIBranch;
  views: (view | viewWithChanges)[];
}
export class PlConfigApplicUIBranchMappingImpl
  implements PlConfigApplicUIBranchMapping
{
  associatedArtifactId: string = '-1';
  branch: PlConfigApplicUIBranch = {
    idIntValue: 0,
    name: '',
    id: '0',
    viewId: '-1',
  };
  editable: boolean = false;
  features: (extendedFeature | extendedFeatureWithChanges)[] = [];
  groups: (configGroup | configGroupWithChanges)[] = [];
  parentBranch: PlConfigApplicUIBranch = {
    idIntValue: 0,
    name: '',
    id: '0',
    viewId: '-1',
  };
  views: (view | viewWithChanges)[] = [];
}

export interface ConfigGroup extends NameValuePair, showable {}
export interface view extends NameValuePair, showable {
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
