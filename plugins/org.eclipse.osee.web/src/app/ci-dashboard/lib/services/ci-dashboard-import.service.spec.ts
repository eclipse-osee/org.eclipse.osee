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
import { CiDashboardImportService } from './ci-dashboard-import.service';
import { CiDashboardImportHttpService } from './ci-dashboard-import-http.service';
import { ciDashboardImportHttpServiceMock } from './ci-dashboard-import-http.service.mock';

describe('CiDashboardImportService', () => {
	let service: CiDashboardImportService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: CiDashboardImportHttpService,
					useValue: ciDashboardImportHttpServiceMock,
				},
			],
		});
		service = TestBed.inject(CiDashboardImportService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
