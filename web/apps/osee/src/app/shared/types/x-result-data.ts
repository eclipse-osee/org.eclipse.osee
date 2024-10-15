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
import { transactionToken } from '@osee/transactions/types';
export type XResultData = {
	empty: boolean;
	errorCount: number;
	errors: boolean;
	failed: boolean;
	ids: string[];
	infoCount: number;
	numErrors: number;
	numErrorsViaSearch: number;
	numWarnings: number;
	numWarningsViaSearch: number;
	results: string[];
	success: boolean;
	tables: [];
	title: string | null;
	txId: string;
	warningCount: number;
};

export type commitResponse = {
	tx: transactionToken;
	results: XResultData;
	success: boolean;
	failed: boolean;
};

export type transitionResponse = {
	cancelled: boolean;
	workItemIds: [];
	results: string[];
	transitionWorkItems: [];
	transaction: transactionToken;
	empty: true;
};
