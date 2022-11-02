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
import { changeReportMock } from '../../http/change-report.mock';
import { differenceReportMock } from '../../http/difference-report.mock';
import { DifferenceBranchInfoService } from './difference-branch-info.service';

export const DifferenceBranchInfoServiceMock: Partial<DifferenceBranchInfoService> =
	{
		differences(branchId) {
			return of(changeReportMock);
		},

		differenceReport(branchId) {
			return of(differenceReportMock);
		},
	};
