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
import { A11yModule } from '@angular/cdk/a11y';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	EnumerationSetService,
	MimPreferencesService,
	QueryService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import {
	enumerationSetServiceMock,
	MimPreferencesServiceMock,
	MockEnumFormUniqueComponent,
	QueryServiceMock,
	typesUIServiceMock,
} from '@osee/messaging/shared/testing';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { ApplicabilityListService } from '@osee/shared/services';
import { applicabilityListServiceMock } from '@osee/shared/testing';

import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { EditEnumSetFieldComponent } from './edit-enum-set-field.component';

describe('EditEnumSetFieldComponent', () => {
	let component: EditEnumSetFieldComponent;
	let fixture: ComponentFixture<EditEnumSetFieldComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(EditEnumSetFieldComponent, {
			set: {
				providers: [
					{ provide: QueryService, useValue: QueryServiceMock },
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
					},
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{ provide: TypesUIService, useValue: typesUIServiceMock },
				],
				imports: [
					NgIf,
					NgFor,
					AsyncPipe,
					A11yModule,
					MatIconModule,
					MatSelectModule,
					MatInputModule,
					MatFormFieldModule,
					FormsModule,
					MatTableModule,
					MockMatOptionLoadingComponent,
					MockEnumFormUniqueComponent,
					MockApplicabilityDropdownComponent,
				],
			},
		})
			.configureTestingModule({
				declarations: [],
				imports: [
					MatIconModule,
					MatSelectModule,
					MatInputModule,
					MatFormFieldModule,
					FormsModule,
					MatTableModule,
					MockMatOptionLoadingComponent,
					MockEnumFormUniqueComponent,
					MockApplicabilityDropdownComponent,
					EditEnumSetFieldComponent,
				],
				providers: [
					provideNoopAnimations(),
					{ provide: QueryService, useValue: QueryServiceMock },
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
					},
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{ provide: TypesUIService, useValue: typesUIServiceMock },
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(EditEnumSetFieldComponent);
		component = fixture.componentInstance;
	});
	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
