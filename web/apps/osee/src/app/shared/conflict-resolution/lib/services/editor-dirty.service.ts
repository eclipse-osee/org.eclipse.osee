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

	/** Clear all dirty state (e.g., on full page refresh). */
	clearAll() {
		this.dirtyEditors.set(new Set());
	}
}
