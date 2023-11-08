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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { AddElementDialogComponent } from './add-element-dialog.component';

import { UserDataAccountService } from '@osee/auth';
import {
	MockApplicabilitySelectorComponent,
	MockMatOptionLoadingComponent,
} from '@osee/shared/components/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';
import {
	MockNewTypeFormComponent,
	MimPreferencesServiceMock,
	typesServiceMock,
	enumsServiceMock,
	enumerationSetServiceMock,
	CurrentStateServiceMock,
} from '@osee/messaging/shared/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	MimPreferencesService,
	TypesService,
	EnumsService,
	EnumerationSetService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { ApplicabilityListService } from '@osee/shared/services';
import { applicabilityListServiceMock } from '@osee/shared/testing';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { ElementDialog } from '@osee/messaging/shared/types';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	ApplicabilitySelectorComponent,
	MatOptionLoadingComponent,
} from '@osee/shared/components';
import { MockElementFormComponent } from '../../forms/element-form/element-form.component.mock';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';

describe('AddElementDialogComponent', () => {
	let component: AddElementDialogComponent;
	let fixture: ComponentFixture<AddElementDialogComponent>;
	let dialogData: ElementDialog = {
		id: '12345',
		name: 'structure',
		type: {
			id: '',
			name: '',
			description: '',
			interfaceLogicalType: '',
			interfacePlatformType2sComplement: false,
			interfacePlatformTypeAnalogAccuracy: '',
			interfacePlatformTypeBitSize: '',
			interfacePlatformTypeBitsResolution: '',
			interfacePlatformTypeCompRate: '',
			interfaceDefaultValue: '',
			interfacePlatformTypeMaxval: '',
			interfacePlatformTypeMinval: '',
			interfacePlatformTypeMsbValue: '',
			interfacePlatformTypeUnits: '',
			interfacePlatformTypeValidRangeDescription: '',
			applicability: {
				id: '1',
				name: 'Base',
			},
			enumSet: {
				id: '-1',
				name: '',
				description: '',
				enumerations: [],
				applicability: {
					id: '1',
					name: 'Base',
				},
			},
		},
		element: {
			id: '-1',
			name: '',
			description: '',
			notes: '',
			interfaceElementAlterable: true,
			interfaceElementIndexEnd: 0,
			interfaceElementIndexStart: 0,
			interfaceDefaultValue: '',
			enumLiteral: '',
			units: '',
			platformType: new PlatformTypeSentinel(),
		},
		mode: 'add',
		allowArray: true,
	};
	let loader: HarnessLoader;
	let nestedDialog: DebugElement;

	beforeEach(async () => {
		await TestBed.overrideComponent(AddElementDialogComponent, {
			add: {
				imports: [
					MockNewTypeFormComponent,
					MockMatOptionLoadingComponent,
					MockApplicabilitySelectorComponent,
					MockElementFormComponent,
				],
			},
			remove: {
				imports: [
					NewTypeFormComponent,
					MatOptionLoadingComponent,
					ApplicabilitySelectorComponent,
					ElementFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					NoopAnimationsModule,
					MatStepperModule,
					MatDialogModule,
					MatButtonModule,
					FormsModule,
					MatFormFieldModule,
					MatSelectModule,
					MatInputModule,
					MatSlideToggleModule,
					MatIconModule,
					MatDividerModule,
					MatProgressSpinnerModule,
				],
				providers: [
					{
						provide: MatDialogRef,
						useValue: {},
					},
					{
						provide: MAT_DIALOG_DATA,
						useValue: dialogData,
					},
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
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
					},
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddElementDialogComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Page 2', () => {
		beforeEach(async () => {
			const createNewBtn = await loader.getHarness(
				MatButtonHarness.with({ text: 'Create new Element' })
			);
			await createNewBtn.click();
		});
	});
});
