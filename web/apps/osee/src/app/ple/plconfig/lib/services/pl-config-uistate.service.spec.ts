/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import { PlConfigUIStateService } from './pl-config-uistate.service';

describe('PlConfigUIStateService', () => {
	let service: PlConfigUIStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({ providers: [PlConfigUIStateService] });
		service = TestBed.inject(PlConfigUIStateService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('#get branchId should return string from observable when given a string', (done: DoneFn) => {
		service.branchIdNum = 'string';
		service.branchId.subscribe((value) => {
			expect(value).toBeInstanceOf(String);
			expect(value).toBe('string');
			done();
		});
		service.branchIdNum = 'string';
	});
	it('#get viewBranchType should return string "all" from observable when given a string "All"', (done: DoneFn) => {
		service.viewBranchTypeString = '';
		service.viewBranchType.subscribe((value) => {
			expect(value).toBeInstanceOf(String);
			expect(value).toBe('');
			done();
		});
		service.viewBranchTypeString = '';
	});
	it('#get viewBranchType should return string "working" from observable when given a string "Working"', (done: DoneFn) => {
		service.viewBranchTypeString = 'working';
		service.viewBranchType.subscribe((value) => {
			expect(value).toBeInstanceOf(String);
			expect(value).toBe('working');
			done();
		});
		service.viewBranchTypeString = 'working';
	});
	it('#get viewBranchType should return string "baseline" from observable when given a string "Baseline"', (done: DoneFn) => {
		service.viewBranchTypeString = 'baseline';
		service.viewBranchType.subscribe((value) => {
			expect(value).toBeInstanceOf(String);
			expect(value).toBe('baseline');
			done();
		});
		service.viewBranchTypeString = 'baseline';
	});
	it('#get updateReq should return boolean "true" from observable when given a bool "true"', (done: DoneFn) => {
		service.updateReq.subscribe((value) => {
			expect(value).toBeInstanceOf(Boolean);
			expect(value).toBe(true);
			done();
		});
		service.updateReqConfig = true;
	});
});
