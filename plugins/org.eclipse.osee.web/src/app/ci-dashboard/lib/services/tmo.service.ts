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
import { BehaviorSubject } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { TmoHttpService } from './tmo-http.service';

@Injectable({
	providedIn: 'root',
})
export class TmoService {
	constructor(
		private uiService: UiService,
		private tmoHttpService: TmoHttpService
	) {}

	private _programId = new BehaviorSubject<string>('-1');

	private _filterValue = new BehaviorSubject<string>('');

	programs = this.tmoHttpService.getProgramList();
	scriptDefs = this.tmoHttpService.getScriptDefList();
	scriptResults = this.tmoHttpService.getScriptResultList();
	testCases = this.tmoHttpService.getTestCaseList();
	testPoints = this.tmoHttpService.getTestPointList();

	get programId() {
		return this._programId;
	}

	set ProgramId(id: string) {
		this._programId.next(id);
	}

	get filterValue() {
		return this._filterValue;
	}

	set FilterValue(value: string) {
		this._filterValue.next(value);
	}
}
