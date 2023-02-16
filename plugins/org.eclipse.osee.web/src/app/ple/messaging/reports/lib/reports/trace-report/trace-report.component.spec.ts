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
import { RouterTestingModule } from '@angular/router/testing';
import { ReportsService } from '@osee/messaging/shared';
import { ReportsServiceMock } from 'src/app/ple/messaging/shared/testing/reports-service.mock';

import { NodeTraceReportRequirementsComponent } from './trace-report.component';

describe('NodeTraceReportComponent', () => {
	let component: NodeTraceReportRequirementsComponent;
	let fixture: ComponentFixture<NodeTraceReportRequirementsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				NodeTraceReportRequirementsComponent,
				RouterTestingModule,
			],
			providers: [
				{ provide: ReportsService, useValue: ReportsServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(NodeTraceReportRequirementsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
