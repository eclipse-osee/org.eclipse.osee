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
import { of } from 'rxjs';
import {
	importOptionsMock,
	importSummaryMock,
} from '../testing/import.response.mock';
import { ImportHttpService } from './import-http.service';
import { transactionResultMock } from '@osee/transactions/testing';
import type { ImportSummary } from '@osee/messaging/shared/types';

export const importHttpServiceMock: Partial<ImportHttpService> = {
	performImport(branchId: string, summary: ImportSummary) {
		return of(transactionResultMock);
	},

	getImportSummary(
		url: string,
		transportTypeId: `${number}`,
		fileName: string,
		formData: FormData | File
	) {
		return of(importSummaryMock);
	},

	getImportOptions() {
		return of(importOptionsMock);
	},
};
