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
import { NgIf, AsyncPipe, NgFor } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ActionDropdownStub } from '../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchPickerStub } from '../../../shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';

import { ImportComponent } from './import.component';
import { ImportService } from './lib/services/import.service';
import { importServiceMock } from './lib/services/import.service.mock';

describe('ImportComponent', () => {
	let component: ImportComponent;
	let fixture: ComponentFixture<ImportComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ImportComponent, {
			set: {
				imports: [
					NgIf,
					NgFor,
					AsyncPipe,
					MatButtonModule,
					MatSelectModule,
					ActionDropdownStub,
					BranchPickerStub,
				],
				providers: [
					{ provide: ImportService, useValue: importServiceMock },
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatButtonModule,
					MatFormFieldModule,
					MatSelectModule,
					NoopAnimationsModule,
					RouterTestingModule,
					ActionDropdownStub,
					BranchPickerStub,
					ImportComponent,
				],
				providers: [
					{ provide: ImportService, useValue: importServiceMock },
				],
				declarations: [],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ImportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
