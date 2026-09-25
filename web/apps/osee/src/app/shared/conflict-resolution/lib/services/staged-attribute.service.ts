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
import { Injectable, computed, signal } from '@angular/core';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';

/**
 * A new attribute instance the user created locally but has NOT persisted,
 * because the artifact was in a conflict state when it was added. It carries the
 * transaction layer's new-instance sentinel (`id`/`gammaId` of `-1`) so it can be
 * committed as an `add` op unchanged, plus a client-generated {@link stagedKey}
 * that uniquely identifies this staged instance.
 *
 * The stagedKey is required because every unpersisted instance shares the `-1`
 * id/gamma, so `-1` cannot distinguish one staged add from another. Dirty flags
 * and pending values are keyed by the stagedKey (used as the instance `id` in the
 * merged editor list), letting each staged add ring, edit, and resolve
 * independently.
 */
export type stagedAttribute = {
	/** Stable client-side key identifying this staged instance (e.g. `TEMP-1`). */
	stagedKey: string;
	/** The new attribute instance to eventually add (sentinel id/gamma of `-1`). */
	attr: attribute<string, ATTRIBUTETYPEID>;
};

/**
 * Tracks new attribute instances created locally while the owning artifact is in
 * a conflict state, staged (not persisted) until the user resolves the conflict.
 *
 * These are the "add" counterpart to {@link PendingAttributeValuesService}'s
 * pending value edits: a value edit changes an existing instance, whereas a
 * staged add introduces a brand-new instance. Both flow through the same
 * conflict pipeline (dirty ring in the editor, read-out in the resolution
 * dialog, applied on resolve) so a conflicted add is never silently committed.
 *
 * Not provided in root: provide it at the editor component level so each open
 * editor has its own isolated set of staged additions.
 */
@Injectable()
export class StagedAttributeService {
	private readonly staged = signal<Map<string, stagedAttribute>>(new Map());
	private counter = 0;

	/** All staged additions as a reactive, insertion-ordered list. */
	readonly stagedAdds = computed(() => [...this.staged().values()]);

	/** Generates a stable, collision-free client key for a new staged instance. */
	nextKey(): string {
		this.counter += 1;
		return `TEMP-${this.counter}`;
	}

	/** Stages a new attribute instance under the given client key. */
	add(stagedKey: string, attr: attribute<string, ATTRIBUTETYPEID>) {
		const next = new Map(this.staged());
		next.set(stagedKey, { stagedKey, attr });
		this.staged.set(next);
	}

	/** Whether the given key identifies a currently-staged instance. */
	has(stagedKey: string): boolean {
		return this.staged().has(stagedKey);
	}

	/** Removes a single staged instance (e.g. the user discarded a staged add). */
	remove(stagedKey: string) {
		if (!this.staged().has(stagedKey)) {
			return;
		}
		const next = new Map(this.staged());
		next.delete(stagedKey);
		this.staged.set(next);
	}

	/** Returns the staged instances as a plain array snapshot. */
	getAll(): stagedAttribute[] {
		return [...this.staged().values()];
	}

	/** Clears all staged additions. */
	clear() {
		if (this.staged().size === 0) {
			return;
		}
		this.staged.set(new Map());
	}
}
