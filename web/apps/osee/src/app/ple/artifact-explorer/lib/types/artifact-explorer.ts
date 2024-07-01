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
import {
	artifactWithRelations,
	artifactTypeIcon,
	operationType,
} from '@osee/artifact-with-relations/types';
import { attribute } from '@osee/shared/types';

interface abstractTab {
	tabId: string;
	tabType: TabType;
	tabTitle: string;
	branchId: string;
	viewId: string;
}

export interface artifactTab extends abstractTab {
	tabType: 'Artifact';
	artifact: artifactWithRelations;
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
	operationType: operationType;
}

export type TabType =
	| 'Artifact'
	| 'ChangeReport'
	| 'MarkdownEditor'
	| 'TeamWorkflow';

export type ExplorerPanel = 'Actions' | 'Artifacts' | 'Branches';

export interface deleteArtifactDialogData {
	artifact: artifactWithRelations;
	operationType: operationType;
}

// Publishing
export interface publishMarkdownDialogData {
	templateId: string;
	operationType: operationType;
}

export interface publishingTemplateKeyGroups {
	publishingTemplateKeyGroupList: publishingTemplateKey[];
}

export interface publishingTemplateKey {
	identifier: key;
	matchCriteria: {
		key: key[];
	};
	name: key;
	safeName: key;
}

export interface key {
	key: string;
	keyType: string;
}

export interface publishMarkdownAsHtmlRequestData {
	publishMarkdownAsHtmlRequestData: publishingRequestData;
}

export interface publishingRequestData {
	artifactIds: string[];
	publishingRendererOptions: publishingRendererOptions;
	publishingTemplateRequest: publishingTemplateRequest;
}

export interface publishingRendererOptions {
	Branch: publishingRendererOptionsBranch;
	PublishingFormat: publishingRendererOptionsPublishingFormat;
}

export interface publishingRendererOptionsBranch {
	id: string;
	viewId: string;
}

export interface publishingRendererOptionsPublishingFormat {
	formatIndicator: string;
}

export interface publishingTemplateRequest {
	byOptions: boolean;
	formatIndicator: string;
	templateId: string;
}
