/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { FormsModule } from '@angular/forms';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';
import { writeFeature } from '../../types/pl-config-features';

import { AddFeatureDialogComponent } from './add-feature-dialog.component';

describe('AddFeatureDialogComponent', () => {
	let component: AddFeatureDialogComponent;
	let fixture: ComponentFixture<AddFeatureDialogComponent>;

	beforeEach(async () => {
		const branchService = jasmine.createSpyObj('PlConfigBranchService', [
			'getBranchApplicability',
		]);
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				MatListModule,
				MatDialogModule,
				MatInputModule,
				MatSelectModule,
				FormsModule,
				AddFeatureDialogComponent,
				NoopAnimationsModule,
				MatSlideToggleModule,
			],
			providers: [
				{ provide: PlConfigBranchService, useValue: branchService },
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
				{
					provide: PlConfigTypesService,
					useValue: plConfigTypesServiceMock,
				},
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						currentBranch: '3182843164128526558',
						feature: new writeFeature(),
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddFeatureDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
