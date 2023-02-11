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
import { take, switchMap, shareReplay } from 'rxjs';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { DifferenceReportBranchInfoService } from './difference-report-branch-info.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentDiffReportService {
	constructor(
		private differenceService: DifferenceReportBranchInfoService,
		private uiService: UiService
	) {}

	get differenceReport() {
		return this.uiService.id.pipe(
			take(1),
			switchMap((id) =>
				this.differenceService
					.differenceReport(id)
					.pipe(shareReplay({ bufferSize: 5, refCount: true }))
			)
		);
	}
}
