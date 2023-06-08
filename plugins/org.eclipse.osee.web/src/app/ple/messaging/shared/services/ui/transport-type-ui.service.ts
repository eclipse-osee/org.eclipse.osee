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
import { shareReplay, switchMap, take } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import {
	MimRouteService,
	TransportTypeService,
} from '@osee/messaging/shared/services';

@Injectable({
	providedIn: 'root',
})
export class TransportTypeUiService {
	constructor(
		private transportTypeService: TransportTypeService,
		private mimRoute: MimRouteService
	) {}

	private _connectionId = this.mimRoute.connectionId;
	private _branchId = this.mimRoute.id;

	private _currentTransportType = combineLatest([
		this._branchId,
		this._connectionId,
	]).pipe(
		take(1),
		switchMap(([branchId, connectionId]) =>
			this.transportTypeService.getFromConnection(branchId, connectionId)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get currentTransportType() {
		return this._currentTransportType;
	}
}
