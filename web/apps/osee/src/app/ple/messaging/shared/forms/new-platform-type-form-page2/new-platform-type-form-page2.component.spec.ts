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

import { CdkStepper } from '@angular/cdk/stepper';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { MatStepperModule } from '@angular/material/stepper';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import {
	EnumerationUIService,
	TypesService,
} from '@osee/messaging/shared/services';
import {
	MockNewPlatformTypeFormComponent,
	enumerationUiServiceMock,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { MockEnumSetFormUniqueComponent } from '../../forms/enum-set-form/enum-set-form.component.mock';
import { NewPlatformTypeFormPage2Component } from './new-platform-type-form-page2.component';
import { Component, signal, viewChild } from '@angular/core';
import { logicalType } from '@osee/messaging/shared/types';

describe('NewPlatformTypeFormPage2Component', () => {
	let component: NewPlatformTypeFormPage2Component;
	let fixture: ComponentFixture<ParentDriverComponent>;
	let loader: HarnessLoader;
	@Component({
		selector: 'osee-test-standalone-form',
		standalone: true,
		imports: [FormsModule, NewPlatformTypeFormPage2Component],
		template: `<form #testForm="ngForm">
			<osee-new-platform-type-form-page2 [logicalType]="logicalType()" />
		</form>`,
	})
	class ParentDriverComponent {
		logicalType = signal<logicalType>({
			id: '-1',
			idIntValue: -1,
			idString: '-1',
			name: '',
		});
		embeddedForm = viewChild.required(NewPlatformTypeFormPage2Component);
	}

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [],
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
						provideNoopAnimations(),
						{
							provide: EnumerationUIService,
							useValue: enumerationUiServiceMock,
						},
						{ provide: TypesService, useValue: typesServiceMock },
						{
							provide: CdkStepper,
							useValue: {},
						},
					],
				},
			})
			.compileComponents();

		fixture = TestBed.createComponent(ParentDriverComponent);
		component = fixture.componentInstance.embeddedForm();
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	//TODO: these tests will be disabled until a harness is created for all these components for playwright testing.
	xit('should select an enum set', async () => {
		const selectEnum = await loader.getHarness(MatSelectHarness);
		expect(selectEnum).toBeDefined();
		await selectEnum.open();
		await selectEnum.clickOptions({ text: 'enumset' });
		expect(await selectEnum.getValueText()).toEqual('enumset');
	});

	xit('should toggle enum mode', async () => {
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
