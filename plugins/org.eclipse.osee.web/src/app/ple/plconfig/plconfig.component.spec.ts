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
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { ActionDropDownComponent } from '../../shared-components/components/action-state-button/action-drop-down/action-drop-down.component';
import { ActionDropdownStub } from '../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchPickerStub } from '../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { ApplicabilityTableComponent } from './components/applicability-table/applicability-table.component';
import { ConfigurationDropdownComponent } from './components/dropdowns/configuration-dropdown/configuration-dropdown.component';
import { ConfigurationGroupDropdownComponent } from './components/dropdowns/configuration-group-dropdown/configuration-group-dropdown.component';
import { FeatureDropdownComponent } from './components/dropdowns/feature-dropdown/feature-dropdown.component';
import { ProductTypeDropDownComponent } from './components/dropdowns/product-type-drop-down/product-type-drop-down.component';

import { PlconfigComponent } from './plconfig.component';

describe('PlconfigComponent', () => {
	let component: PlconfigComponent;
	let fixture: ComponentFixture<PlconfigComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientModule,
				MatDialogModule,
				MatMenuModule,
				MatIconModule,
				RouterTestingModule,
				MatTableModule,
				MatFormFieldModule,
				FormsModule,
				MatInputModule,
				MatSelectModule,
				MatRadioModule,
				MatTooltipModule,
				MatPaginatorModule,
				MatButtonModule,
				NoopAnimationsModule,
			],
			declarations: [
				PlconfigComponent,
				ApplicabilityTableComponent,
				BranchPickerStub,
				ActionDropdownStub,
				ConfigurationDropdownComponent,
				ConfigurationGroupDropdownComponent,
				FeatureDropdownComponent,
				ProductTypeDropDownComponent,
			],
			providers: [
				{
					provide: Router,
					useValue: { navigate: () => {}, events: of() },
				},
				{
					provide: ActivatedRoute,
					useValue: {
						paramMap: of(
							convertToParamMap({
								branchId: '10',
								branchType: 'all',
							})
						),
					},
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PlconfigComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
