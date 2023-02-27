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
import { CrossReference } from '@osee/messaging/shared';

export const crossReferencesMock: CrossReference[] = [
	{
		id: '1',
		name: 'CR1',
		crossReferenceValue: 'CR Val 1',
		crossReferenceArrayValues: '0=ABC;1=DEF',
	},
	{
		id: '2',
		name: 'CR2',
		crossReferenceValue: 'CR Val 2',
		crossReferenceArrayValues: '0=GHI;1=JKL',
	},
];
