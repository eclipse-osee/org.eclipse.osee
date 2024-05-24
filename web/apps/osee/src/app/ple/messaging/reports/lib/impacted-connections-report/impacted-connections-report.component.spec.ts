/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { ImpactedConnectionsReportComponent } from './impacted-connections-report.component';

import { ReportsServiceMock } from '@osee/messaging/shared/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ReportsService } from '@osee/messaging/shared/services';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock } from '@osee/shared/testing';

describe('ImpactedConnectionsReport', () => {
	let component: ImpactedConnectionsReportComponent;
	let fixture: ComponentFixture<ImpactedConnectionsReportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ImpactedConnectionsReportComponent],
			providers: [
				provideRouter([]),
				provideNoopAnimations(),
				{
					provide: ReportsService,
					useValue: ReportsServiceMock,
				},
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ImpactedConnectionsReportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
