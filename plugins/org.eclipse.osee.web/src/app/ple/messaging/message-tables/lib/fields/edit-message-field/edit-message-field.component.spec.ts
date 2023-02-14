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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import {
	ComponentFixture,
	fakeAsync,
	TestBed,
	tick,
} from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from '../../../../../../../environments/environment';
import { EditMessageFieldComponent } from './edit-message-field.component';
import {
	ApplicabilityListService,
	MessageUiService,
} from '@osee/messaging/shared';
import { applicabilityListServiceMock } from '@osee/messaging/shared/testing';

describe('EditMessageFieldComponent', () => {
	let component: EditMessageFieldComponent;
	let fixture: ComponentFixture<EditMessageFieldComponent>;
	let httpTestingController: HttpTestingController;
	let uiService: MessageUiService;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientTestingModule,
				FormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatSelectModule,
				MatDialogModule,
				NoopAnimationsModule,
				EditMessageFieldComponent,
			],
			providers: [
				{
					provide: ApplicabilityListService,
					useValue: applicabilityListServiceMock,
				},
			],
			teardown: { destroyAfterEach: false },
		}).compileComponents();
	});

	beforeEach(() => {
		httpTestingController = TestBed.inject(HttpTestingController);
		uiService = TestBed.inject(MessageUiService);
		fixture = TestBed.createComponent(EditMessageFieldComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
		component.header = 'applicability';
		component.value = { id: '1', name: 'Base' };
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should update value', fakeAsync(() => {
		uiService.BranchIdString = '8';
		uiService.connectionIdString = '10';
		component.focusChanged(null);
		component.updateMessage('v2');
		tick(500);
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs');
		expect(req.request.method).toEqual('POST');
	}));

	it('should select an applicability', async () => {
		uiService.BranchIdString = '8';
		uiService.connectionIdString = '10';
		component.focusChanged(null);
		const spy = spyOn(component, 'updateImmediately').and.callThrough();
		const select = await loader.getHarness(MatSelectHarness);
		await select.open();
		await select.clickOptions({ text: 'Second' });
		await select.close();
		expect(spy).toHaveBeenCalled();
	});
});
