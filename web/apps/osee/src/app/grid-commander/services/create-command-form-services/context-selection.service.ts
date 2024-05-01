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
import { BehaviorSubject, combineLatest, iif, of, scan, switchMap } from 'rxjs';
import { UserContextRelationsService } from '../data-services/context-relations/user-context-relations.service';

@Injectable({
	providedIn: 'root',
})
export class ContextSelectionService {
	private _selectedContext = new BehaviorSubject<string>('');

	availableContexts = this.userContextRelationsService.contexts.pipe(
		switchMap((contexts) =>
			iif(
				() => contexts.length === 0,
				of(),
				of(contexts).pipe(
					switchMap((contexts) =>
						contexts.map((context) => context[0])
					),
					scan((acc, curr) => {
						acc.push(curr);
						return acc;
					}, [] as string[])
				)
			)
		)
	);

	contextDetails = combineLatest([
		this._selectedContext,
		this.userContextRelationsService.contexts,
	]).pipe(
		switchMap(([context, contextOptions]) =>
			iif(
				() => context === '',
				of(),
				of(contextOptions).pipe(
					switchMap((contextOptions) =>
						contextOptions.filter((option) => option[0] === context)
					)
				)
			)
		)
	);

	constructor(
		private userContextRelationsService: UserContextRelationsService
	) {}

	public get selectedContext() {
		return this._selectedContext;
	}
	public set SelectedContext(value: string) {
		this._selectedContext.next(value);
	}
}
