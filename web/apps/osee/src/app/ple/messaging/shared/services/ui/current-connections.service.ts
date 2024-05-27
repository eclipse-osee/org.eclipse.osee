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
import { Injectable, inject } from '@angular/core';
import { combineLatest, switchMap, take, filter } from 'rxjs';
import { ConnectionService } from '../http/connection.service';
import { ConnectionsUiService } from './connections-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CurrentConnectionsService {
	private connectionsService = inject(ConnectionService);
	private uiService = inject(ConnectionsUiService);

	getFilteredPaginatedConnections(
		pageNum: string | number,
		filterParameter?: string
	) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
			this.uiService.currentPageSize,
		]).pipe(
			take(1),
			filter(
				([branchId, _viewId, _pageSize]) =>
					branchId !== '' && branchId !== '-1'
			),
			switchMap(([id, viewId, pageSize]) =>
				this.connectionsService.getFiltered(
					id,
					filterParameter,
					viewId,
					pageNum,
					pageSize
				)
			)
		);
	}

	getFilteredCount(filterParameter?: string) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
		]).pipe(
			take(1),
			filter(
				([branchId, _viewId]) => branchId !== '' && branchId !== '-1'
			),
			switchMap(([id, viewId]) =>
				this.connectionsService.getCount(id, filterParameter, viewId)
			)
		);
	}

	get currentPageSize() {
		return this.uiService.currentPageSize;
	}
}
