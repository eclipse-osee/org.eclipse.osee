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
import { combineLatest } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SharedConnectionService } from '../http/shared-connection.service';
import { MimRouteService } from './mim-route.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class SharedConnectionUIService {
	private _mimRoute = inject(MimRouteService);
	private _connectionService = inject(SharedConnectionService);

	private _connectionId = toObservable(this._mimRoute.connectionId);
	public readonly connection = combineLatest([
		this._mimRoute.id,
		this._connectionId,
	]).pipe(
		switchMap(([id, connection]) =>
			this._connectionService.getConnection(id, connection)
		)
	);

	get viewId() {
		return this._mimRoute.viewId;
	}

	get branchId() {
		return this._mimRoute.id;
	}

	get branchType() {
		return this._mimRoute.type;
	}
}
