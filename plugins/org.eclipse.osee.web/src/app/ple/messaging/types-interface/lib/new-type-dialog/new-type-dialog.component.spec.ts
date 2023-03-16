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
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { NewTypeDialogComponent } from './new-type-dialog.component';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import {
	NgIf,
	NgFor,
	AsyncPipe,
	TitleCasePipe,
	KeyValuePipe,
} from '@angular/common';
import {
	MockNewTypeFormComponent,
	MimPreferencesServiceMock,
	typesServiceMock,
	enumsServiceMock,
	enumerationUiServiceMock,
	applicabilityListUIServiceMock,
} from '@osee/messaging/shared/testing';
import {
	MimPreferencesService,
	TypesService,
	EnumsService,
	EnumerationUIService,
	ApplicabilityListUIService,
} from '@osee/messaging/shared/services';
import { UserDataAccountService } from '@osee/auth';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { TransactionBuilderService } from '@osee/shared/transactions';
import { transactionBuilderMock } from '@osee/shared/transactions/testing';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('NewTypeDialogComponent', () => {
	let component: NewTypeDialogComponent;
	let fixture: ComponentFixture<NewTypeDialogComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
		})
			.overrideComponent(NewTypeDialogComponent, {
				set: {
					imports: [
						MatDialogModule,
						MatStepperModule,
						MatSelectModule,
						FormsModule,
						MatFormFieldModule,
						MatInputModule,
						MatButtonModule,
						MatTableModule,
						MatIconModule,
						MockMatOptionLoadingComponent,
						MockNewTypeFormComponent,
						NgIf,
						NgFor,
						AsyncPipe,
						TitleCasePipe,
						KeyValuePipe,
					],
					providers: [
						{ provide: MatDialogRef, useValue: {} },
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
							provide: EnumerationUIService,
							useValue: enumerationUiServiceMock,
						},
						{
							provide: ApplicabilityListUIService,
							useValue: applicabilityListUIServiceMock,
						},
					],
				},
			})
			.compileComponents();
	});

	beforeEach(async () => {
		fixture = TestBed.createComponent(NewTypeDialogComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
	// describe('Page 1 Testing', () => {
	// 	it('should select enumeration', async () => {
	// 		let select = await loader.getHarness(MatSelectHarness);
	// 		await select.clickOptions({ text: 'Enumeration' });
	// 		let button = await loader.getHarness(
	// 			MatButtonHarness.with({ text: 'Next' })
	// 		);
	// 		expect(button).toBeDefined();
	// 		expect(await button.isDisabled()).toBe(false);
	// 		await button.click();
	// 	});
	// });
	// describe('Page 2 testing', () => {
	// 	beforeEach(async () => {
	// 		let selectPage1 = await loader.getHarness(MatSelectHarness);
	// 		await selectPage1.clickOptions({ text: 'Enumeration' });
	// 		let buttonPage1 = await loader.getHarness(
	// 			MatStepperNextHarness.with({ text: 'Next' })
	// 		);
	// 		await buttonPage1.click();
	// 	});
	// 	it('should select an enum set', async () => {
	// 		let selectEnum = await loader.getHarness(MatSelectHarness);
	// 		expect(selectEnum).toBeDefined();
	// 		await selectEnum.open();
	// 		await selectEnum.clickOptions({ text: 'Enumeration' });
	// 		expect(await selectEnum.getValueText()).toEqual('Enumeration');
	// 	});

	// 	it('should toggle enum mode', async () => {
	// 		// const spy = spyOn(
	// 		// 	component,
	// 		// 	'toggleEnumCreationState'
	// 		// ).and.callThrough();
	// 		// let button = await loader.getHarness(
	// 		// 	MatButtonHarness.with({ text: new RegExp('add') })
	// 		// );
	// 		// await button.click();
	// 		// expect(spy).toHaveBeenCalled();
	// 	});
	// 	describe('enum editing', () => {
	// 		beforeEach(async () => {
	// 			let button = await loader.getHarness(
	// 				MatButtonHarness.with({ text: new RegExp('add') })
	// 			);
	// 			await button.click();
	// 		});
	// 	});

	// 	describe('Page 3 testing', () => {
	// 		beforeEach(async () => {
	// 			let buttonPage2 = await loader.getHarness(
	// 				MatStepperNextHarness.with({ text: 'Next' })
	// 			);
	// 			await buttonPage2.click();
	// 		});

	// 		it('should create a type', async () => {
	// 			// let spy = spyOn(component, 'hideTypeDialog').and.callThrough();
	// 			// let button = await loader.getHarness(
	// 			// 	MatButtonHarness.with({ text: 'Create Type' })
	// 			// );
	// 			// await button.click();
	// 			// expect(spy).toHaveBeenCalled();
	// 		});
	// 	});
	// });
});
