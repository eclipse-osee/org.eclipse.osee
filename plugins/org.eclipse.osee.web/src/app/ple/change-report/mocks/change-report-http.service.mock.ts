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
import { ChangeReportHttpService } from '../services/change-report-http.service';
import { changeReportMock } from './changeReportMock';

export const changeReportHttpServiceMock: Partial<ChangeReportHttpService> = {
	getBranchChangeReport(branch1Id: string, branch2Id: string) {
		return of(changeReportMock);
	},
	getTxChangeReport(branchId: string, tx1: string, tx2: string) {
		return of(changeReportMock);
	},
};
