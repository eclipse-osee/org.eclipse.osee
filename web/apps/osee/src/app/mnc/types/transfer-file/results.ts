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
export type results = {
	title: string;
	results: string[];
	ids: string[];
	errorCount: number;
	warningCount: number;
	infoCount: number;
	tables: xResultTable[];
	txId: number;
	empty: boolean;
	numErrorsViaSearch: number;
	numWarningsViaSearch: number;
	success: boolean;
	numErrors: number;
	warnings: boolean;
	numWarnings: number;
	errors: boolean;
	failed: boolean;
	ok: boolean;
};

export type xResultTable = {
	rows: row[];
	columns: column[];
	name: string;
};

export type row = {
	values: string[];
};

export type column = {
	id: string;
	name: string;
	width: number;
	type: string;
};
