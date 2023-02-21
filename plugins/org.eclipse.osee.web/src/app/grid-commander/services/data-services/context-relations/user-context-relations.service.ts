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
import {
	concatMap,
	distinct,
	map,
	of,
	repeatWhen,
	scan,
	shareReplay,
	switchMap,
} from 'rxjs';
import { UiService } from '../../../../ple-services/ui/ui.service';
import {
	Command,
	UsersContext,
} from '../../../types/grid-commander-types/gc-user-and-contexts-relationships';
import { GetUserContextRelationsService } from '../../fetch-data-services/user-context-relations/get-user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class UserContextRelationsService {
	contextData$ = this.getUserContextRelationsService
		.getResponseUserContextData()
		.pipe(
			switchMap((userContexts) => this.setContextPriority(userContexts)),
			repeatWhen(() => this.uiService.update)
		);

	constructor(
		private getUserContextRelationsService: GetUserContextRelationsService,
		private uiService: UiService
	) {}

	private _contexts = this.contextData$.pipe(
		map((contexts) => contexts.map((context) => [context.name, context.id]))
	);

	private _commands = this.contextData$.pipe(
		map((contexts) => contexts.map((context) => context.commands).flat()),
		concatMap((commands) => commands),
		distinct((command) => command.name),
		scan((acc, curr) => {
			acc.push(curr);
			return acc;
		}, [] as Command[]),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public get contexts() {
		return this._contexts;
	}

	public get commands() {
		return this._commands;
	}

	setContextPriority(contexts: UsersContext[]) {
		return of(
			contexts.reduce((acc, curr) => {
				if (curr.name === 'Default User Context') return [curr, ...acc];
				return [...acc, curr];
			}, [] as UsersContext[])
		);
	}
}
