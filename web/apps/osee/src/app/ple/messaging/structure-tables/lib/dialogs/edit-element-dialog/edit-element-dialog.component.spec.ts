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
import { ElementFormComponent } from '../../forms/element-form/element-form.component';

describe('EditElementDialogComponent', () => {
	let component: EditElementDialogComponent;
	let fixture: ComponentFixture<EditElementDialogComponent>;
	const dialogData: ElementDialog = {
		id: '12345',
		name: 'structure',
		type: new PlatformTypeSentinel(),
		//@ts-expect-error: this is invalid due to array platform type
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
			platformType: new PlatformTypeSentinel(),
			arrayElements: [],
		},
		mode: 'edit',
		allowArray: false,
		arrayChild: false,
	};
	beforeEach(async () => {
		await TestBed.overrideComponent(EditElementDialogComponent, {
			add: {
				imports: [MockElementFormComponent],
			},
			remove: {
				imports: [ElementFormComponent],
			},
		})
			.configureTestingModule({
				imports: [
					NoopAnimationsModule,
					MatDialogModule,
					MatButtonModule,
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
