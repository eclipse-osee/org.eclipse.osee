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
import { HarnessLoader } from '@angular/cdk/testing';
import { MatCardHarness } from '@angular/material/card/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { PlatformTypeCardComponent } from './platform-type-card.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { of } from 'rxjs';
import { enumerationSet } from '../../types/enum';
import { editPlatformTypeDialogData } from '../../types/editPlatformTypeDialogData';
import { MimPreferencesService } from '../../services/http/mim-preferences.service';
import { MimPreferencesServiceMock } from '../../testing/mim-preferences.service.mock';
import { enumerationSetServiceMock } from '../../testing/enumeration-set.service.mock';
import { EnumerationSetService } from '../../services/http/enumeration-set.service';
import { TypesService } from '../../services/http/types.service';
import { typesServiceMock } from '../../testing/types.service.mock';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { transactionBuilderMock } from '../../../../../transactions/transaction-builder.service.mock';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { applicabilityListServiceMock } from '../../testing/applicability-list.service.mock';
import { enumsServiceMock } from '../../testing/enums.service.mock';
import { ApplicabilityListService } from '../../services/http/applicability-list.service';
import { EnumsService } from '../../services/http/enums.service';
import { userDataAccountServiceMock } from '../../../../../userdata/services/user-data-account.service.mock';
import { MockEditTypeDialogComponent } from '../../testing/edit-type-dialog.component.mock';
import { MockEditEnumSetDialogComponent } from '../../testing/edit-enum-set-dialog.component.mock';
import { QueryServiceMock } from '../../testing/query.service.mock';
import { QueryService } from '../../services/http/query.service';
import { EditTypeDialogComponent } from '../../dialogs/edit-type-dialog/edit-type-dialog.component';
import { EditEnumSetDialogComponent } from '../../dialogs/edit-enum-set-dialog/edit-enum-set-dialog.component';
import { NO_ERRORS_SCHEMA } from '@angular/compiler';
import { EditEnumSetFieldComponent } from '../../forms/edit-enum-set-field/edit-enum-set-field.component';
import { MockEditEnumSetFieldComponent } from '../../testing/edit-enum-set-field.component.mock';

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
					],
				},
				add: {
					imports: [
						MockEditTypeDialogComponent,
						MockEditEnumSetDialogComponent,
					],
				},
			})
			.configureTestingModule({
				imports: [
					NoopAnimationsModule,
					MatDialogModule,
					MockEditTypeDialogComponent,
					MockEditEnumSetDialogComponent,
					PlatformTypeCardComponent,
				],
				declarations: [],
				providers: [
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
					{ provide: EnumsService, useValue: enumsServiceMock },
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
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
		const expectedData = {
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
			interfacePlatform2sComplement: false,
			interfacePlatformTypeEnumLiteral: 'A string',
			interfacePlatformTypeUnits: 'N/A',
			interfacePlatformTypeValidRangeDescription: 'N/A',
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

	it('should contain text that has minimum value, maximum value, byte size, default value, msb value, resolution, comp rate, analog accuracy, edit and Create New Type From Base', async () => {
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

	it('should open dialog and create a copy of an existing type', async () => {
		const openDialog = spyOn(component, 'openDialog').and.callThrough();
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of<editPlatformTypeDialogData>({
				mode: editPlatformTypeDialogDataMode.copy,
				type: {
					name: '',
					description: '',
					interfaceLogicalType: '',
					interfacePlatform2sComplement: false,
					interfacePlatformTypeAnalogAccuracy: '',
					interfacePlatformTypeBitSize: '0',
					interfacePlatformTypeBitsResolution: '',
					interfacePlatformTypeCompRate: '',
					interfaceDefaultValue: '0',
					interfacePlatformTypeMaxval: '',
					interfacePlatformTypeMinval: '',
					interfacePlatformTypeMsbValue: '',
					interfacePlatformTypeUnits: '',
					interfacePlatformTypeValidRangeDescription: '',
				},
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		const button = await (
			await loader.getHarness(MatCardHarness)
		).getHarness(
			MatButtonHarness.with({
				text: new RegExp('Create New Type From Base'),
			})
		);
		await button.click();
		expect(openDialog).toHaveBeenCalledWith(
			editPlatformTypeDialogDataMode.copy
		);
	});

	it('should call openEnumDialog()', async () => {
		const openEnumDialog = spyOn(
			component,
			'openEnumDialog'
		).and.callThrough();
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of<enumerationSet>({
				name: '',
				description: '',
				applicability: { id: '1', name: 'Base' },
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		const button = await (
			await loader.getHarness(MatCardHarness)
		).getHarness(
			MatButtonHarness.with({
				text: new RegExp('View Related Enumeration Set Attributes'),
			})
		);
		await button.click();
		expect(openEnumDialog).toHaveBeenCalled();
	});
});
