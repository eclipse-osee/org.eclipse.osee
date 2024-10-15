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
import { transactionMock } from '@osee/transactions/testing';
import { of } from 'rxjs';
import { TypeGridComponent } from '../type-grid/type-grid.component';
import { MockTypeGridComponent } from '../testing/type-grid.component.mock';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

describe('TypesInterfaceComponent', () => {
	let component: TypesInterfaceComponent;
	let fixture: ComponentFixture<TypesInterfaceComponent>;

	beforeEach(async () => {
		const typeData = of<PlatformType[]>([
			{
				id: '1' as `${number}`,
				gammaId: '1' as `${number}`,
				interfaceLogicalType: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.LOGICALTYPE,
					gammaId: '-1' as `${number}`,
					value: 'boolean',
				},
				description: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
					gammaId: '-1' as `${number}`,
					value: '',
				},
				interfacePlatformType2sComplement: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT,
					gammaId: '-1' as `${number}`,
					value: false,
				},
				interfacePlatformTypeAnalogAccuracy: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
					gammaId: '-1' as `${number}`,
					value: 'Hello',
				},
				interfacePlatformTypeBitsResolution: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeBitSize: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
					gammaId: '-1' as `${number}`,
					value: '8',
				},
				interfacePlatformTypeCompRate: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfaceDefaultValue: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeMaxval: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeMinval: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
					gammaId: '-1' as `${number}`,
					value: '0',
				},
				interfacePlatformTypeMsbValue: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeUnits: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
					gammaId: '-1' as `${number}`,
					value: 'N/A',
				},
				interfacePlatformTypeValidRangeDescription: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION,
					gammaId: '-1' as `${number}`,
					value: 'Description',
				},
				name: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.NAME,
					gammaId: '-1' as `${number}`,
					value: 'boolean',
				},
				applicability: {
					id: '1',
					name: 'Base',
				},
				enumSet: {
					id: '-1' as `${number}`,
					gammaId: '-1' as `${number}`,
					name: {
						id: '-1' as `${number}`,
						typeId: ATTRIBUTETYPEIDENUM.NAME,
						gammaId: '-1' as `${number}`,
						value: '',
					},
					description: {
						id: '-1' as `${number}`,
						typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
						gammaId: '-1' as `${number}`,
						value: '',
					},
					enumerations: [],
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			},
			{
				id: '2' as `${number}`,
				gammaId: '2' as `${number}`,
				interfaceLogicalType: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.LOGICALTYPE,
					gammaId: '-1' as `${number}`,
					value: 'integer',
				},
				description: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
					gammaId: '-1' as `${number}`,
					value: '',
				},
				interfacePlatformType2sComplement: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPE2SCOMPLEMENT,
					gammaId: '-1' as `${number}`,
					value: false,
				},
				interfacePlatformTypeAnalogAccuracy: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEANALOGACCURACY,
					gammaId: '-1' as `${number}`,
					value: 'Hello',
				},
				interfacePlatformTypeBitsResolution: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSRESOLUTION,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeBitSize: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEBITSIZE,
					gammaId: '-1' as `${number}`,
					value: '8',
				},
				interfacePlatformTypeCompRate: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPECOMPRATE,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfaceDefaultValue: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeMaxval: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMAXVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeMinval: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMINVAL,
					gammaId: '-1' as `${number}`,
					value: '0',
				},
				interfacePlatformTypeMsbValue: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEMSBVAL,
					gammaId: '-1' as `${number}`,
					value: '1',
				},
				interfacePlatformTypeUnits: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS,
					gammaId: '-1' as `${number}`,
					value: 'N/A',
				},
				interfacePlatformTypeValidRangeDescription: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEVALIDRANGEDESCRIPTION,
					gammaId: '-1' as `${number}`,
					value: 'Description',
				},
				name: {
					id: '-1' as `${number}`,
					typeId: ATTRIBUTETYPEIDENUM.NAME,
					gammaId: '-1' as `${number}`,
					value: 'integer',
				},
				applicability: {
					id: '1',
					name: 'Base',
				},
				enumSet: {
					id: '-1' as `${number}`,
					gammaId: '-1' as `${number}`,
					name: {
						id: '-1' as `${number}`,
						typeId: ATTRIBUTETYPEIDENUM.NAME,
						gammaId: '-1' as `${number}`,
						value: '',
					},
					description: {
						id: '-1' as `${number}`,
						typeId: ATTRIBUTETYPEIDENUM.DESCRIPTION,
						gammaId: '-1' as `${number}`,
						value: '',
					},
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
							updatePreferences(
								_preferences: settingsDialogData
							) {
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
