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

	it('is idempotent on repeated mark/clean', () => {
		service.markDirty('12-1');
		service.markDirty('12-1');
		expect(service.hasDirtyEditors()).toBe(true);
		service.markClean('12-1');
		service.markClean('12-1');
		expect(service.hasDirtyEditors()).toBe(false);
	});
});
