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

/**
 * Tracks which attribute editors currently hold unsaved changes, keyed by an
 * editor key. Consumers form keys as `${entityId}-${attributeId}` so both
 * "is this specific field dirty" and "does this entity have any dirty field"
 * can be answered.
 *
 * Root-provided so a single source of truth spans the parent editor (which
 * decides whether to reload on a remote change) and the child field editors
 * (which set the red conflict ring).
 */
@Injectable({
	providedIn: 'root',
})
export class EditorDirtyService {
	/** Set of editor keys that currently have dirty data. */
	private dirtyEditors = signal<Set<string>>(new Set());

	/**
	 * Monotonic "edit generation", bumped only on a bulk {@link clearAll} (conflict
	 * resolve/discard or a forced refresh). A field editor captures the generation
	 * when it starts a live edit; a later deferred commit (e.g. a text input's
	 * debounced blur) checks whether the generation still matches before saving.
	 *
	 * This is what makes a stale commit detectable precisely: it distinguishes "my
	 * edit is still live" from "my edit was discarded, and the pending slot I see was
	 * recreated by a *different*, newer edit." A per-field {@link markClean} (a normal
	 * save) does NOT bump it -- only a bulk invalidation does.
	 */
	private readonly _generation = signal(0);

	/** Current edit generation; capture at edit start, compare at deferred commit. */
	readonly generation = this._generation.asReadonly();

	/** Whether any editor currently has unsaved changes. */
	readonly hasDirtyEditors = computed(() => this.dirtyEditors().size > 0);

	/** Whether a specific editor key is currently dirty. */
	isDirty(editorKey: string): boolean {
		return this.dirtyEditors().has(editorKey);
	}

	/**
	 * Whether any editor for the given entity currently has unsaved changes.
	 * Editor keys are formatted as `${entityId}-${attributeId}`, so we match on
	 * the `${entityId}-` prefix. The trailing dash prevents false matches between
	 * entities whose IDs share a numeric prefix (e.g. "12" vs "123").
	 */
	hasDirtyEditorsForEntity(entityId: string): boolean {
		const prefix = `${entityId}-`;
		for (const key of this.dirtyEditors()) {
			if (key.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/** Mark an editor as dirty (has unsaved changes). */
	markDirty(editorKey: string) {
		this.dirtyEditors.update((set) => {
			if (set.has(editorKey)) {
				return set;
			}
			const next = new Set(set);
			next.add(editorKey);
			return next;
		});
	}

	/** Mark an editor as clean (changes saved or reverted). */
	markClean(editorKey: string) {
		this.dirtyEditors.update((set) => {
			if (!set.has(editorKey)) {
				return set;
			}
			const next = new Set(set);
			next.delete(editorKey);
			return next;
		});
	}

	/**
	 * Clear all dirty state (conflict resolve/discard, forced refresh). Bumps the
	 * edit generation so any in-flight deferred commit from before this point is
	 * recognized as stale and dropped, instead of re-saving a discarded value.
	 */
	clearAll() {
		this.dirtyEditors.set(new Set());
		this._generation.update((g) => g + 1);
	}
}
