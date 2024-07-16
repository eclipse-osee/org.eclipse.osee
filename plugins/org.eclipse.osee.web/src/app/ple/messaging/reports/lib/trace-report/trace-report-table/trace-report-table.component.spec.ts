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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { TraceReportTableComponent } from './trace-report-table.component';

describe('NodeTraceReqTableComponent', () => {
	let component: TraceReportTableComponent;
	let fixture: ComponentFixture<TraceReportTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TraceReportTableComponent, NoopAnimationsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(TraceReportTableComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('data', []);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
