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
import { BehaviorSubject, Subject } from 'rxjs';
import { UiService } from '../../../../../ple-services/ui/ui.service';

@Injectable({
	providedIn: 'root',
})
export class PlMessagingTypesUIService {
	private _filter: BehaviorSubject<string> = new BehaviorSubject<string>('');
	private _singleLineAdjustment: BehaviorSubject<number> =
		new BehaviorSubject<number>(0);

	private _columnCount: BehaviorSubject<number> = new BehaviorSubject<number>(
		0
	);
	constructor(private ui: UiService) {}

	get filter() {
		return this._filter;
	}

	set filterString(filter: string) {
		if (filter !== this._filter.getValue()) {
			this._filter.next(filter);
		}
	}

	get singleLineAdjustment() {
		return this._singleLineAdjustment;
	}

	set singleLineAdjustmentNumber(value: number) {
		if (value !== this._singleLineAdjustment.getValue()) {
			this._singleLineAdjustment.next(value);
		}
	}

	get columnCount() {
		return this._columnCount;
	}

	set columnCountNumber(value: number) {
		if (value !== this._columnCount.getValue()) {
			this._columnCount.next(value);
		}
	}

	get typeUpdateRequired() {
		return this.ui.update;
	}

	set updateTypes(value: boolean) {
		this.ui.updated = value;
	}

	get BranchId() {
		return this.ui.id;
	}

	set BranchIdString(value: string) {
		this.ui.idValue = value;
	}

	set branchType(value: string) {
		this.ui.typeValue = value;
	}
}
