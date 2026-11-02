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
import { TestBed } from '@angular/core/testing';
import { EditorDirtyService } from './editor-dirty.service';

describe('EditorDirtyService', () => {
	let service: EditorDirtyService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(EditorDirtyService);
	});

	it('starts clean', () => {
		expect(service.hasDirtyEditors()).toBe(false);
		expect(service.isDirty('12-1')).toBe(false);
	});

	it('marks and clears a specific editor key', () => {
		service.markDirty('12-1');
		expect(service.isDirty('12-1')).toBe(true);
		expect(service.hasDirtyEditors()).toBe(true);

		service.markClean('12-1');
		expect(service.isDirty('12-1')).toBe(false);
		expect(service.hasDirtyEditors()).toBe(false);
	});

	it('matches dirty editors by entity prefix', () => {
		service.markDirty('12-1');
		expect(service.hasDirtyEditorsForEntity('12')).toBe(true);
		expect(service.hasDirtyEditorsForEntity('99')).toBe(false);
	});

	it('does not match entities that share a numeric prefix', () => {
		// "12" must not match an editor keyed under entity "123" (the trailing dash guards this).
		service.markDirty('123-1');
		expect(service.hasDirtyEditorsForEntity('12')).toBe(false);
		expect(service.hasDirtyEditorsForEntity('123')).toBe(true);
	});

	it('clearAll removes all dirty state', () => {
		service.markDirty('12-1');
		service.markDirty('12-2');
		service.clearAll();
		expect(service.hasDirtyEditors()).toBe(false);
	});

	describe('conflict tracking', () => {
		it('starts with no conflicts', () => {
			expect(service.hasConflictedEditors()).toBe(false);
			expect(service.isConflicted('12')).toBe(false);
		});

		it('flags and clears a conflicted entity', () => {
			service.setConflicted('12', true);
			expect(service.isConflicted('12')).toBe(true);
			expect(service.hasConflictedEditors()).toBe(true);
			expect(service.isConflicted('99')).toBe(false);

			service.setConflicted('12', false);
			expect(service.isConflicted('12')).toBe(false);
			expect(service.hasConflictedEditors()).toBe(false);
		});

		it('is idempotent on repeated setConflicted', () => {
			service.setConflicted('12', true);
			service.setConflicted('12', true);
			expect(service.hasConflictedEditors()).toBe(true);
			service.setConflicted('12', false);
			service.setConflicted('12', false);
			expect(service.hasConflictedEditors()).toBe(false);
		});

		it('clearAll also clears conflict state', () => {
			service.setConflicted('12', true);
			service.clearAll();
			expect(service.hasConflictedEditors()).toBe(false);
			expect(service.isConflicted('12')).toBe(false);
		});
	});

	describe('clearEntity (explicit discard)', () => {
		it('drops the entity dirty keys and conflict flag, leaving it clean', () => {
			service.markDirty('12-1');
			service.markDirty('12-2');
			service.setConflicted('12', true);

			service.clearEntity('12');

			expect(service.hasDirtyEditorsForEntity('12')).toBe(false);
			expect(service.isConflicted('12')).toBe(false);
			expect(service.leaveStateFor('12')).toBe('clean');
		});

		it('leaves other entities untouched', () => {
			service.markDirty('12-1');
			service.markDirty('99-1');
			service.setConflicted('99', true);

			service.clearEntity('12');

			expect(service.hasDirtyEditorsForEntity('12')).toBe(false);
			// 99 is unaffected.
			expect(service.hasDirtyEditorsForEntity('99')).toBe(true);
			expect(service.isConflicted('99')).toBe(true);
		});

		it('does not match an entity whose id is a prefix of another', () => {
			service.markDirty('12-1');
			service.markDirty('123-1');

			service.clearEntity('12');

			expect(service.hasDirtyEditorsForEntity('12')).toBe(false);
			// "123" must survive clearing "12" (trailing-dash prefix guard).
			expect(service.hasDirtyEditorsForEntity('123')).toBe(true);
		});

		it('bumps the generation so in-flight deferred commits are invalidated', () => {
			service.markDirty('12-1');
			const before = service.generation();
			service.clearEntity('12');
			expect(service.generation()).toBe(before + 1);
		});
	});

	it('is idempotent on repeated mark/clean', () => {
		service.markDirty('12-1');
		service.markDirty('12-1');
		expect(service.hasDirtyEditors()).toBe(true);
		service.markClean('12-1');
		service.markClean('12-1');
		expect(service.hasDirtyEditors()).toBe(false);
	});

	describe('edit generation', () => {
		it('bumps only on clearAll (bulk invalidation), not on per-field mark/clean', () => {
			const start = service.generation();

			// Per-field dirty/clean must NOT bump the generation: a normal save of one
			// field cannot invalidate another field's in-flight deferred commit.
			service.markDirty('12-1');
			service.markClean('12-1');
			expect(service.generation()).toBe(start);

			// A bulk clear (conflict resolve/discard/forced refresh) bumps it so any
			// in-flight deferred commit from before this point is recognized as stale.
			service.markDirty('12-1');
			service.clearAll();
			expect(service.generation()).toBe(start + 1);

			service.clearAll();
			expect(service.generation()).toBe(start + 2);
		});
	});

	describe('leave state', () => {
		it('is clean for an entity with no dirty or conflicted editors', () => {
			expect(service.leaveStateFor('12')).toBe('clean');
			expect(service.globalLeaveState()).toBe('clean');
		});

		it('is saving for a dirty, non-conflicted entity', () => {
			service.markDirty('12-1');
			expect(service.leaveStateFor('12')).toBe('saving');
			expect(service.globalLeaveState()).toBe('saving');
		});

		it('is conflicted when the entity has a conflict, even if also dirty', () => {
			service.markDirty('12-1');
			service.setConflicted('12', true);
			// Conflict dominates: a held edit cannot auto-save.
			expect(service.leaveStateFor('12')).toBe('conflicted');
			expect(service.globalLeaveState()).toBe('conflicted');
		});

		it('scopes leaveStateFor to the given entity', () => {
			service.markDirty('12-1');
			expect(service.leaveStateFor('99')).toBe('clean');
			// Global still sees the dirty entity.
			expect(service.globalLeaveState()).toBe('saving');
		});
	});

	describe('awaitSettled', () => {
		it('resolves "saved" immediately when the entity is already clean', async () => {
			await expect(service.awaitSettled('12', 1000)).resolves.toBe(
				'saved'
			);
		});

		it('resolves "saved" once the entity\'s dirty editors clear', async () => {
			service.markDirty('12-1');
			const settled = service.awaitSettled('12', 1000);
			// Simulate the async save landing.
			service.markClean('12-1');
			await expect(settled).resolves.toBe('saved');
		});

		it('ignores clears of OTHER entities', async () => {
			service.markDirty('12-1');
			service.markDirty('99-1');
			const settled = service.awaitSettled('12', 1000);
			// Another entity settling must not resolve this wait.
			service.markClean('99-1');
			service.markClean('12-1');
			await expect(settled).resolves.toBe('saved');
		});

		it('resolves "timeout" when the save never lands', async () => {
			service.markDirty('12-1');
			await expect(service.awaitSettled('12', 10)).resolves.toBe(
				'timeout'
			);
		});
	});
});
