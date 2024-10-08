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
import { applic } from '@osee/applicability/types';
import type { connection } from './connection';
export type CrossReference = {
	id?: string;
	name: string;
	crossReferenceValue: string;
	crossReferenceArrayValues: string;
	crossReferenceAdditionalContent: string;
	connections?: connection[];
	applicability: applic;
};

export type CrossRefKeyValue = {
	key: string;
	value: string;
};
