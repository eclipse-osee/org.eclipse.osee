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
import { Injectable, inject } from '@angular/core';
import { filter, switchMap } from 'rxjs/operators';
import { UiService } from '@osee/shared/services';
import { StructureNamesService } from '../http/structure-names.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentStructureNamesService {
	private ui = inject(UiService);
	private structureService = inject(StructureNamesService);

	getStructureNames(connectionId: string) {
		return this.ui.id.pipe(
			filter(
				(id) =>
					id !== '' && id !== undefined && id !== '-1' && id !== '0'
			),
			switchMap((id) =>
				this.structureService.getStructureNames(id, connectionId)
			)
		);
	}
}
