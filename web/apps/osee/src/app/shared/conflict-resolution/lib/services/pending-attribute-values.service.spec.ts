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
import { PendingAttributeValuesService } from './pending-attribute-values.service';

describe('PendingAttributeValuesService', () => {
	let service: PendingAttributeValuesService;

	beforeEach(() => {
		// Not root-provided; supply it explicitly (mirrors editor-level provision).
		TestBed.configureTestingModule({
			providers: [PendingAttributeValuesService],
		});
		service = TestBed.inject(PendingAttributeValuesService);
	});

	it('starts empty', () => {
		expect(service.hasPending()).toBe(false);
		expect(service.get('a1')).toBeUndefined();
		expect(service.getAll().size).toBe(0);
	});

	it('sets and gets a pending value', () => {
		service.set('a1', 'new value');
		expect(service.get('a1')).toBe('new value');
		expect(service.hasPending()).toBe(true);
	});

	it('overwrites an existing pending value', () => {
		service.set('a1', 'first');
		service.set('a1', 'second');
		expect(service.get('a1')).toBe('second');
		expect(service.getAll().size).toBe(1);
	});

	it('removes a pending value', () => {
		service.set('a1', 'v');
		service.remove('a1');
		expect(service.get('a1')).toBeUndefined();
		expect(service.hasPending()).toBe(false);
	});

	it('exposes a snapshot of all pending values', () => {
		service.set('a1', 'x');
		service.set('a2', 'y');
		const all = service.getAll();
		expect(all.size).toBe(2);
		expect(all.get('a1')).toBe('x');
		expect(all.get('a2')).toBe('y');
	});

	it('clears all pending values', () => {
		service.set('a1', 'x');
		service.set('a2', 'y');
		service.clear();
		expect(service.hasPending()).toBe(false);
		expect(service.getAll().size).toBe(0);
	});
});
