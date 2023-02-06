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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatStepperModule } from '@angular/material/stepper';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddElementDialog } from './add-element-dialog';

import { AddElementDialogComponent } from './add-element-dialog.component';

import { PlatformTypeQueryMock } from '../testing/platform-type-query.component.mock';
import {
	ApplicabilityListService,
	EnumerationSetService,
	EnumsService,
	MimPreferencesService,
	STRUCTURE_SERVICE_TOKEN,
	TypesService,
} from '@osee/messaging/shared';
import { UserDataAccountService, userDataAccountServiceMock } from '@osee/auth';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';
import {
	MockNewTypeFormComponent,
	MimPreferencesServiceMock,
	typesServiceMock,
	enumsServiceMock,
	enumerationSetServiceMock,
	applicabilityListServiceMock,
	CurrentStateServiceMock,
	MockNewTypeDialogComponent,
} from '@osee/messaging/shared/testing';

describe('AddElementDialogComponent', () => {
	let component: AddElementDialogComponent;
	let fixture: ComponentFixture<AddElementDialogComponent>;
	let dialogData: AddElementDialog = {
		id: '12345',
		name: 'structure',
		type: {
			id: '',
			name: '',
			description: '',
			interfaceLogicalType: '',
			interfacePlatform2sComplement: false,
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
		},
	};
	let loader: HarnessLoader;
	let nestedDialog: DebugElement;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
		})
			.configureTestingModule({
				imports: [
					HttpClientTestingModule,
					MatStepperModule,
					MatDialogModule,
					MatButtonModule,
					FormsModule,
					MatFormFieldModule,
					MatSelectModule,
					MatInputModule,
					MatSlideToggleModule,
					MatIconModule,
					MatDividerModule,
					MatProgressSpinnerModule,
					MockMatOptionLoadingComponent,
					MockNewTypeFormComponent,
				],
				declarations: [],
				providers: [
					{
						provide: MatDialogRef,
						useValue: {},
					},
					{
						provide: MAT_DIALOG_DATA,
						useValue: dialogData,
					},
					{
						provide: TransactionBuilderService,
						useValue: transactionBuilderMock,
					},
					{
						provide: MimPreferencesService,
						useValue: MimPreferencesServiceMock,
					},
					{
						provide: UserDataAccountService,
						useValue: userDataAccountServiceMock,
					},
					{ provide: TypesService, useValue: typesServiceMock },
					{ provide: EnumsService, useValue: enumsServiceMock },
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
					{
						provide: ApplicabilityListService,
						useValue: applicabilityListServiceMock,
					},
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(AddElementDialogComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Page 2', () => {
		beforeEach(async () => {
			const createNewBtn = await loader.getHarness(
				MatButtonHarness.with({ text: 'Create new Element' })
			);
			await createNewBtn.click();
		});

		//these two tests are randomly failing because the loader seems to not be responding...
		xit('should fill out the form', async () => {
			const spy = spyOn(
				component,
				'receivePlatformTypeData'
			).and.callThrough();
			const addTypeBtn = await loader.getHarness(
				MatButtonHarness.with({ text: new RegExp('add') })
			);
			await addTypeBtn.click();
			nestedDialog = fixture.debugElement.query(
				By.directive(MockNewTypeDialogComponent)
			);
			fixture.detectChanges();
			nestedDialog.componentInstance.closeDialog();
			expect(spy).toHaveBeenCalled();
		});
		xit('should open the search form', async () => {
			const spy = spyOn(component, 'openSearch').and.callThrough();
			const spy2 = spyOn(component, 'receiveQuery').and.callThrough();
			const searchbtn = await loader.getHarness(
				MatButtonHarness.with({ selector: '.search-button' })
			);
			await searchbtn.click();
			expect(spy).toHaveBeenCalled();
			const nestedSearch = fixture.debugElement.query(
				By.directive(PlatformTypeQueryMock)
			);
			fixture.detectChanges();
			nestedSearch.componentInstance.returnQuery.next(
				new PlatformTypeQueryMock()
			);
			expect(spy2).toHaveBeenCalled();
		});
	});
});
