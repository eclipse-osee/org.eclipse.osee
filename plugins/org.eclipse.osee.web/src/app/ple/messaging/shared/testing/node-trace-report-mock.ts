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
import type { NodeTraceReportItem } from '../types/NodeTraceReport';

export const NodeTraceReportMock: NodeTraceReportItem[] = [
	{
		id: '1',
		name: 'Requirement 1',
		artifactType: 'Artifact Type 1',
		relatedItems: [
			{
				id: '100',
				name: 'Related Artifact 1',
				artifactType: 'Related Artifact Type 1',
				relatedItems: [],
			},
		],
	},
	{
		id: '2',
		name: 'Requirement 2',
		artifactType: 'Artifact Type 2',
		relatedItems: [
			{
				id: '200',
				name: 'Related Artifact 2',
				artifactType: 'Related Artifact Type 2',
				relatedItems: [],
			},
			{
				id: '300',
				name: 'Related Artifact 3',
				artifactType: 'Related Artifact Type 3',
				relatedItems: [],
			},
		],
	},
];
