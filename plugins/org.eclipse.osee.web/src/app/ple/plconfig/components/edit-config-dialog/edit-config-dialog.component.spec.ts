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
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { testBranchApplicability } from '../../testing/mockBranchService';
import { plConfigTypesServiceMock } from '../../testing/pl-config-types.service.mock';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';

import { EditConfigurationDialogComponent } from './edit-config-dialog.component';

describe('EditConfigDialogComponent', () => {
	let component: EditConfigurationDialogComponent;
	let fixture: ComponentFixture<EditConfigurationDialogComponent>;

	beforeEach(async () => {
		const currentBranchService = jasmine.createSpyObj(
			'PlConfigCurrentBranchService',
			[],
			['cfgGroups']
		);
		const branchService = jasmine.createSpyObj('PlConfigBranchService', [
			'getBranchApplicability',
		]);
		await TestBed.configureTestingModule({
			imports: [
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule,
				MatDialogModule,
				NoopAnimationsModule,
				MatListModule,
				MatButtonModule,
				FormsModule,
			],
			declarations: [EditConfigurationDialogComponent],
			providers: [
				{ provide: PlConfigBranchService, useValue: branchService },
				{
					provide: PlConfigCurrentBranchService,
					useValue: currentBranchService,
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
						currentConfig: {
							id: '200045',
							name: 'Product A',
							description: '',
							ConfigurationToCopyFrom: {
								id: '',
								name: '',
								description: '',
							},
						},
					},
				},
				{
					provide: PlConfigBranchService,
					useValue: {
						getBranchApplicability() {
							return of(testBranchApplicability);
						},
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditConfigurationDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
