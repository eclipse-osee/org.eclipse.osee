/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 *
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { TestBed } from '@angular/core/testing';
import { OriginIdService } from './origin-id.service';

describe('OriginIdService', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({});
	});

	it('mints a non-empty origin id', () => {
		const service = TestBed.inject(OriginIdService);
		expect(service.originId).toBeTruthy();
		expect(typeof service.originId).toBe('string');
		expect(service.originId.length).toBeGreaterThan(0);
	});

	it('is stable across reads (same tab keeps one id)', () => {
		const service = TestBed.inject(OriginIdService);
		expect(service.originId).toBe(service.originId);
	});

	it('mints distinct ids for distinct instances (per-tab uniqueness)', () => {
		// Each tab constructs its own root service; two instances must not collide.
		const a = new OriginIdService();
		const b = new OriginIdService();
		expect(a.originId).not.toBe(b.originId);
	});
});
