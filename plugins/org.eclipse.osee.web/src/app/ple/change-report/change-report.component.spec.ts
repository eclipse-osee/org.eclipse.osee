/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { NgIf, AsyncPipe } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BranchPickerStub } from '@osee/shared/components/testing';
import { ChangeReportComponent } from './change-report.component';
import { MockChangeReportTableComponent } from './mocks/change-report-table.component.mock';
import { BranchPickerComponent } from '@osee/shared/components';
import { ChangeReportTableComponent } from './components/change-report-table/change-report-table.component';

describe('ChangeReportComponent', () => {
	let component: ChangeReportComponent;
	let fixture: ComponentFixture<ChangeReportComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ChangeReportComponent, {
			add: {
				imports: [
					BranchPickerStub,
					NgIf,
					AsyncPipe,
					MockChangeReportTableComponent,
				],
			},
			remove: {
				imports: [BranchPickerComponent, ChangeReportTableComponent],
			},
		})
			.configureTestingModule({
				imports: [RouterTestingModule, ChangeReportComponent],
				declarations: [],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ChangeReportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
