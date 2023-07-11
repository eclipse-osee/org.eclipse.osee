import { transactionToken } from '@osee/shared/types';

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
export interface response {
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
	results: Array<string>;
	success: boolean;
	tables: [];
	title: string | null;
	txId: string;
	warningCount: number;
}
export interface commitResponse {
	tx: transactionToken;
	results: response;
	success: boolean;
	failed: boolean;
}
export interface transitionResponse {
	cancelled: boolean;
	workItemIds: [];
	results: Array<string>;
	transitionWorkItems: [];
	transaction: transactionToken;
	empty: true;
}
