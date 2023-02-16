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
import { ImportEnumSet } from '../types';

export const importEnumSetHeaderDetails: headerDetail<ImportEnumSet>[] = [
	{
		header: 'name',
		description: 'Enum Set name',
		humanReadable: 'Name',
	},
	{
		header: 'enums',
		description: 'Enumerated Literals',
		humanReadable: 'Enums',
	},
	{
		header: 'applicability',
		description: 'Enum Set applicability',
		humanReadable: 'Applicability',
	},
];
