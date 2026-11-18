/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { CiSetRoutedUiService } from './ci-set-routed-ui.service';
@Component({ template: '' })
class BlankComponent {}
describe('CiSetRoutedUiService', () => {
	let service: CiSetRoutedUiService;
	let router: Router;
	let routedSetIds: string[];
	beforeEach(() => {
		routedSetIds = [];
		TestBed.configureTestingModule({
			providers: [
				provideRouter([{ path: '**', component: BlankComponent }]),
				{
					provide: CiDashboardUiService,
					useValue: {
						set CiSetId(value: string) {
							routedSetIds.push(value);
						},
					},
				},
			],
		});
		router = TestBed.inject(Router);
		service = TestBed.inject(CiSetRoutedUiService);
	});
	it('should be created', () => {
		expect(service).toBeTruthy();
	});
	it('should not report a set before the first navigation settles', () => {
		TestBed.tick();
		expect(service.urlSetId()).toBeUndefined();
		expect(routedSetIds).toEqual([]);
	});
	it('should report the set id from the settled URL', async () => {
		await router.navigateByUrl('/ci/allScripts?branchId=1&set=1234');
		expect(service.urlSetId()).toBe('1234');
		TestBed.tick();
		expect(routedSetIds).toEqual(['1234']);
	});
	it("should report '-1' only when the settled URL has no set", async () => {
		await router.navigateByUrl('/ci/allScripts?branchId=1');
		expect(service.urlSetId()).toBe('-1');
	});
});
