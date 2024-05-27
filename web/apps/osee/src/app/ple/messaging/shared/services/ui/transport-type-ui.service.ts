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
import { shareReplay, switchMap, take } from 'rxjs/operators';
import { combineLatest } from 'rxjs';
import {
	MimRouteService,
	TransportTypeService,
} from '@osee/messaging/shared/services';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class TransportTypeUiService {
	private transportTypeService = inject(TransportTypeService);
	private mimRoute = inject(MimRouteService);

	private _connectionId = toObservable(this.mimRoute.connectionId);
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
