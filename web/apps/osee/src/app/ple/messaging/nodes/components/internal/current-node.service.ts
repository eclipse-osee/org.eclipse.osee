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
import { NodeService } from '@osee/messaging/shared/services';
import { UiService } from '@osee/shared/services';
import { take, switchMap } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CurrentNodeService {
	private ui = inject(UiService);
	private nodeService = inject(NodeService);

	getPaginatedNodesByName(
		name: string,
		pageNum: string | number,
		pageSize: number,
		connectionId: `${number}`
	) {
		return this.ui.id.pipe(
			take(1),
			switchMap((id) =>
				this.nodeService.getPaginatedNodesByName(
					id,
					name,
					pageNum,
					pageSize,
					connectionId
				)
			)
		);
	}

	getNodesByNameCount(name: string) {
		return this.ui.id.pipe(
			take(1),
			switchMap((id) => this.nodeService.getNodesByNameCount(id, name))
		);
	}
}
