/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { NamedId } from '@osee/shared/types';
import { ResponseColumnSchema } from './table-data-types';

export type userHistory = {
	executedCommandHistory: string;
	commandHistoryId: string;
	columns: ResponseColumnSchema[];
	data: string[][];
} & NamedId;

export type executedCommandHistory = {
	Command: string;
	Parameters: unknown;
	Favorite: string;
	Validated: string;
	name: string;
	id?: string;
};
