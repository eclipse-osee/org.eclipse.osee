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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatDialogHarness } from '@angular/material/dialog/testing';
import { OverlayContainer } from '@angular/cdk/overlay';
import { EditTypeDialogComponent } from './edit-type-dialog.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { A11yModule } from '@angular/cdk/a11y';
import { NgIf, AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MockEditEnumSetFieldComponent,
	MockUniquePlatformTypeAttributesDirective,
	typesServiceMock,
	enumsServiceMock,
	QueryServiceMock,
} from '@osee/messaging/shared/testing';
import type { editPlatformTypeDialogData } from '@osee/messaging/shared/types';
import { editPlatformTypeDialogDataMode } from '@osee/messaging/shared/types';
import {
	QueryService,
	TypesService,
	EnumsService,
} from '@osee/messaging/shared/services';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';

let loader: HarnessLoader;

describe('EditTypeDialogComponent', () => {
	let component: EditTypeDialogComponent;
	let fixture: ComponentFixture<EditTypeDialogComponent>;
	let overlayContainer: OverlayContainer;
	let matDialogData: editPlatformTypeDialogData = {
		mode: editPlatformTypeDialogDataMode.edit,
		type: {
			interfaceLogicalType: 'boolean',
			description: '',
			interfacePlatform2sComplement: false,
			interfacePlatformTypeAnalogAccuracy: 'N/A',
			interfacePlatformTypeBitsResolution: 'N/A',
			interfacePlatformTypeCompRate: '50Hz',
			interfacePlatformTypeBitSize: '8',
			interfaceDefaultValue: '0',
			interfacePlatformTypeMinval: '0',
			interfacePlatformTypeMsbValue: '0',
			interfacePlatformTypeMaxval: '1',
			interfacePlatformTypeUnits: 'N/A',
			interfacePlatformTypeValidRangeDescription: '',
			name: 'FACE Boolean',
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
				],
				providers: [
					{ provide: QueryService, useValue: QueryServiceMock },
					{ provide: MatDialogRef, useValue: {} },
					{ provide: MAT_DIALOG_DATA, useValue: matDialogData },
					{ provide: TypesService, useValue: typesServiceMock },
					{ provide: EnumsService, useValue: enumsServiceMock },
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
					{ provide: EnumsService, useValue: enumsServiceMock },
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
