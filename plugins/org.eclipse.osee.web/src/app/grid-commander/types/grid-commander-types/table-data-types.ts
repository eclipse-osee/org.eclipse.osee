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
//used to create RowObj dynamically (may have more/less columns fetched from API)
export interface RowObj {
	[key: string]: string | boolean | number;
}

export interface ResponseColumnSchema {
	name: string;
	type?: string;
	filters?: [];
	sort?: boolean;
}

export interface ResponseTableData {
	tableOptions: {
		columns: ResponseColumnSchema[];
	};
	data: string[][];
}
