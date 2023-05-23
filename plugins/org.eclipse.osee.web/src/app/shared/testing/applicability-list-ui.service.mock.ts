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
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applic } from '@osee/shared/types/applicability';
import { of } from 'rxjs';

export const applicsMock: applic[] = [
	{ id: '1', name: 'Base' },
	{ id: '2', name: 'Second' },
];

export const viewsMock: applic[] = [
	{ id: '10', name: 'Product A' },
	{ id: '11', name: 'Product B' },
];

export const applicabilityListUIServiceMock: Partial<ApplicabilityListUIService> =
	{
		applic: of(applicsMock),
		views: of(viewsMock),
		/**
		 * @todo make these mocks smarter
		 */
		getApplicabilities(
			pageNum: string | number,
			count: number,
			filter?: string
		) {
			return of(applicsMock);
		},
		getApplicabilityCount(filter?: string) {
			return of(applicsMock.length);
		},
	};
