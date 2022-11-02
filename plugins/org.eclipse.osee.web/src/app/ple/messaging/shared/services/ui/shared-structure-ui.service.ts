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
import { StructuresService } from '../http/structures.service';
import { MimRouteService } from './mim-route.service';

@Injectable({
	providedIn: 'root',
})
export class SharedStructureUIService {
	public readonly structure = combineLatest([
		this._mimRoute.id,
		this._mimRoute.connectionId,
		this._mimRoute.messageId,
		this._mimRoute.submessageId,
		this._mimRoute.singleStructureId,
	]).pipe(
		switchMap(([branch, connection, message, submessage, structure]) =>
			this.structureService.getStructure(
				branch,
				message,
				submessage,
				structure,
				connection
			)
		)
	);
	constructor(
		private _mimRoute: MimRouteService,
		private structureService: StructuresService
	) {}
}
