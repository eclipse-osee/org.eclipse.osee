/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Injectable, inject, signal } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { UnreferencedService } from '../http/unreferenced.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { combineLatest, debounceTime, iif, of, repeat, switchMap } from 'rxjs';
import {
	PlatformType,
	element,
	message,
	structure,
	subMessage,
} from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class UnreferencedUiService {
	private _ui = inject(UiService);
	private _unreferencedService = inject(UnreferencedService);

	/**
	 * PLATFORM TYPES
	 */
	public currentTypesPage = signal(0);
	public currentTypesPageSize = signal(10);

	public currentTypesFilter = signal('');

	private _currentTypesPage = toObservable(this.currentTypesPage);

	private _currentTypesPageSize = toObservable(this.currentTypesPageSize);

	private _currentTypesFilter = toObservable(this.currentTypesFilter);
	private _types = combineLatest([
		this._ui.id,
		this._currentTypesFilter,
		this._currentTypesPage,
		this._currentTypesPageSize,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedPlatformTypes(
						id,
						filter,
						page + 1,
						pageSize
					)
					.pipe(repeat({ delay: () => this._ui.update })),
				of([] as PlatformType[])
			)
		)
	);

	private _typesCount = combineLatest([
		this._ui.id,
		this._currentTypesFilter,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedPlatformTypesCount(id, filter)
					.pipe(repeat({ delay: () => this._ui.update })),
				of(0)
			)
		)
	);

	public types = toSignal(this._types, {
		initialValue: [] as PlatformType[],
	});

	public typesCount = toSignal(this._typesCount, { initialValue: 0 });

	/**
	 * END PLATFORM TYPES
	 */

	/**
	 * Elements
	 */
	public currentElementsPage = signal(0);
	public currentElementsPageSize = signal(10);

	public currentElementsFilter = signal('');

	private _currentElementsPage = toObservable(this.currentElementsPage);

	private _currentElementsPageSize = toObservable(
		this.currentElementsPageSize
	);

	private _currentElementsFilter = toObservable(this.currentElementsFilter);
	private _elements = combineLatest([
		this._ui.id,
		this._currentElementsFilter,
		this._currentElementsPage,
		this._currentElementsPageSize,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedElements(id, filter, page + 1, pageSize)
					.pipe(repeat({ delay: () => this._ui.update })),
				of([] as element[])
			)
		)
	);

	private _elementsCount = combineLatest([
		this._ui.id,
		this._currentElementsFilter,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedElementsCount(id, filter)
					.pipe(repeat({ delay: () => this._ui.update })),
				of(0)
			)
		)
	);

	public elements = toSignal(this._elements, {
		initialValue: [] as element[],
	});

	public elementsCount = toSignal(this._elementsCount, { initialValue: 0 });

	/**
	 * END Elements
	 */

	/**
	 * Structures
	 */
	public currentStructuresPage = signal(0);
	public currentStructuresPageSize = signal(10);

	public currentStructuresFilter = signal('');

	private _currentStructuresPage = toObservable(this.currentStructuresPage);

	private _currentStructuresPageSize = toObservable(
		this.currentStructuresPageSize
	);

	private _currentStructuresFilter = toObservable(
		this.currentStructuresFilter
	);
	private _structures = combineLatest([
		this._ui.id,
		this._currentStructuresFilter,
		this._currentStructuresPage,
		this._currentStructuresPageSize,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedStructures(id, filter, page + 1, pageSize)
					.pipe(repeat({ delay: () => this._ui.update })),
				of([] as structure[])
			)
		)
	);

	private _structuresCount = combineLatest([
		this._ui.id,
		this._currentStructuresFilter,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedStructuresCount(id, filter)
					.pipe(repeat({ delay: () => this._ui.update })),
				of(0)
			)
		)
	);

	public structures = toSignal(this._structures, {
		initialValue: [] as structure[],
	});

	public structuresCount = toSignal(this._structuresCount, {
		initialValue: 0,
	});

	/**
	 * END Structures
	 */

	/**
	 * Submessages
	 */
	public currentSubmessagesPage = signal(0);
	public currentSubmessagesPageSize = signal(10);

	public currentSubmessagesFilter = signal('');

	private _currentSubmessagesPage = toObservable(this.currentSubmessagesPage);

	private _currentSubmessagesPageSize = toObservable(
		this.currentSubmessagesPageSize
	);

	private _currentSubmessagesFilter = toObservable(
		this.currentSubmessagesFilter
	);
	private _submessages = combineLatest([
		this._ui.id,
		this._currentSubmessagesFilter,
		this._currentSubmessagesPage,
		this._currentSubmessagesPageSize,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedSubmessages(id, filter, page + 1, pageSize)
					.pipe(repeat({ delay: () => this._ui.update })),
				of([] as subMessage[])
			)
		)
	);

	private _submessagesCount = combineLatest([
		this._ui.id,
		this._currentSubmessagesFilter,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedSubmessagesCount(id, filter)
					.pipe(repeat({ delay: () => this._ui.update })),
				of(0)
			)
		)
	);

	public submessages = toSignal(this._submessages, {
		initialValue: [] as subMessage[],
	});

	public submessagesCount = toSignal(this._submessagesCount, {
		initialValue: 0,
	});

	/**
	 * END Submessages
	 */

	/**
	 * Messages
	 */
	public currentMessagesPage = signal(0);
	public currentMessagesPageSize = signal(10);

	public currentMessagesFilter = signal('');

	private _currentMessagesPage = toObservable(this.currentMessagesPage);

	private _currentMessagesPageSize = toObservable(
		this.currentMessagesPageSize
	);

	private _currentMessagesFilter = toObservable(this.currentMessagesFilter);
	private _messages = combineLatest([
		this._ui.id,
		this._currentMessagesFilter,
		this._currentMessagesPage,
		this._currentMessagesPageSize,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter, page, pageSize]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedMessages(id, filter, page + 1, pageSize)
					.pipe(repeat({ delay: () => this._ui.update })),
				of([] as message[])
			)
		)
	);

	private _messagesCount = combineLatest([
		this._ui.id,
		this._currentMessagesFilter,
	]).pipe(
		debounceTime(500),
		switchMap(([id, filter]) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this._unreferencedService
					.getUnreferencedMessagesCount(id, filter)
					.pipe(repeat({ delay: () => this._ui.update })),
				of(0)
			)
		)
	);

	public messages = toSignal(this._messages, {
		initialValue: [] as message[],
	});

	public messagesCount = toSignal(this._messagesCount, { initialValue: 0 });

	/**
	 * END Messages
	 */
}
