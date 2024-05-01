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
	element,
	elementWithChanges,
	structure,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { ElementTableDropdownService } from '@osee/messaging/structure-tables/services';

export const elementTableDropdownServiceMock: Partial<ElementTableDropdownService> =
	{
		openAddElementDialog(
			parent: structure | element,
			isArray: boolean,
			allowArray: boolean,
			afterElement?: string,
			copyElement?: element
		) {},
		openDeleteElementDialog(
			element: element,
			removeType: 'Structure' | 'Array'
		) {},
		openEditElementDialog(element: element) {},
		openEnumDialog(id: string, editMode: boolean) {},
		openDescriptionDialog(description: string, elementId: string) {},
		openEnumLiteralDialog(enumLiteral: string, elementId: string) {},
		openNotesDialog(notes: string, elementId: string) {},
		viewDiff<T>(value: difference<T> | undefined, header: string) {},
		hasChanges(v: element | elementWithChanges): v is elementWithChanges {
			return (
				(v as any).changes !== undefined ||
				(v as any).added !== undefined ||
				(v as any).deleted !== undefined
			);
		},
	};
