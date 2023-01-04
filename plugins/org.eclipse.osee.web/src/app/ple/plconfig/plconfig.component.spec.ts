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
import { ApplicabilityTableComponent } from './lib/table/applicability-table/applicability-table.component';
import { CompoundApplicabilityDropdownComponent } from './lib/dropdowns/compound-applicability-dropdown/compound-applicability-dropdown.component';
import { ConfigurationDropdownComponent } from './lib/dropdowns/configuration-dropdown/configuration-dropdown.component';
import { ConfigurationGroupDropdownComponent } from './lib/dropdowns/configuration-group-dropdown/configuration-group-dropdown.component';
import { EditDefinitionsDropdownComponent } from './lib/dropdowns/edit-definitions-dropdown/edit-definitions-dropdown.component';
import { FeatureDropdownComponent } from './lib/dropdowns/feature-dropdown/feature-dropdown.component';
import { ProductTypeDropDownComponent } from './lib/dropdowns/product-type-drop-down/product-type-drop-down.component';
import { PlconfigComponent } from './plconfig.component';
import {
	ActionDropdownStub,
	BranchPickerStub,
} from '@osee/shared/components/testing';

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
				ActionDropdownStub,
				BranchPickerStub,
			],
			declarations: [
				PlconfigComponent,
				ApplicabilityTableComponent,
				ConfigurationDropdownComponent,
				ConfigurationGroupDropdownComponent,
				FeatureDropdownComponent,
				ProductTypeDropDownComponent,
				EditDefinitionsDropdownComponent,
				CompoundApplicabilityDropdownComponent,
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
