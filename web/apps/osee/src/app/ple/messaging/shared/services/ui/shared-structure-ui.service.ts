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
import { StructuresService } from '../http/structures.service';
import { MimRouteService } from './mim-route.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class SharedStructureUIService {
	private _mimRoute = inject(MimRouteService);
	private structureService = inject(StructuresService);

	private _subMessageId = toObservable(this._mimRoute.submessageId);

	private _connectionId = toObservable(this._mimRoute.connectionId);
	public readonly structure = combineLatest([
		this._mimRoute.id,
		this._connectionId,
		this._mimRoute.messageId,
		this._subMessageId,
		this._mimRoute.singleStructureId,
		this._mimRoute.viewId,
	]).pipe(
		switchMap(
			([branch, connection, message, submessage, structure, viewId]) =>
				this.structureService.getStructure(
					branch,
					message,
					submessage,
					structure,
					connection,
					viewId
				)
		)
	);
}
