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
import { CurrentStateServiceMock } from '../../../../shared/testing/current-structure.service.mock';
import { enumsServiceMock } from '../../../../shared/testing/enums.service.mock';
import { warningDialogServiceMock } from '../../../../shared/testing/warning-dialog.ui.service.mock';
import { EnumsService } from '../../../../shared/services/http/enums.service';
import { StructuresUiService } from '../../../../shared/services/ui/structures-ui.service';
import { WarningDialogService } from '../../../../shared/services/ui/warning-dialog.service';
import { STRUCTURE_SERVICE_TOKEN } from '../../../../shared/tokens/injection/structure/token';
import { EditStructureFieldComponent } from './edit-structure-field.component';
import { MatDialogModule } from '@angular/material/dialog';

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
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		uiService = TestBed.inject(StructuresUiService);
		fixture = TestBed.createComponent(EditStructureFieldComponent);
		component = fixture.componentInstance;
		component.header = 'applicability';
		component.value = { id: '1', name: 'Base' };
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should update value', async () => {
		uiService.BranchIdString = '8';
		uiService.messageIdString = '10';
		uiService.subMessageIdString = '20';
		uiService.connectionIdString = '10';
		component.focusChanged(null);
		let spy = spyOn(component, 'updateImmediately').and.callThrough();
		let select = await loader.getHarness(MatSelectHarness);
		await select.open();
		if (await select.isOpen()) {
			await select.clickOptions({ text: 'Second' });
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
		component.focusChanged(null);
		let spy = spyOn(component, 'updateStructure').and.callThrough();
		let input = await loader.getHarness(MatInputHarness);
		await input.setValue('asdfghij');
		tick(500);
		expect(spy).toHaveBeenCalled();
	}));
});
