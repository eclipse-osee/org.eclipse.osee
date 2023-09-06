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
import {
	ComponentFixture,
	fakeAsync,
	TestBed,
	tick,
} from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { EditStructureFieldComponent } from './edit-structure-field.component';
import { MatDialogModule } from '@angular/material/dialog';

import {
	CurrentStateServiceMock,
	enumsServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import {
	StructuresUiService,
	EnumsService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';

describe('EditStructureFieldComponent', () => {
	let component: EditStructureFieldComponent;
	let fixture: ComponentFixture<EditStructureFieldComponent>;
	let uiService: StructuresUiService;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientTestingModule,
				NoopAnimationsModule,
				FormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule,
				MatDialogModule,
			],
			providers: [
				{ provide: EnumsService, useValue: enumsServiceMock },
				{
					provide: WarningDialogService,
					useValue: warningDialogServiceMock,
				},
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		uiService = TestBed.inject(StructuresUiService);
		fixture = TestBed.createComponent(EditStructureFieldComponent);
		component = fixture.componentInstance;
		//@todo luciano: backport gamma id changes to these components to make them more reactive
	});

	it('should create', () => {
		component.header = 'interfaceStructureCategory';
		component.value = 'blah';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
		expect(component).toBeTruthy();
	});

	it('should update value', async () => {
		component.header = 'interfaceStructureCategory';
		component.value = 'blah';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
		uiService.BranchIdString = '8';
		uiService.messageIdString = '10';
		uiService.subMessageIdString = '20';
		uiService.connectionIdString = '10';
		component.focusChanged(null);
		let spy = spyOn(component, 'updateImmediately').and.callThrough();
		let select = await loader.getHarness(MatSelectHarness);
		await select.open();
		if (await select.isOpen()) {
			await select.clickOptions({ text: 'N/A' });
			expect(spy).toHaveBeenCalled();
		} else {
			expect(spy).not.toHaveBeenCalled();
		}
	});
	it('should update description', fakeAsync(async () => {
		uiService.BranchIdString = '8';
		uiService.messageIdString = '10';
		uiService.subMessageIdString = '20';
		uiService.connectionIdString = '10';
		component.header = 'description';
		component.value = 'asdf';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
		component.focusChanged(null);
		let spy = spyOn(component, 'updateStructure').and.callThrough();
		let input = await loader.getHarness(MatInputHarness);
		await input.setValue('asdfghij');
		tick(500);
		expect(spy).toHaveBeenCalled();
	}));
});
