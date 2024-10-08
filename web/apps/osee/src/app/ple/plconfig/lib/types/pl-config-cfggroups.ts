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
import { view } from './pl-config-applicui-branch-mapping';

export type addCfgGroup = {
	title: string;
	description: string;
};

export type CfgGroupDialog = {
	configGroup: {
		name: string;
		description: string;
		id: string;
		views: view[];
		configurations: string[];
	};
	editable: boolean;
};
export type ConfigurationGroupDefinition = {
	id?: string;
	name: string;
	description: string;
	configurations?: string[];
};
