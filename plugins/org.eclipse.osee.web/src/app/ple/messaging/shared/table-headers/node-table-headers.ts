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
import { nodeData } from '../types';

export const nodeHeaderDetails: headerDetail<nodeData>[] = [
	{
		header: 'name',
		description: 'Name of the node',
		humanReadable: 'Name',
	},
	{
		header: 'interfaceNodeNameAbbrev',
		description: 'Name abbreviation',
		humanReadable: 'Name Abbrev.',
	},
	{
		header: 'interfaceNodeCodeGenName',
		description: 'Name used for code gen',
		humanReadable: 'Code Gen Name',
	},
	{
		header: 'interfaceNodeType',
		description: 'Node Type',
		humanReadable: 'Node Type',
	},
	{
		header: 'interfaceNodeNumber',
		description: 'Node number',
		humanReadable: 'Node Number',
	},
	{
		header: 'interfaceNodeGroupId',
		description: 'Node Group ID',
		humanReadable: 'Group ID',
	},
	{
		header: 'interfaceNodeAddress',
		description: 'Node Address',
		humanReadable: 'Address',
	},
	{
		header: 'description',
		description: 'Description of the node',
		humanReadable: 'Description',
	},
	{
		header: 'interfaceNodeToolUse',
		description: 'Node used in code gen tool',
		humanReadable: 'Used in Tool',
	},
	{
		header: 'interfaceNodeCodeGen',
		description: 'Generate code for this node',
		humanReadable: 'Code Gen',
	},
	{
		header: 'interfaceNodeBuildCodeGen',
		description: 'Generate code for this node on build',
		humanReadable: 'Build Code Gen',
	},
	{
		header: 'notes',
		description: 'Node notes',
		humanReadable: 'Notes',
	},
	{
		header: 'applicability',
		description: 'Applicability of the node',
		humanReadable: 'Applicability',
	},
];
