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
import { Injectable } from '@angular/core';
import { DifferenceReportService } from '../http/difference-report.service';
import { take, switchMap } from 'rxjs';
import { CurrentBranchInfoService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class DifferenceReportBranchInfoService {
	constructor(
		private branchInfoService: CurrentBranchInfoService,
		private reportService: DifferenceReportService
	) {}

	differenceReport(branchId: string | number) {
		return this.branchInfoService.parentBranch.pipe(
			take(1),
			switchMap((parentBranch) =>
				this.reportService.getDifferenceReport(parentBranch, branchId)
			)
		);
	}
}
