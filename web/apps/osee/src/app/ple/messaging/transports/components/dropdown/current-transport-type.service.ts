/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { UiService } from '@osee/shared/services';
import { TransportTypeService } from '@osee/messaging/shared/services';
import { take, switchMap } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CurrentTransportTypeService {
	private ui = inject(UiService);
	private transportTypeService = inject(TransportTypeService);

	//TODO update this endpoint to support a filter later.
	getPaginatedTypes(pageNum: string | number, pageSize: string | number) {
		return this.ui.id.pipe(
			take(1),
			switchMap((id) =>
				this.transportTypeService.getPaginated(id, pageNum, pageSize)
			)
		);
	}
}
