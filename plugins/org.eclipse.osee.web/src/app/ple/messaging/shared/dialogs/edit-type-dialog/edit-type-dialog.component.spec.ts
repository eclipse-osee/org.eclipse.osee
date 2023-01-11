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
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { MatSelectModule } from '@angular/material/select';
import { TypesService } from '../../services/http/types.service';
import { typesServiceMock } from '../../testing/types.service.mock';
import { EnumsService } from '../../services/http/enums.service';
import { enumsServiceMock } from '../../testing/enums.service.mock';
import { MatStepperModule } from '@angular/material/stepper';
import { MockUniquePlatformTypeAttributesDirective } from '../../testing/unique-platform-type-attributes.directive.mock';
import { QueryServiceMock } from '../../testing/query.service.mock';
import { QueryService } from '../../services/http/query.service';
import { MockMatOptionLoadingComponent } from '../../../../../shared-components/mat-option-loading/testing/mat-option-loading.component';
import { MockEditEnumSetFieldComponent } from '../../testing/edit-enum-set-field.component.mock';
import { A11yModule } from '@angular/cdk/a11y';
import { NgIf, AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';

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
			interfacePlatformTypeDefaultValue: '0',
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
