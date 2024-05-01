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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { CurrentTypesService } from './lib/services/current-types.service';

import { provideRouter } from '@angular/router';
import { MessagingControlsMockComponent } from '@osee/messaging/shared/testing';
import { MockTypesInterfaceComponent } from './lib/types-interface/types-interface.component.mock';
import { TypesPageComponent } from './types-page.component';

describe('TypesInterfaceComponent', () => {
	let component: TypesPageComponent;
	let fixture: ComponentFixture<TypesPageComponent>;
	let typesService: Partial<CurrentTypesService> = {
		typeData: of([
			{
				id: '1',
				interfaceLogicalType: 'boolean',
				description: '',
				interfacePlatformType2sComplement: false,
				interfacePlatformTypeAnalogAccuracy: 'Hello',
				interfacePlatformTypeBitsResolution: '1',
				interfacePlatformTypeBitSize: '8',
				interfacePlatformTypeCompRate: '1',
				interfaceDefaultValue: '1',
				interfacePlatformTypeEnumLiteral: 'Enum Lit.',
				interfacePlatformTypeMaxval: '1',
				interfacePlatformTypeMinval: '0',
				interfacePlatformTypeMsbValue: '1',
				interfacePlatformTypeUnits: 'N/A',
				interfacePlatformTypeValidRangeDescription: 'Description',
				name: 'boolean',
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
			{
				id: '2',
				interfaceLogicalType: 'integer',
				description: '',
				interfacePlatformType2sComplement: false,
				interfacePlatformTypeAnalogAccuracy: 'Hello',
				interfacePlatformTypeBitsResolution: '1',
				interfacePlatformTypeBitSize: '8',
				interfacePlatformTypeCompRate: '1',
				interfaceDefaultValue: '1',
				interfacePlatformTypeEnumLiteral: 'Enum Lit.',
				interfacePlatformTypeMaxval: '1',
				interfacePlatformTypeMinval: '0',
				interfacePlatformTypeMsbValue: '1',
				interfacePlatformTypeUnits: 'N/A',
				interfacePlatformTypeValidRangeDescription: 'Description',
				name: 'integer',
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
		]),
	};

	beforeEach(async () => {
		await TestBed.overrideComponent(TypesPageComponent, {
			set: {
				imports: [
					MessagingControlsMockComponent,
					MockTypesInterfaceComponent,
				],
				providers: [
					{ provide: CurrentTypesService, useValue: typesService },
				],
			},
		})
			.configureTestingModule({
				imports: [TypesPageComponent],
				declarations: [],
				providers: [provideNoopAnimations(), provideRouter([])],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TypesPageComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
