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
import { map, shareReplay, tap } from 'rxjs';
import { GetUserContextRelations } from '../fetch-data-services/get-user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class UserContextRelationsService {
	contextData$ = this.getUserContextRelations.getResponseUserContextData();

	constructor(private getUserContextRelations: GetUserContextRelations) {}

	private _commands = this.contextData$.pipe(
		map((contexts) => contexts.map((context) => context.commands).flat()),
		shareReplay()
	);

	public get commands() {
		return this._commands;
	}
}
