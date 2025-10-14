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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverflowMenuComponent } from './overflow-menu.component';

import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { DashboardService } from '../../../services/dashboard.service';
import { dashboardServiceMock } from '../../../services/dashboard.service.mock';

describe('OverflowMenuComponent', () => {
	let component: OverflowMenuComponent;
	let fixture: ComponentFixture<OverflowMenuComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [OverflowMenuComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: DashboardService,
					useValue: dashboardServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(OverflowMenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
