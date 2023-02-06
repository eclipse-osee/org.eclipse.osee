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
import {
	NgIf,
	NgFor,
	AsyncPipe,
	TitleCasePipe,
	KeyValuePipe,
} from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockEnumSetFormUniqueComponent } from '../../forms/enum-set-form/enum-set-form.component.mock';

import { NewTypeFormComponent } from './new-type-form.component';
import {
	MockLogicalTypeSelectorComponent,
	MockNewPlatformTypeFormPage2Component,
	MimPreferencesServiceMock,
	typesServiceMock,
	enumsServiceMock,
	enumerationUiServiceMock,
	applicabilityListUIServiceMock,
} from '@osee/messaging/shared/testing';
import {
	MimPreferencesService,
	TypesService,
	EnumsService,
	EnumerationUIService,
	ApplicabilityListUIService,
} from '@osee/messaging/shared/services';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';

describe('NewTypeFormComponent', () => {
	let component: NewTypeFormComponent;
	let fixture: ComponentFixture<NewTypeFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule, NewTypeFormComponent],
		})
			.overrideComponent(NewTypeFormComponent, {
				set: {
					imports: [
						MatDialogModule,
						MatStepperModule,
						MatSelectModule,
						FormsModule,
						MatFormFieldModule,
						MatInputModule,
						MatButtonModule,
						MatTableModule,
						MatIconModule,
						MatOptionLoadingComponent,
						MockEnumSetFormUniqueComponent,
						MockLogicalTypeSelectorComponent,
						MockNewPlatformTypeFormPage2Component,
						NgIf,
						NgFor,
						AsyncPipe,
						TitleCasePipe,
						KeyValuePipe,
					],
					providers: [
						{
							provide: TransactionBuilderService,
							useValue: transactionBuilderMock,
						},
						{
							provide: MimPreferencesService,
							useValue: MimPreferencesServiceMock,
						},
						{
							provide: UserDataAccountService,
							useValue: userDataAccountServiceMock,
						},
						{ provide: TypesService, useValue: typesServiceMock },
						{ provide: EnumsService, useValue: enumsServiceMock },
						{
							provide: EnumerationUIService,
							useValue: enumerationUiServiceMock,
						},
						{
							provide: ApplicabilityListUIService,
							useValue: applicabilityListUIServiceMock,
						},
					],
				},
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewTypeFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
