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

import { of } from 'rxjs';
import { StructureCategoriesService } from '@osee/messaging/structure-category/services';

export const structureCategoriesServiceMock: Partial<StructureCategoriesService> =
	{
		getFiltered: () => of([]),
		getOne: () =>
			of({
				id: '-1',
				name: 'test',
			}),
		getCount: () => of(0),
	};
