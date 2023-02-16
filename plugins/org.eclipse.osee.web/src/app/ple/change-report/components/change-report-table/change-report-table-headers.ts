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
import { changeReportRow } from '@osee/shared/types/change-report';
import { headerDetail } from '@osee/shared/types';

export const changeReportHeaders: headerDetail<changeReportRow>[] = [
	{
		header: 'ids',
		description: 'ID(s) of changed artifact(s)',
		humanReadable: 'Id(s)',
	},
	{
		header: 'names',
		description: 'Name of changed artifact(s)',
		humanReadable: 'Artifact Name(s)',
	},
	{
		header: 'itemType',
		description: 'Type of changed item',
		humanReadable: 'Item Type',
	},
	{
		header: 'itemKind',
		description: 'Kind of changed item',
		humanReadable: 'Item Kind',
	},
	{
		header: 'changeType',
		description: 'Change Type',
		humanReadable: 'Change Type',
	},
	{
		header: 'isValue',
		description: 'Current value',
		humanReadable: 'Is Value',
	},
	{
		header: 'wasValue',
		description: 'Previous value',
		humanReadable: 'Was Value',
	},
];
