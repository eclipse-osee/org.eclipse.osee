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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BranchPickerStub } from '../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';

import { ChangeReportComponent } from './change-report.component';
import { MockChangeReportTableComponent } from './mocks/change-report-table.component.mock';

describe('ChangeReportComponent', () => {
	let component: ChangeReportComponent;
	let fixture: ComponentFixture<ChangeReportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule, BranchPickerStub],
			declarations: [
				ChangeReportComponent,
				MockChangeReportTableComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ChangeReportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
