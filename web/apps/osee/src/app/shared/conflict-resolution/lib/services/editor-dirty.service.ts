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
import { Injectable, Injector, computed, inject, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import {
	Observable,
	filter,
	firstValueFrom,
	map,
	race,
	take,
	timer,
} from 'rxjs';

/**
 * How a close/navigation flow should treat an entity (or the whole editor):
 * - `clean`: nothing unsaved — leave immediately, no prompt.
 * - `saving`: has unsaved edits, but they auto-save on blur. The close that
 *   caused the blur already triggered the save; the caller should wait for it to
 *   land ({@link EditorDirtyService.awaitSettled}) rather than warn.
 * - `conflicted`: a remote change arrived while dirty, so the edit is HELD for the
 *   user to resolve and cannot auto-save. This is the only state that warrants a
 *   warning before discarding.
 */
export type leaveState = 'clean' | 'saving' | 'conflicted';

/** Outcome of waiting for an entity's in-flight auto-save to complete. */
export type settleResult = 'saved' | 'timeout';

/**
 * Tracks which attribute editors currently hold unsaved changes, keyed by an
 * editor key, plus which entities are blocked by an unresolved conflict. Consumers
 * form dirty keys as `${entityId}-${attributeId}` so both "is this specific field
 * dirty" and "does this entity have any dirty field" can be answered.
 *
 * Root-provided so a single source of truth spans the parent editor (which decides
 * whether to reload on a remote change), the child field editors (which set the
 * conflict ring), and the close/unload flows (which decide whether leaving is safe,
 * via {@link leaveStateFor} / {@link awaitSettled}).
 */
@Injectable({
	providedIn: 'root',
})
export class EditorDirtyService {
	private readonly injector = inject(Injector);

	/** Set of editor keys that currently have dirty (unsaved) data. */
	private readonly dirtyEditors = signal<Set<string>>(new Set());

	/**
	 * Entities that currently have an unresolved conflict (a remote change arrived
	 * while the user had unsaved edits), keyed by entity id. A conflicted entity
	 * cannot auto-save — its edits are held until the user resolves them.
	 */
	private readonly conflictedEntities = signal<Set<string>>(new Set());

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

	/** Whether any editor is currently blocked by an unresolved conflict. */
	readonly hasConflictedEditors = computed(
		() => this.conflictedEntities().size > 0
	);

	/** Whether a specific editor key is currently dirty. */
	isDirty(editorKey: string): boolean {
		return this.dirtyEditors().has(editorKey);
	}

	/** Whether a specific entity is currently blocked by an unresolved conflict. */
	isConflicted(entityId: string): boolean {
		return this.conflictedEntities().has(entityId);
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

	/**
	 * The leave-state for a single entity: the one source of truth close flows use to
	 * decide between "leave now", "wait for the auto-save", and "warn". Conflict wins
	 * over plain dirtiness because a conflicted edit cannot auto-save.
	 */
	leaveStateFor(entityId: string): leaveState {
		if (this.isConflicted(entityId)) {
			return 'conflicted';
		}
		return this.hasDirtyEditorsForEntity(entityId) ? 'saving' : 'clean';
	}

	/**
	 * The leave-state across ALL entities, for a whole-window close (browser/tab
	 * unload). `conflicted` dominates `saving` dominates `clean`.
	 */
	globalLeaveState(): leaveState {
		if (this.hasConflictedEditors()) {
			return 'conflicted';
		}
		return this.hasDirtyEditors() ? 'saving' : 'clean';
	}

	/**
	 * Resolves once the entity has no remaining dirty editors (its in-flight
	 * auto-saves have landed), or `timeout` elapses first. Built on the real
	 * save-completion signal: the field editor calls {@link markClean} inside its
	 * save `next` handler, so dirtiness clearing IS the save settling.
	 *
	 * A deadline is required because a save can FAIL (the field stays dirty) — a
	 * `timeout` outcome lets the caller fall back to an explicit prompt rather than
	 * hang. Already-clean resolves immediately.
	 */
	awaitSettled(entityId: string, timeoutMs: number): Promise<settleResult> {
		if (!this.hasDirtyEditorsForEntity(entityId)) {
			return Promise.resolve('saved');
		}
		const settled$: Observable<settleResult> = toObservable(
			this.dirtyEditors,
			{ injector: this.injector }
		).pipe(
			filter(() => !this.hasDirtyEditorsForEntity(entityId)),
			take(1),
			map((): settleResult => 'saved')
		);
		const deadline$: Observable<settleResult> = timer(timeoutMs).pipe(
			map((): settleResult => 'timeout')
		);
		return firstValueFrom(race(settled$, deadline$), {
			defaultValue: 'timeout' as settleResult,
		});
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

	/** Flag an entity as having an unresolved conflict (set true) or not (set false). */
	setConflicted(entityId: string, conflicted: boolean) {
		this.conflictedEntities.update((set) => {
			if (set.has(entityId) === conflicted) {
				return set;
			}
			const next = new Set(set);
			if (conflicted) {
				next.add(entityId);
			} else {
				next.delete(entityId);
			}
			return next;
		});
	}

	/**
	 * Drop all dirty keys and the conflict flag for one entity — an explicit discard
	 * (e.g. closing a conflicted tab). A conflicted field editor's `ngOnDestroy`
	 * deliberately skips `markClean` (teardown must not drop a live conflict edit), so
	 * on discard the keys would otherwise leak and leave the entity looking `saving`
	 * forever. Bumps the (global) generation so any in-flight deferred commit for the
	 * now-gone editor is recognized as stale and cannot resurrect the discarded edit
	 * via the reconnect-flush; the bump is global like {@link clearAll}, harmless to
	 * other entities (their commits re-validate by value identity).
	 */
	clearEntity(entityId: string) {
		const prefix = `${entityId}-`;
		this.dirtyEditors.update((set) => {
			let next: Set<string> | null = null;
			for (const key of set) {
				if (key.startsWith(prefix)) {
					(next ??= new Set(set)).delete(key);
				}
			}
			return next ?? set;
		});
		this.setConflicted(entityId, false);
		this._generation.update((g) => g + 1);
	}

	/**
	 * Clear all dirty + conflict state (conflict resolve/discard, forced refresh).
	 * Bumps the edit generation so any in-flight deferred commit from before this
	 * point is recognized as stale and dropped, instead of re-saving a discarded
	 * value.
	 */
	clearAll() {
		this.dirtyEditors.set(new Set());
		this.conflictedEntities.set(new Set());
		this._generation.update((g) => g + 1);
	}
}
