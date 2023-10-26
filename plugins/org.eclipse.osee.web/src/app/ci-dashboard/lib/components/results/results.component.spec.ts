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
import ResultsComponent from './results.component';
import { TmoHttpService } from '../../services/tmo-http.service';
import { tmoHttpServiceMock } from '../../services/tmo-http.service.mock';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BatchDropdownMockComponent } from './batch-dropdown/batch-dropdown.component.mock';
import { RouterTestingModule } from '@angular/router/testing';

describe('ResultsComponent', () => {
	let component: ResultsComponent;
	let fixture: ComponentFixture<ResultsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ResultsComponent, {
			set: {
				imports: [
					CommonModule,
					RouterTestingModule,
					CiDashboardControlsMockComponent,
					MatTableModule,
					MatTooltipModule,
					BatchDropdownMockComponent,
				],
			},
		}).configureTestingModule({
			imports: [ResultsComponent],
			providers: [
				{ provide: TmoHttpService, useValue: tmoHttpServiceMock },
			],
		});
		fixture = TestBed.createComponent(ResultsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
