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
import { combineLatest } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SharedConnectionService } from '../http/shared-connection.service';
import { MimRouteService } from './mim-route.service';

@Injectable({
	providedIn: 'root',
})
export class SharedConnectionUIService {
	public readonly connection = combineLatest([
		this._mimRoute.id,
		this._mimRoute.connectionId,
	]).pipe(
		switchMap(([id, connection]) =>
			this._connectionService.getConnection(id, connection)
		)
	);
	constructor(
		private _mimRoute: MimRouteService,
		private _connectionService: SharedConnectionService
	) {}
}
