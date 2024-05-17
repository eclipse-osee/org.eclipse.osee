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
import { attribute } from '@osee/shared/types';
import {
	artifact,
	artifactTypeIcon,
	relation,
	teamWorkflow,
} from '@osee/shared/types/configuration-management';

export const DEFAULT_HIERARCHY_ROOT_ARTIFACT: artifact = {
	id: '197818',
	name: 'Default Hierarchy Root',
	typeId: '10',
	typeName: 'Root Artifact',
	icon: {
		icon: '',
		color: '',
		lightShade: '',
		darkShade: '',
		variant: '',
	},
	attributes: [],
	editable: false,
};

export interface artifactWithDirectRelations {
	artId: string;
	artName: string;
	artType: string;
	relations: relation[];
}

interface abstractTab {
	tabId: string;
	tabType: TabType;
	tabTitle: string;
	branchId: string;
	viewId: string;
}

export interface artifactTab extends abstractTab {
	tabType: 'Artifact';
	artifact: artifact;
}

export interface changeReportTab extends abstractTab {
	tabType: 'ChangeReport';
}

export interface teamWorkflowTab extends abstractTab {
	tabType: 'TeamWorkflow';
	teamWorkflowId: `${number}`;
}

export type tab = artifactTab | changeReportTab | teamWorkflowTab;

export interface artifactHierarchyOptions {
	showRelations: boolean;
}

export interface createChildArtifactDialogData {
	name: string;
	artifactTypeId: string;
	parentArtifactId: string;
	attributes: attribute[];
	option: artifactContextMenuOption;
}

export type TabType =
	| 'Artifact'
	| 'ChangeReport'
	| 'MarkdownEditor'
	| 'TeamWorkflow';

export interface artifactContextMenuOption {
	name: string;
	icon: artifactTypeIcon;
}
export type ExplorerPanel = 'Actions' | 'Artifacts' | 'Branches';
export interface deleteArtifactDialogData {
	artifact: artifact;
	option: artifactContextMenuOption;
}
