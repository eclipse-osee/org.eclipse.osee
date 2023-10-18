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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import CiDashboardImportComponent from './ci-dashboard-import.component';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { ciDashboardImportHttpServiceMock } from '../../services/ci-dashboard-import-http.service.mock';
import { CiDashboardImportHttpService } from '../../services/ci-dashboard-import-http.service';
import { AsyncPipe, NgIf } from '@angular/common';

describe('CiDashboardImportComponent', () => {
	let component: CiDashboardImportComponent;
	let fixture: ComponentFixture<CiDashboardImportComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(CiDashboardImportComponent, {
			set: {
				imports: [AsyncPipe, NgIf, CiDashboardControlsMockComponent],
			},
		}).configureTestingModule({
			imports: [CiDashboardImportComponent],
			providers: [
				{
					provide: CiDashboardImportHttpService,
					useValue: ciDashboardImportHttpServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(CiDashboardImportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
