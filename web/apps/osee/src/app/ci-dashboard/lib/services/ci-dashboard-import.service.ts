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
import { Subject, combineLatest, filter, of, switchMap } from 'rxjs';
import { CiDashboardImportHttpService } from './ci-dashboard-import-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CiDashboardImportService {
	constructor(
		private uiService: CiDashboardUiService,
		private importHttpService: CiDashboardImportHttpService
	) {}

	importFile(file: File | undefined) {
		if (file === undefined || file.name === '') {
			return of();
		}
		return combineLatest([
			this.uiService.branchId,
			this.uiService.ciSetId,
		]).pipe(
			filter(
				([branchId, ciSetId]) =>
					branchId !== '' &&
					branchId !== '-1' &&
					ciSetId !== '' &&
					ciSetId !== '-1'
			),
			switchMap(([branchId, ciSetId]) =>
				this.importHttpService.importFile(branchId, ciSetId, file)
			)
		);
	}
}
