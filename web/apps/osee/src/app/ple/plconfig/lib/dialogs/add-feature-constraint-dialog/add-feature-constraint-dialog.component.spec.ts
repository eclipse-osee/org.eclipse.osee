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

import { AddFeatureConstraintDialogComponent } from './add-feature-constraint-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { defaultFeatureConstraint } from '../../types/pl-config-feature-constraints';
import { testBranchApplicabilityIdName } from '../../testing/mockBranchService';
import { of } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applicsMock } from '@osee/shared/testing';

describe('AddFeatureConstraintDialogComponent', () => {
	let component: AddFeatureConstraintDialogComponent;
	let fixture: ComponentFixture<AddFeatureConstraintDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				AddFeatureConstraintDialogComponent,
				BrowserAnimationsModule,
			],
			providers: [
				{
					provide: PlConfigCurrentBranchService,
					useValue: {
						branchApplicsIdName: of(testBranchApplicabilityIdName),
					},
				},
				{
					provide: ApplicabilityListUIService,
					useValue: {
						applic: of(applicsMock),
					},
				},
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						featureConstraint: defaultFeatureConstraint,
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(AddFeatureConstraintDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
