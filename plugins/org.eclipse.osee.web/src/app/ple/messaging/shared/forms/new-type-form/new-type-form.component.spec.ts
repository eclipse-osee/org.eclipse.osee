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
import { MatOptionLoadingComponent } from '../../../../../shared-components/mat-option-loading/mat-option-loading/mat-option-loading.component';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { transactionBuilderMock } from '../../../../../transactions/transaction-builder.service.mock';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../../userdata/services/user-data-account.service.mock';
import { applicabilityListUIServiceMock } from '../../testing/applicability-list-ui.service.mock';
import { enumerationUiServiceMock } from '../../testing/enumeration-ui.service.mock';
import { enumsServiceMock } from '../../testing/enums.service.mock';
import { MimPreferencesServiceMock } from '../../testing/mim-preferences.service.mock';
import { typesServiceMock } from '../../testing/types.service.mock';
import { EnumsService } from '../../services/http/enums.service';
import { MimPreferencesService } from '../../services/http/mim-preferences.service';
import { TypesService } from '../../services/http/types.service';
import { ApplicabilityListUIService } from '../../services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../services/ui/enumeration-ui.service';
import { MockEnumSetFormUnique } from '../../forms/enum-set-form/enum-set-form.component.mock';
import { MockLogicalTypeSelectorComponent } from '../../testing/logical-type-selector.component.mock';
import { MockNewPlatformTypeFormPage2Component } from '../../testing/new-platform-type-form-page2.component.mock';

import { NewTypeFormComponent } from './new-type-form.component';

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
						MockEnumSetFormUnique,
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
