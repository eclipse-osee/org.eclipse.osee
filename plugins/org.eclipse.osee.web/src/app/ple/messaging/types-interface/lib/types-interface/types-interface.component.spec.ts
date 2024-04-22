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

import { TypesInterfaceComponent } from './types-interface.component';
import { CurrentTypesService } from '../services/current-types.service';
import { PlatformType, settingsDialogData } from '@osee/messaging/shared/types';
import { transactionMock } from '@osee/shared/transactions/testing';
import { Observable, of } from 'rxjs';
import { TypeGridComponent } from '../type-grid/type-grid.component';
import { MockTypeGridComponent } from '../testing/type-grid.component.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('TypesInterfaceComponent', () => {
	let component: TypesInterfaceComponent;
	let fixture: ComponentFixture<TypesInterfaceComponent>;

	beforeEach(async () => {
		const typeData: Observable<PlatformType[]> = of([
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
		]);
		await TestBed.overrideComponent(TypesInterfaceComponent, {
			remove: {
				imports: [TypeGridComponent],
				providers: [CurrentTypesService],
			},
			add: {
				imports: [MockTypeGridComponent],
				providers: [
					{
						provide: CurrentTypesService,
						useValue: {
							typeData: typeData,
							typeDataCount: of(10),
							currentPage: of(0),
							currentPageSize: of(10),
							inEditMode: of(true),
							updatePreferences(preferences: settingsDialogData) {
								return of(transactionMock);
							},
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [TypesInterfaceComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(TypesInterfaceComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
