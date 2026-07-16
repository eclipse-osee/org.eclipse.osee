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
import { Injectable, signal, computed } from '@angular/core';

/**
 * Tracks which attribute editors have unsaved (dirty) changes.
 * Used to warn users before closing tabs or the browser.
 */
@Injectable({
	providedIn: 'root',
})
export class ArtifactEditorDirtyService {
	/** Set of editor keys that currently have dirty data. */
	private dirtyEditors = signal<Set<string>>(new Set());

	/** Whether any editor currently has unsaved changes. */
	readonly hasDirtyEditors = computed(() => this.dirtyEditors().size > 0);

	/** Mark an editor as dirty (has unsaved changes). */
	markDirty(editorKey: string) {
		this.dirtyEditors.update((set) => {
			const next = new Set(set);
			next.add(editorKey);
			return next;
		});
	}

	/** Mark an editor as clean (changes saved or reverted). */
	markClean(editorKey: string) {
		this.dirtyEditors.update((set) => {
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
