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
import { CiDashboardImportHttpService } from './ci-dashboard-import-http.service';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { of } from 'rxjs';

export const ciDashboardImportHttpServiceMock: Partial<CiDashboardImportHttpService> =
	{
		importBatch(branchId, ciSetId, formData) {
			return of(transactionResultMock);
		},
		importFile(branchId, ciSetId, file) {
			return of(transactionResultMock);
		},
		importSingleFile(branchId, ciSetId, formData) {
			return of(transactionResultMock);
		},
	};
