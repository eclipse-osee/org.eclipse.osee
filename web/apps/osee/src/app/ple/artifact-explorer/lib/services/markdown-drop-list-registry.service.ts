/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { computed, Injectable, signal } from '@angular/core';

/**
 * Tracks the CDK drop-list ids of markdown editors that accept artifact drops.
 *
 * The artifact hierarchy is the drag source and must declare its drop targets by
 * id (`cdkDropListConnectedTo`). The markdown editor lists are nested inside the
 * tab group's own `cdkDropList`, which shadows the `cdkDropListGroup` for its
 * descendants (CDK provides `CDK_DROP_LIST_GROUP = undefined` under a drop list),
 * so those lists never join the group automatically. Registering their ids here
 * lets the hierarchy connect to them explicitly, the same mechanism the relations
 * editor uses with its static `relationList` id.
 *
 * Each markdown editor registers a unique id on init and removes it on destroy, so
 * the hierarchy only connects to lists that currently exist (no stale/duplicate ids).
 */
@Injectable({
	providedIn: 'root',
})
export class MarkdownDropListRegistryService {
	private readonly _ids = signal<string[]>([]);

	/** Ids currently registered, for binding to `cdkDropListConnectedTo`. */
	readonly ids = computed(() => this._ids());

	/** Monotonic counter backing {@link nextId}; collision-free within the session. */
	private _counter = 0;

	register(id: string): void {
		this._ids.update((ids) => (ids.includes(id) ? ids : [...ids, id]));
	}

	unregister(id: string): void {
		this._ids.update((ids) => ids.filter((existing) => existing !== id));
	}

	/** A unique drop-list id. A simple incrementing counter is obviously collision-free. */
	nextId(): string {
		return `osee-md-editor-drop-${this._counter++}`;
	}
}
