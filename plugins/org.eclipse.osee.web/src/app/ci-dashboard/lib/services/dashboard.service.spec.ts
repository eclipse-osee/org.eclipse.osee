/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { DashboardService } from './dashboard.service';
import { DashboardHttpService } from '../services/dashboard-http.service';
import { dashboardHttpServiceMock } from '../services/dashboard-http.service.mock';

describe('DashboardService', () => {
	let service: DashboardService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: DashboardHttpService,
					useValue: dashboardHttpServiceMock,
				},
			],
		});
		service = TestBed.inject(DashboardService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
