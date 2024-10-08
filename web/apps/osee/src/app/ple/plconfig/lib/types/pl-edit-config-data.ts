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
import {
	ConfigGroup,
	view,
	viewWithChangesAndGroups,
	viewWithGroups,
	viewWithDescription,
} from './pl-config-applicui-branch-mapping';

export class PLEditConfigData implements ConfigData {
	constructor(
		currentConfig?: view,
		ConfigurationToCopyFrom?: view,
		productApplicabilities?: string[],
		editable?: boolean,
		groups?: ConfigGroup[]
	) {
		if (currentConfig) {
			this.currentConfig = { ...currentConfig, groups: [] };
		}
		if (ConfigurationToCopyFrom) {
			this.copyFrom = ConfigurationToCopyFrom;
		}
		if (productApplicabilities) {
			this.productApplicabilities = productApplicabilities;
		}
		if (editable) {
			this.editable = editable;
		}
		if (groups) {
			this.group = groups;
		}
	}
	productApplicabilities: string[] = [];
	currentBranch = '';
	currentConfig:
		| viewWithChangesAndGroups
		| viewWithGroups
		| viewWithDescription = {
		id: '-1',
		name: '',
		description: '',
		hasFeatureApplicabilities: false,
		groups: [],
	};
	copyFrom: view = {
		id: '-1',
		name: '',
		description: '',
		hasFeatureApplicabilities: false,
	};
	group: ConfigGroup[] = [];
	editable = false;
}
export type copyFrom = {
	copyFrom: number;
};
export type PLAddConfigData = {
	title: string;
	description: string;
} & ConfigData;
type ConfigData = {
	copyFrom: view;
	group: ConfigGroup[];
	productApplicabilities: string[];
};
