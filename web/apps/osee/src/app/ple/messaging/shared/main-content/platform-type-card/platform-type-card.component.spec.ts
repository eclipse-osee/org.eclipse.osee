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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardHarness } from '@angular/material/card/testing';
import { MatDialogModule } from '@angular/material/dialog';

import { NO_ERRORS_SCHEMA } from '@angular/compiler';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import {
	EditEnumSetDialogComponent,
	EditTypeDialogComponent,
} from '@osee/messaging/shared/dialogs';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import {
	ConnectionService,
	CrossReferenceHttpService,
	EnumerationSetService,
	MimPreferencesService,
	PreferencesUIService,
	QueryService,
	TypesService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	CrossReferenceHttpServiceMock,
	MimPreferencesServiceMock,
	MockEditEnumSetDialogComponent,
	MockEditEnumSetFieldComponent,
	MockEditTypeDialogComponent,
	QueryServiceMock,
	connectionServiceMock,
	enumerationSetServiceMock,
	preferencesUiServiceMock,
	typesServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { ApplicabilityListService } from '@osee/shared/services';
import { applicabilityListServiceMock } from '@osee/shared/testing';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { transactionBuilderMock } from '@osee/shared/transactions-legacy/testing';
import { PlatformTypeCardComponent } from './platform-type-card.component';
import { PlatformType } from '@osee/messaging/shared/types';
import { UnitsService } from '@osee/messaging/units/services';
import { unitsServiceMock } from '@osee/messaging/units/services/testing';
import { PlatformTypeActionsComponent } from '../platform-type-actions/platform-type-actions.component';
import { MockPlatformTypeActionsComponent } from '@osee/messaging/shared/main-content/testing';

let loader: HarnessLoader;

describe('PlatformTypeCardComponent', () => {
	let component: PlatformTypeCardComponent;
	let fixture: ComponentFixture<PlatformTypeCardComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(EditEnumSetDialogComponent, {
			remove: {
				imports: [EditEnumSetFieldComponent],
			},
			add: {
				imports: [MockEditEnumSetFieldComponent],
			},
		})
			.overrideComponent(PlatformTypeCardComponent, {
				remove: {
					imports: [
						EditTypeDialogComponent,
						EditEnumSetDialogComponent,
						PlatformTypeActionsComponent,
					],
				},
				add: {
					imports: [
						MockEditTypeDialogComponent,
						MockEditEnumSetDialogComponent,
						MockPlatformTypeActionsComponent,
					],
					providers: [
						{
							provide: WarningDialogService,
							useValue: warningDialogServiceMock,
						},
						{
							provide: UnitsService,
							useValue: unitsServiceMock,
						},
						{
							provide: CrossReferenceHttpService,
							useValue: CrossReferenceHttpServiceMock,
						},
						{
							provide: ConnectionService,
							useValue: connectionServiceMock,
						},
						{
							provide: PreferencesUIService,
							useValue: preferencesUiServiceMock,
						},
					],
				},
			})
			.configureTestingModule({
				imports: [
					MatDialogModule,
					MockEditTypeDialogComponent,
					MockEditEnumSetDialogComponent,
					PlatformTypeCardComponent,
				],
				declarations: [],
				providers: [
					provideNoopAnimations(),
					{ provide: QueryService, useValue: QueryServiceMock },
					{ provide: TypesService, useValue: typesServiceMock },
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
					{
						provide: TransactionBuilderService,
						useValue: transactionBuilderMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
					{
						provide: UnitsService,
						useValue: unitsServiceMock,
					},
					{
						provide: CrossReferenceHttpService,
						useValue: CrossReferenceHttpServiceMock,
					},
					{
						provide: ConnectionService,
						useValue: connectionServiceMock,
					},
					{
						provide: PreferencesUIService,
						useValue: preferencesUiServiceMock,
					},
				],
				schemas: [NO_ERRORS_SCHEMA], //weirdness with standalone component + dialog
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PlatformTypeCardComponent);
		loader = TestbedHarnessEnvironment.loader(fixture);
		component = fixture.componentInstance;
		const expectedData: PlatformType = {
			id: '0',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Random enumeration',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceLogicalType: {
				id: '-1',
				typeId: '2455059983007225762',
				gammaId: '-1',
				value: 'enumeration',
			},
			interfacePlatformTypeMinval: {
				id: '-1',
				typeId: '3899709087455064782',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeMaxval: {
				id: '-1',
				typeId: '3899709087455064783',
				gammaId: '-1',
				value: '1',
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
			interfacePlatformTypeMsbValue: {
				id: '-1',
				typeId: '3899709087455064785',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeBitsResolution: {
				id: '-1',
				typeId: '3899709087455064786',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeCompRate: {
				id: '-1',
				typeId: '3899709087455064787',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformTypeAnalogAccuracy: {
				id: '-1',
				typeId: '3899709087455064788',
				gammaId: '-1',
				value: '0',
			},
			interfacePlatformType2sComplement: {
				id: '-1',
				typeId: '3899709087455064784',
				gammaId: '-1',
				value: false,
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
				value: 'N/A',
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
		};
		component.typeData = expectedData;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should create a header of class mat-card-header-text with text of Random Boolean', async () => {
		fixture.detectChanges();
		const card = await loader.getHarness(MatCardHarness);
		expect(await card.getTitleText()).toEqual('Random enumeration');
	});
	it('should create a subtitle with text of Boolean', async () => {
		fixture.detectChanges();
		const card = await loader.getHarness(MatCardHarness);
		expect(await card.getSubtitleText()).toEqual('enumeration');
	});

	xit('should contain text that has minimum value, maximum value, byte size, default value, msb value, resolution, comp rate, analog accuracy, edit and Create New Type From Base', async () => {
		fixture.detectChanges();
		const card = await loader.getHarness(MatCardHarness);
		expect(await card.getText()).toContain('Random enumeration');
		expect(await card.getText()).toContain('enumeration');
		expect(await card.getText()).toContain('Minimum Value: 0');
		expect(await card.getText()).toContain('Maximum Value: 1');
		expect(await card.getText()).toContain('Bit Size: 8');
		expect(await card.getText()).toContain('Comp Rate: 0');
		expect(await card.getText()).toContain('Default Value: 0');
		expect(await card.getText()).toContain('MSB Value: 0');
		expect(await card.getText()).toContain('Resolution: 0');
		expect(await card.getText()).toContain('Analog Accuracy: 0');
		//expect(await card.getText()).toContain('Edit')
		expect(await card.getText()).toContain('Create New Type From Base');
		expect(await card.getText()).toContain(
			'View Related Enumeration Set Attributes'
		);
	});

	//re-enable if we re-activate platform type editing
	// it('should open dialog and create an edit of an existing type', async() => {
	//   const openDialog = spyOn(component, 'openDialog').and.callThrough();
	//   let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of<editPlatformTypeDialogData>({mode:editPlatformTypeDialogDataMode.edit,type:{name:'',description:'',interfaceLogicalType:'',interfacePlatform2sComplement:false,interfacePlatformTypeAnalogAccuracy:'',interfacePlatformTypeBitSize:'0',interfacePlatformTypeBitsResolution:'',interfacePlatformTypeCompRate:'',interfaceDefaultValue:'0',interfacePlatformTypeEnumLiteral:'',interfacePlatformTypeMaxval:'',interfacePlatformTypeMinval:'',interfacePlatformTypeMsbValue:'',interfacePlatformTypeUnits:'',interfacePlatformTypeValidRangeDescription:''}}), close: null });
	//   let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy)
	//   const button = await (await loader.getHarness(MatCardHarness)).getHarness(MatButtonHarness.with({ text: new RegExp("Edit") }));
	//   await button.click();
	//   expect(openDialog).toHaveBeenCalledWith(editPlatformTypeDialogDataMode.edit);
	// })
});
