/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { CiDetailsService } from './ci-details.service';
import { TmoHttpService } from './tmo-http.service';
import { tmoHttpServiceMock } from './tmo-http.service.mock';
import { Injectable, signal } from '@angular/core';
import { CiDashboardUiService } from './ci-dashboard-ui.service';

@Injectable({
	providedIn: 'root',
})
class TestCiDetailsService extends CiDetailsService {
	set page(page: number) {
		this._currentPage.set(page);
	}

	set pageSize(size: number) {
		this._currentPageSize.set(size);
	}
}

describe('CiDetailsService (Abstract)', () => {
	let service: TestCiDetailsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TmoHttpService,
					useValue: tmoHttpServiceMock,
				},
				{
					provide: CiDashboardUiService,
					useValue: {
						branchId: signal('1'),
						ciSetId: signal('1'),
						BranchId: jasmine.createSpy('BranchId'),
						BranchType: jasmine.createSpy('BranchType'),
					},
				},
				TestCiDetailsService,
			],
		});
		service = TestBed.inject(TestCiDetailsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
