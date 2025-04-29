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
	operationType,
} from '@osee/artifact-with-relations/types';
import { attribute } from '@osee/shared/types';

type abstractTab = {
	tabId: string;
	tabType: TabType;
	tabTitle: string;
	branchId: string;
	branchName: string;
	viewId: string;
};

export type artifactTab = {
	tabType: 'Artifact';
	artifact: artifactWithRelations;
} & abstractTab;

export type changeReportTab = {
	tabType: 'ChangeReport';
} & abstractTab;

export type teamWorkflowTab = {
	tabType: 'TeamWorkflow';
	teamWorkflowId: `${number}`;
} & abstractTab;

export type tab = artifactTab | changeReportTab | teamWorkflowTab;

export type artifactHierarchyOptions = {
	showRelations: boolean;
};

export type createChildArtifactDialogData = {
	name: string;
	artifactTypeId: string;
	parentArtifactId: string;
	attributes: attribute[];
	operationType: operationType;
};

export type TabType =
	| 'Artifact'
	| 'ChangeReport'
	| 'MarkdownEditor'
	| 'TeamWorkflow';

export type ExplorerPanel = 'Actions' | 'Artifacts' | 'Branches';

export type deleteArtifactDialogData = {
	artifact: artifactWithRelations;
	operationType: operationType;
};

// Publishing
export type publishMarkdownDialogData = {
	templateId: string;
	operationType: operationType;
	extension: publishingExtension;
};

export type publishingExtension = 'html' | 'md';

export type publishingOutputType = {
	label: string;
	extension: publishingExtension;
};

export const publishingOutputTypesMap: publishingOutputType[] = [
	{ label: 'HTML', extension: 'html' },
	{ label: 'Markdown', extension: 'md' },
];

export type publishingTemplateKeyGroups = {
	publishingTemplateKeyGroupList: publishingTemplateKey[];
};

export type publishingTemplateKey = {
	identifier: key;
	matchCriteria: {
		key: key[];
	};
	name: key;
	safeName: key;
};

export type key = {
	key: string;
	keyType: string;
};

export type publishMarkdownAsHtmlRequestData = {
	publishMarkdownAsHtmlRequestData: publishingRequestData;
};

export type msWordPreviewRequestData = {
	msWordPreviewRequestData: publishingRequestData;
};

export type publishingRequestFormData = {
	publishingRequestData: publishingRequestData;
};

export type publishingRequestData = {
	artifactIds: string[];
	publishingRendererOptions: publishingRendererOptions;
	publishingTemplateRequest: publishingTemplateRequest;
};

export type publishingRendererOptions = {
	Branch: publishingRendererOptionsBranch;
	PublishingFormat: publishingRendererOptionsPublishingFormat;
};

export type publishingRendererOptionsBranch = {
	id: string;
	viewId: string;
};

export type publishingRendererOptionsPublishingFormat = {
	formatIndicator: string;
};

export type publishingTemplateRequest = {
	byOptions: boolean;
	formatIndicator: string;
	templateId: string;
};
