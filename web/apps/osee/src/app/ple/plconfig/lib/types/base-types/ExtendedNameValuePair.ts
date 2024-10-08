/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { difference } from '@osee/shared/types/change-report';
export type ExtendedNameValuePair = {
	id: string;
	name: string;
	value: string;
	values: string[];
};

export type ExtendedNameValuePairWithChanges = {
	changes: {
		value: difference;
	};
} & ExtendedNameValuePair;
