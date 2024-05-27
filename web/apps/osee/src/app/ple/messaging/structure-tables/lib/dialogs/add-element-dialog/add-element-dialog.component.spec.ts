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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
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

import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { applicabilitySentinel } from '@osee/applicability/types';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	EnumerationSetService,
	MimPreferencesService,
	TypesService,
} from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	enumerationSetServiceMock,
	MimPreferencesServiceMock,
	MockNewTypeFormComponent,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { ElementDialog } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { ApplicabilityListService } from '@osee/shared/services';
import { applicabilityListServiceMock } from '@osee/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';
import { MockElementFormComponent } from '../../forms/element-form/element-form.component.mock';

describe('AddElementDialogComponent', () => {
	let component: AddElementDialogComponent;
	let fixture: ComponentFixture<AddElementDialogComponent>;
	const dialogData: ElementDialog = {
		id: '12345',
		name: 'structure',
		type: new PlatformTypeSentinel(),
		element: {
			id: '0' as `${number}`,
			gammaId: '1' as `${number}`,
			name: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847088',
				gammaId: '-1' as `${number}`,
				value: 'name',
			},
			description: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847090',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			notes: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847085',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			interfaceElementAlterable: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225788',
				gammaId: '-1' as `${number}`,
				value: true,
			},
			interfaceElementBlockData: {
				id: '-1' as `${number}`,
				typeId: '1523923981411079299',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementArrayHeader: {
				id: '-1' as `${number}`,
				typeId: '3313203088521964923',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementWriteArrayHeaderName: {
				id: '-1' as `${number}`,
				typeId: '3313203088521964924',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementIndexEnd: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225802',
				gammaId: '-1' as `${number}`,
				value: 1,
			},
			interfaceElementIndexStart: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225801',
				gammaId: '-1' as `${number}`,
				value: 0,
			},
			interfaceDefaultValue: {
				id: '-1' as `${number}`,
				typeId: '2886273464685805413',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			interfaceElementArrayIndexOrder: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472581',
				gammaId: '-1' as `${number}`,
				value: 'OUTER_INNER',
			},
			interfaceElementArrayIndexDelimiterOne: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472582',
				gammaId: '-1' as `${number}`,
				value: ' ',
			},
			interfaceElementArrayIndexDelimiterTwo: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472583',
				gammaId: '-1' as `${number}`,
				value: ' ',
			},
			enumLiteral: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225803',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			applicability: applicabilitySentinel,
			platformType: new PlatformTypeSentinel(),
			arrayElements: [],
		},
		startingElement: {
			id: '0' as `${number}`,
			gammaId: '1' as `${number}`,
			name: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847088',
				gammaId: '-1' as `${number}`,
				value: 'name',
			},
			description: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847090',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			notes: {
				id: '-1' as `${number}`,
				typeId: '1152921504606847085',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			interfaceElementAlterable: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225788',
				gammaId: '-1' as `${number}`,
				value: true,
			},
			interfaceElementBlockData: {
				id: '-1' as `${number}`,
				typeId: '1523923981411079299',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementArrayHeader: {
				id: '-1' as `${number}`,
				typeId: '3313203088521964923',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementWriteArrayHeaderName: {
				id: '-1' as `${number}`,
				typeId: '3313203088521964924',
				gammaId: '-1' as `${number}`,
				value: false,
			},
			interfaceElementIndexEnd: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225802',
				gammaId: '-1' as `${number}`,
				value: 1,
			},
			interfaceElementIndexStart: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225801',
				gammaId: '-1' as `${number}`,
				value: 0,
			},
			interfaceDefaultValue: {
				id: '-1' as `${number}`,
				typeId: '2886273464685805413',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			interfaceElementArrayIndexOrder: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472581',
				gammaId: '-1' as `${number}`,
				value: 'OUTER_INNER',
			},
			interfaceElementArrayIndexDelimiterOne: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472582',
				gammaId: '-1' as `${number}`,
				value: ' ',
			},
			interfaceElementArrayIndexDelimiterTwo: {
				id: '-1' as `${number}`,
				typeId: '6818939106523472583',
				gammaId: '-1' as `${number}`,
				value: ' ',
			},
			enumLiteral: {
				id: '-1' as `${number}`,
				typeId: '2455059983007225803',
				gammaId: '-1' as `${number}`,
				value: '',
			},
			applicability: applicabilitySentinel,
			platformType: new PlatformTypeSentinel(),
			arrayElements: [],
		},
		mode: 'add',
		allowArray: true,
		arrayChild: false,
		createdTypes: [],
	};
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(AddElementDialogComponent, {
			add: {
				imports: [
					MockNewTypeFormComponent,
					MockMatOptionLoadingComponent,
					MockApplicabilityDropdownComponent,
					MockElementFormComponent,
				],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			},
			remove: {
				imports: [
					NewTypeFormComponent,
					MatOptionLoadingComponent,
					ApplicabilityDropdownComponent,
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
