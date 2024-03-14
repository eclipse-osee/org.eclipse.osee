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
import { mergeData } from '@osee/shared/types';

export const mergeDataMock: mergeData[] = [
	{
		artId: '999',
		artType: '56789',
		name: 'data 1',
		conflictType: 'ATTRIBUTE',
		conflictStatus: 'UNTOUCHED',
		conflictId: 1,
		attrMergeData: {
			attrType: '123456',
			attrId: '5423',
			attrTypeName: 'Art Type 1',
			sourceValue: 'Src Val',
			mergeValue: 'Merge Val',
			destValue: 'Dest Val',
			sourceUri: '',
			mergeUri: '',
			destUri: '',
			sourceGammaId: '5678',
			destGammaId: '8765',
			storeType: 'Integer',
		},
	},
];
