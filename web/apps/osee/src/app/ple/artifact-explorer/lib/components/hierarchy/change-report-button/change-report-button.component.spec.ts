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

import { ChangeReportButtonComponent } from './change-report-button.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';

describe('ChangeReportButtonComponent', () => {
	let component: ChangeReportButtonComponent;
	let fixture: ComponentFixture<ChangeReportButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ChangeReportButtonComponent],
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: {
						get currentBranch() {
							return of(testBranchInfo);
						},
						get parentBranch() {
							return of(testBranchInfo.parentBranch.id);
						},
					} as Partial<CurrentBranchInfoService>,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ChangeReportButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
