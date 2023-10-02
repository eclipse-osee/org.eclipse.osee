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

import { CiDashboardComponent } from './ci-dashboard.component';
import { RouterTestingModule } from '@angular/router/testing';
import { tmoServiceMock } from './lib/testing/tmo.service.mock';
import { TmoService } from './lib/services/tmo.service';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('CiDashboardComponent', () => {
	let component: CiDashboardComponent;
	let fixture: ComponentFixture<CiDashboardComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				CiDashboardComponent,
				RouterTestingModule,
				NoopAnimationsModule,
			],
			providers: [{ provide: TmoService, useValue: tmoServiceMock }],
		});
		fixture = TestBed.createComponent(CiDashboardComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
