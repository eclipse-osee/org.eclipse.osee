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
import { CiDashboardImportHttpService } from '../services/ci-dashboard-import-http.service';
import { of } from 'rxjs';
import { tmoImportResultMock } from './tmo.response.mock';

export const ciDashboardImportHttpServiceMock: Partial<CiDashboardImportHttpService> =
	{
		importBatch(branchId, ciSetId, formData) {
			return of(tmoImportResultMock);
		},
		importFile(branchId, ciSetId, file) {
			return of(tmoImportResultMock);
		},
		importSingleFile(branchId, ciSetId, formData) {
			return of(tmoImportResultMock);
		},
	};
