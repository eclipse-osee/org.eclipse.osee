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
import type { NodeTraceReportItem } from '@osee/messaging/shared/types';
import type { headerDetail } from '@osee/shared/types';

export const nodeTraceReportHeaderDetails: headerDetail<NodeTraceReportItem>[] =
	[
		{
			header: 'name',
			description: 'Requirement Name',
			humanReadable: 'Name',
		},
		{
			header: 'artifactType',
			description: 'Requirement Artifact Type',
			humanReadable: 'Artifact Type',
		},
		{
			header: 'relatedItems',
			description: 'Artifacts related to the requirement',
			humanReadable: 'Traces To',
		},
	];
