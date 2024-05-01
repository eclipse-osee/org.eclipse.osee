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
import { structure } from '../types';

export const structureHeaderDetails: headerDetail<structure>[] = [
	{
		header: 'name',
		description: 'Name of structure',
		humanReadable: 'Name',
	},
	{
		header: 'nameAbbrev',
		description: 'Abbreviated name of structure',
		humanReadable: 'Name Abbreviation',
	},
	{
		header: 'description',
		description: 'Description of structure',
		humanReadable: 'Description',
	},
	{
		header: 'interfaceMinSimultaneity',
		description: 'Minimum simultaneity of structure',
		humanReadable: 'Min Simult.',
	},
	{
		header: 'interfaceMaxSimultaneity',
		description: 'Maximum simultaneity of structure',
		humanReadable: 'Max Simult.',
	},
	{
		header: 'interfaceTaskFileType',
		description: 'Task file type of structure',
		humanReadable: 'Task File Type',
	},
	{
		header: 'interfaceStructureCategory',
		description: 'Category of structure',
		humanReadable: 'Category',
	},
	{
		header: 'applicability',
		description: 'Applicability of the structure',
		humanReadable: 'Applicability',
	},
];
