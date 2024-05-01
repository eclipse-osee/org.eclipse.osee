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
import { Injectable } from '@angular/core';
import { switchMap, take } from 'rxjs/operators';
import {
	CurrentBranchInfoService,
	DifferenceReportService,
} from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class DifferenceBranchInfoService {
	constructor(
		private diffService: DifferenceReportService,
		private branchInfoService: CurrentBranchInfoService
	) {}

	differences(branchId: string | number) {
		return this.branchInfoService.parentBranch.pipe(
			take(1),
			switchMap((parentBranch) =>
				this.diffService.getDifferences(parentBranch, branchId)
			)
		);
	}
}
