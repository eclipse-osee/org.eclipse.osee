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
import { differenceReportMock } from './difference-report.mock';
import { DifferenceReportService } from './difference-report.service';

export const DifferenceReportServiceMock: Partial<DifferenceReportService> = {
	getDifferences(fromBranchId, toBranchId) {
		return of(changeReportMock);
	},

	getDifferenceReport(fromBranchId, toBranchId) {
		return of(differenceReportMock);
	},
};
