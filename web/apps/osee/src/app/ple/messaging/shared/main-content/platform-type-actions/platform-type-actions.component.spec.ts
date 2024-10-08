/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	EnumerationUIService,
	PreferencesUIService,
	TypesUIService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	enumerationUiServiceMock,
	preferencesUiServiceMock,
	typesUIServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { PlatformTypeActionsComponent } from './platform-type-actions.component';

describe('PlatformTypeActionsComponent', () => {
	let component: PlatformTypeActionsComponent;
	let fixture: ComponentFixture<PlatformTypeActionsComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(PlatformTypeActionsComponent, {
			add: {
				providers: [
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
			},
		})
			.configureTestingModule({
				imports: [PlatformTypeActionsComponent],
				providers: [
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
					{ provide: TypesUIService, useValue: typesUIServiceMock },
					{
						provide: EnumerationUIService,
						useValue: enumerationUiServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(PlatformTypeActionsComponent);
		fixture.componentRef.setInput('typeData', {
			id: '0',
			name: 'Random enumeration',
			description: '',
			interfaceLogicalType: 'enumeration',
			interfacePlatformTypeMinval: '0',
			interfacePlatformTypeMaxval: '1',
			interfacePlatformTypeBitSize: '8',
			interfaceDefaultValue: '0',
			interfacePlatformTypeMsbValue: '0',
			interfacePlatformTypeBitsResolution: '0',
			interfacePlatformTypeCompRate: '0',
			interfacePlatformTypeAnalogAccuracy: '0',
			interfacePlatformType2sComplement: false,
			interfacePlatformTypeEnumLiteral: 'A string',
			interfacePlatformTypeUnits: 'N/A',
			interfacePlatformTypeValidRangeDescription: 'N/A',
			applicability: {
				id: '1',
				name: 'Base',
			},
			enumSet: {
				id: '-1',
				name: '',
				description: '',
				applicability: { id: '1', name: 'Base' },
			},
		});
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
