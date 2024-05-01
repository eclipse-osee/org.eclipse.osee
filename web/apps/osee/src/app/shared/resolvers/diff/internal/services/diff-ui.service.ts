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
import { iif, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { DiffReportBranchService } from './diff-report-branch.service';

@Injectable({
	providedIn: 'root',
})
export class DiffUIService {
	private _diff = this.uiService.isInDiff.pipe(
		take(1),
		switchMap((mode) =>
			iif(() => mode, this.diffService.differences, of(undefined))
		)
	);
	constructor(
		private uiService: UiService,
		private diffService: DiffReportBranchService
	) {}

	get diff() {
		return this._diff;
	}

	get id() {
		return this.uiService.id.getValue();
	}

	get type() {
		return this.uiService.type.getValue();
	}

	set DiffMode(value: boolean) {
		this.uiService.diffMode = value;
	}

	set branchId(value: string) {
		this.uiService.idValue = value;
	}

	set branchType(value: 'working' | 'baseline' | '') {
		this.uiService.typeValue = value;
	}
}
