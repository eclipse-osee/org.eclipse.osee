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
import { combineLatest, switchMap, take } from 'rxjs';
import { ConnectionService } from 'src/app/ple/messaging/shared/services/public-api';
import { ConnectionsUiService } from 'src/app/ple/messaging/shared/services/ui/connections-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentConnectionsService {
	constructor(
		private connectionsService: ConnectionService,
		private uiService: ConnectionsUiService
	) {}

	getFilteredPaginatedConnections(pageNum: string | number, filter?: string) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
			this.uiService.currentPageSize,
		]).pipe(
			take(1),
			switchMap(([id, viewId, pageSize]) =>
				this.connectionsService.getFiltered(
					id,
					filter,
					viewId,
					pageNum,
					pageSize
				)
			)
		);
	}

	getFilteredCount(filter?: string) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
		]).pipe(
			take(1),
			switchMap(([id, viewId]) =>
				this.connectionsService.getCount(id, filter, viewId)
			)
		);
	}

	get currentPageSize() {
		return this.uiService.currentPageSize;
	}
}
