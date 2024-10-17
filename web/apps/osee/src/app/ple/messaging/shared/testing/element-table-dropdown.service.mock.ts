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

import { element, PlatformType, structure } from '@osee/messaging/shared/types';
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
		openEnumDialog(platformType: PlatformType, editMode: boolean) {},
		openDescriptionDialog(element: element) {},
		openEnumLiteralDialog(element: element) {},
		openNotesDialog(element: element) {},
		viewDiff<T>(value: difference<T> | undefined, header: string) {},
		hasChanges(v: element): v is Required<element> {
			return (
				(v as any).changes !== undefined ||
				(v as any).added !== undefined ||
				(v as any).deleted !== undefined
			);
		},
	};
