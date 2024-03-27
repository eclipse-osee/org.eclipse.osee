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
import { DiffReportHttpService } from '@osee/messaging/shared/services';
import { differenceReportMock } from './difference-report.mock';
import { mimChangeSummaryMock } from './mim-change-summary.mock';
import { of } from 'rxjs';

export const diffReportHttpServiceMock: Partial<DiffReportHttpService> = {
	getDifferenceReport(
		sourceBranch: string | number,
		destBranch: string | number
	) {
		return of(differenceReportMock);
	},

	getDifferenceReport2(
		sourceBranch: string | number,
		destBranch: string | number
	) {
		return of(mimChangeSummaryMock);
	},
};
