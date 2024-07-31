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
import { transactionResult } from '@osee/shared/types/change-report';

export type TmoImportResult = {
	txResult: transactionResult;
	workflows: string[];
};

export const tmoImportResultSentinel: TmoImportResult = {
	txResult: {
		results: {
			empty: false,
			errorCount: 0,
			errors: false,
			failed: false,
			ids: [],
			infoCount: 0,
			numErrors: 0,
			numErrorsViaSearch: 0,
			numWarnings: 0,
			numWarningsViaSearch: 0,
			results: [],
			success: false,
			tables: [],
			title: '',
			txId: '0',
			warningCount: 0,
		},
		tx: { id: '0', branchId: '-1' },
	},
	workflows: [],
};
