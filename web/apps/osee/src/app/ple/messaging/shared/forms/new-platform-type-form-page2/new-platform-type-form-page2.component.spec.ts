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

import { NewPlatformTypeFormPage2Component } from './new-platform-type-form-page2.component';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { CdkStepper } from '@angular/cdk/stepper';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatSelectHarness } from '@angular/material/select/testing';
import { SimpleChange } from '@angular/core';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockEnumSetFormUniqueComponent } from '../../forms/enum-set-form/enum-set-form.component.mock';
import { MatButtonHarness } from '@angular/material/button/testing';
import {
	MockNewPlatformTypeFormComponent,
	typesServiceMock,
	enumsServiceMock,
	enumerationUiServiceMock,
} from '@osee/messaging/shared/testing';
import {
	EnumerationUIService,
	TypesService,
	EnumsService,
} from '@osee/messaging/shared/services';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';

describe('NewPlatformTypeFormPage2Component', () => {
	let component: NewPlatformTypeFormPage2Component;
	let fixture: ComponentFixture<NewPlatformTypeFormPage2Component>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule],
		})
			.overrideComponent(NewPlatformTypeFormPage2Component, {
				set: {
					imports: [
						NgIf,
						NgFor,
						AsyncPipe,
						MockNewPlatformTypeFormComponent,
						MockMatOptionLoadingComponent,
						MockEnumSetFormUniqueComponent,
						FormsModule,
						MatFormFieldModule,
						MatSelectModule,
						MatIconModule,
						MatDialogModule,
						MatButtonModule,
						MatStepperModule,
					],
					providers: [
						{
							provide: EnumerationUIService,
							useValue: enumerationUiServiceMock,
						},
						{ provide: TypesService, useValue: typesServiceMock },
						{ provide: EnumsService, useValue: enumsServiceMock },
						{
							provide: CdkStepper,
							useValue: {},
						},
					],
				},
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewPlatformTypeFormPage2Component);
		component = fixture.componentInstance;
		fixture.detectChanges();
		component.logicalType = {
			id: '8',
			idIntValue: 8,
			idString: '8',
			name: 'enumeration',
		};
		component.ngOnChanges({
			logicalType: new SimpleChange(
				{
					id: '-1',
					idIntValue: -1,
					idString: '-1',
					name: '',
				},
				{
					id: '8',
					idIntValue: 8,
					idString: '8',
					name: 'enumeration',
				},
				true
			),
		});
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should select an enum set', async () => {
		let selectEnum = await loader.getHarness(MatSelectHarness);
		expect(selectEnum).toBeDefined();
		await selectEnum.open();
		await selectEnum.clickOptions({ text: 'enumset' });
		expect(await selectEnum.getValueText()).toEqual('enumset');
	});

	it('should toggle enum mode', async () => {
		const addButton = await loader.getHarness(
			MatButtonHarness.with({ text: new RegExp('add') })
		);
		expect(addButton).toBeDefined();
		const spy = spyOn(
			component,
			'toggleEnumCreationState'
		).and.callThrough();
		await addButton.click();
		expect(spy).toHaveBeenCalled();
	});
});
