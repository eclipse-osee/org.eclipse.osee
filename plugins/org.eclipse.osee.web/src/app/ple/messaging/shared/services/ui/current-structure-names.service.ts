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
import { Injectable } from '@angular/core';
import { filter, switchMap } from 'rxjs/operators';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { StructureNamesService } from '../http/structure-names.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureNamesService {
	private _names = this.ui.id.pipe(
		filter(
			(id) => id !== '' && id !== undefined && id !== '-1' && id !== '0'
		),
		switchMap((id) => this.structureService.getStructureNames(id))
	);
	constructor(
		private ui: UiService,
		private structureService: StructureNamesService
	) {}
	get names() {
		return this._names;
	}
}
