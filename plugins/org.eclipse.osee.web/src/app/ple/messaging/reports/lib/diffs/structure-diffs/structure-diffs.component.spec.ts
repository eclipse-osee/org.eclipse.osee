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
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { DiffReportService } from '@osee/messaging/shared/services';
import { DiffReportServiceMock } from '@osee/messaging/shared/testing';
import { DiffReportTableComponent } from '../../tables/diff-report-table/diff-report-table.component';

import { StructureDiffsComponent } from './structure-diffs.component';

describe('StructureDiffsComponent', () => {
	let component: StructureDiffsComponent;
	let fixture: ComponentFixture<StructureDiffsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			providers: [
				{ provide: DiffReportService, useValue: DiffReportServiceMock },
			],
			imports: [
				MatIconModule,
				MatTableModule,
				DiffReportTableComponent,
				StructureDiffsComponent,
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(StructureDiffsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
