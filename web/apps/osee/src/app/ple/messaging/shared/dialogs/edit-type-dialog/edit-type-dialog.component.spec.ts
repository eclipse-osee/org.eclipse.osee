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
import { OverlayContainer } from '@angular/cdk/overlay';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { AsyncPipe, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import { MockUnitDropdownComponent } from '@osee/messaging/shared/dropdowns/testing';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/enumerations';
import { QueryService, TypesService } from '@osee/messaging/shared/services';
import {
	MockCrossReferenceDropdownComponent,
	MockEditEnumSetFieldComponent,
	MockUniquePlatformTypeAttributesDirective,
	MockUniquePlatformTypeNameDirective,
	QueryServiceMock,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import type { editPlatformTypeDialogData } from '@osee/messaging/shared/types';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { EditTypeDialogComponent } from './edit-type-dialog.component';

let loader: HarnessLoader;

describe('EditTypeDialogComponent', () => {
	let component: EditTypeDialogComponent;
	let fixture: ComponentFixture<EditTypeDialogComponent>;
	let overlayContainer: OverlayContainer;
	const matDialogData: editPlatformTypeDialogData = {
		mode: editPlatformTypeDialogDataMode.edit,
		type: {
			id: '1',
			gammaId: '-1',
			interfaceLogicalType: {
				id: '-1',
				typeId: '2455059983007225762',
				gammaId: '-1',
				value: 'boolean',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformType2sComplement: {
				id: '-1',
				typeId: '3899709087455064784',
				gammaId: '-1',
				value: false,
			},
			interfacePlatformTypeAnalogAccuracy: {
				id: '-1',
				typeId: '3899709087455064788',
				gammaId: '-1',
				value: 'N/A',
			},
			interfacePlatformTypeBitsResolution: {
				id: '-1',
				typeId: '3899709087455064786',
				gammaId: '-1',
				value: 'N/A',
			},
			interfacePlatformTypeCompRate: {
				id: '-1',
				typeId: '3899709087455064787',
				gammaId: '-1',
				value: '50Hz',
			},
			interfacePlatformTypeBitSize: {
				id: '-1',
				typeId: '2455059983007225786',
				gammaId: '-1',
				value: '8',
			},
			interfaceDefaultValue: {
				id: '-1',
				typeId: '2886273464685805413',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeMinval: {
				id: '-1',
				typeId: '3899709087455064782',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeMsbValue: {
				id: '-1',
				typeId: '3899709087455064785',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeMaxval: {
				id: '-1',
				typeId: '3899709087455064783',
				gammaId: '-1',
				value: '1',
			},
			interfacePlatformTypeUnits: {
				id: '-1',
				typeId: '4026643196432874344',
				gammaId: '-1',
				value: 'N/A',
			},
			interfacePlatformTypeValidRangeDescription: {
				id: '-1',
				typeId: '2121416901992068417',
				gammaId: '-1',
				value: '',
			},
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Boolean',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			enumSet: {
				id: '-1',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: '',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
				enumerations: [],
				applicability: { id: '1', name: 'Base' },
			},
		},
	};

	beforeEach(async () => {
		await TestBed.overrideComponent(EditTypeDialogComponent, {
			set: {
				imports: [
					NgIf,
					FormsModule,
					AsyncPipe,
					MatDialogModule,
					MatStepperModule,
					MatFormFieldModule,
					MatInputModule,
					MatSelectModule,
					MatOptionModule,
					A11yModule,
					MatButtonModule,
					MockMatOptionLoadingComponent,
					MockEditEnumSetFieldComponent,
					MockUniquePlatformTypeAttributesDirective,
					MockUniquePlatformTypeNameDirective,
					MockApplicabilityDropdownComponent,
					MockUnitDropdownComponent,
					MockCrossReferenceDropdownComponent,
				],
				providers: [
					{ provide: QueryService, useValue: QueryServiceMock },
					{ provide: MatDialogRef, useValue: {} },
					{ provide: MAT_DIALOG_DATA, useValue: matDialogData },
					{ provide: TypesService, useValue: typesServiceMock },
				],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule],
				declarations: [],
				providers: [
					{ provide: QueryService, useValue: QueryServiceMock },
					{ provide: MatDialogRef, useValue: {} },
					{ provide: MAT_DIALOG_DATA, useValue: matDialogData },
					{ provide: TypesService, useValue: typesServiceMock },
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditTypeDialogComponent);
		loader = TestbedHarnessEnvironment.loader(fixture);
		overlayContainer = TestBed.inject(OverlayContainer);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	afterEach(async () => {
		const dialogs = loader.getAllHarnesses(MatDialogHarness);
		await Promise.all(
			(await dialogs).map(async (d: MatDialogHarness) => await d.close())
		);
		overlayContainer.ngOnDestroy();
	});
	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
