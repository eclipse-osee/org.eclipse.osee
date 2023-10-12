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
import { CiDashboardControlsComponent } from './ci-dashboard-controls.component';
import { NgIf } from '@angular/common';
import { BranchPickerStub } from '@osee/shared/components/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('CiDashboardControlsComponent', () => {
	let component: CiDashboardControlsComponent;
	let fixture: ComponentFixture<CiDashboardControlsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(CiDashboardControlsComponent, {
			set: {
				imports: [NgIf, BranchPickerStub],
			},
		}).configureTestingModule({
			imports: [
				CiDashboardControlsComponent,
				BranchPickerStub,
				RouterTestingModule,
			],
		});
		fixture = TestBed.createComponent(CiDashboardControlsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
