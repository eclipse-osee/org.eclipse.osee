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
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class RouterStateService {
	constructor(private uiService: UiService) {}

	get BranchId() {
		return this.uiService.id;
	}
	get id() {
		return this.BranchId.getValue();
	}
	set id(value: string) {
		this.uiService.idValue = value;
	}

	get BranchType() {
		return this.uiService.type;
	}

	get type() {
		return this.uiService.type.getValue();
	}

	set type(value: 'working' | 'baseline' | '') {
		this.uiService.typeValue = value;
	}
}
