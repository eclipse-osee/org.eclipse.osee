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
import { headerDetail } from '@osee/shared/types';
import { CrossReference } from '../types';

export const crossReferenceHeaderDetails: headerDetail<CrossReference>[] = [
	{
		header: 'name',
		description: 'Name to be used in cross reference lookup',
		humanReadable: 'Name',
	},
	{
		header: 'crossReferenceValue',
		description: 'Value returned by cross reference lookup',
		humanReadable: 'Value',
	},
	{
		header: 'crossReferenceArrayValues',
		description:
			'Key-value pairs returned by cross reference lookup (if cross reference represents an array)',
		humanReadable: 'Array Values',
	},
	{
		header: 'crossReferenceAdditionalContent',
		description: 'Additional content related to the cross reference',
		humanReadable: 'Additional Content',
	},
	{
		header: 'applicability',
		description: 'Applicability of Cross Reference',
		humanReadable: 'Applicability',
	},
];
