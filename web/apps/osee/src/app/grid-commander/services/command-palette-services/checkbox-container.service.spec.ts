/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import { CheckboxContainerService } from './checkbox-container.service';

describe('CheckboxContainerService', () => {
	let service: CheckboxContainerService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(CheckboxContainerService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('when true is passed to clearIsChecked, clearIsCheckedVal will be true', (done: DoneFn) => {
		service.updateClearIsChecked(true);
		service.clearIsCheckedVal.subscribe((val) => {
			expect(val).toBeInstanceOf(Boolean);
			expect(val).toBe(true);
			done();
		});
	});

	it('when true is passed into clearIsChecked, clearIsCheckedVal will be true', (done: DoneFn) => {
		service.updateClearIsChecked(true);
		service.clearIsCheckedVal.subscribe((val) => {
			expect(val).toBeInstanceOf(Boolean);
			expect(val).toBe(true);
			done();
		});
	});
});
