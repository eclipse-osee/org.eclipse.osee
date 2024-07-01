/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { operationType } from '@osee/artifact-with-relations/types';
import { artifactTypeIconMock } from '@osee/artifact-with-relations/testing';
import {
	publishingTemplateKeyGroups,
	publishMarkdownDialogData,
} from '../types/artifact-explorer';

export const operationTypeMock: operationType = {
	id: '1',
	name: 'create',
	description: 'create something',
	materialIcon: artifactTypeIconMock,
};

export const publishMarkdownDialogDataMock: publishMarkdownDialogData = {
	templateId: '',
	operationType: operationTypeMock,
};

export const publishingTemplateKeyGroupsMock: publishingTemplateKeyGroups = {
	publishingTemplateKeyGroupList: [
		{
			identifier: {
				key: 'AT-111111',
				keyType: 'IDENTIFIER',
			},
			matchCriteria: {
				key: [
					{
						key: 'org.eclipse.osee.framework.ui.skynet.render.MarkdownRenderer PREVIEW PREVIEW_ALL_RECURSE',
						keyType: 'MATCH_CRITERIA',
					},
				],
			},
			name: {
				key: 'PREVIEW_ALL_RECURSE',
				keyType: 'NAME',
			},
			safeName: {
				key: 'PREVIEW_ALL_RECURSE',
				keyType: 'SAFE_NAME',
			},
		},
	],
};
