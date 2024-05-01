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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSelectHarness } from '@angular/material/select/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { EditSubMessageFieldComponent } from './edit-sub-message-field.component';
import { MatDialogModule } from '@angular/material/dialog';
import {
	CurrentMessagesService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	warningDialogServiceMock,
} from '@osee/messaging/shared/testing';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';
import { A11yModule } from '@angular/cdk/a11y';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { MatOptionModule } from '@angular/material/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

describe('EditSubMessageFieldComponent', () => {
	let component: EditSubMessageFieldComponent;
	let fixture: ComponentFixture<EditSubMessageFieldComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(EditSubMessageFieldComponent, {
			set: {
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
				imports: [
					NgIf,
					NgFor,
					AsyncPipe,
					A11yModule,
					FormsModule,
					MatFormFieldModule,
					MatSelectModule,
					MatOptionModule,
					MatInputModule,
					MatSlideToggleModule,
					MockApplicabilitySelectorComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					FormsModule,
					MatFormFieldModule,
					MatInputModule,
					MatSelectModule,
					NoopAnimationsModule,
					EditSubMessageFieldComponent,
					MatDialogModule,
				],
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
					{
						provide: WarningDialogService,
						useValue: warningDialogServiceMock,
					},
				],
				declarations: [],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditSubMessageFieldComponent);
		component = fixture.componentInstance;
		component.header = 'applicability';
		component.value = { id: '1', name: 'Base' };
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
