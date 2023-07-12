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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { ElementDialog } from '@osee/messaging/shared/types';
import { MockElementFormComponent } from '../../forms/element-form/element-form.component.mock';

import { EditElementDialogComponent } from './edit-element-dialog.component';

describe('EditElementDialogComponent', () => {
	let component: EditElementDialogComponent;
	let fixture: ComponentFixture<EditElementDialogComponent>;
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
	};
	beforeEach(async () => {
		await TestBed.overrideComponent(EditElementDialogComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatButtonModule,
					MockElementFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule],
				providers: [
					{
						provide: MatDialogRef,
						useValue: {},
					},
					{
						provide: MAT_DIALOG_DATA,
						useValue: dialogData,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(EditElementDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
