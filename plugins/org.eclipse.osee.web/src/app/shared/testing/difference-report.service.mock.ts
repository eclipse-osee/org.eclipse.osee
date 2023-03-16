/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { changeReportMock } from './change-report.mock';
import { DifferenceReportService } from '@osee/shared/services';

export const DifferenceReportServiceMock: Partial<DifferenceReportService> = {
	getDifferences(fromBranchId, toBranchId) {
		return of(changeReportMock);
	},
};
