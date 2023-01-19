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
import { NameValuePair } from 'src/app/ple/plconfig/types/base-types/NameValuePair';
import { ResponseColumnSchema } from './table-data-types';

export interface userHistory extends NameValuePair {
	executedCommandHistory: string;
	commandHistoryId: string;
	columns: ResponseColumnSchema[];
	data: string[][];
}

export interface executedCommandHistory {
	Command: string;
	Parameters: any;
	Favorite: string;
	Validated: string;
	name: string;
	id?: string;
}
