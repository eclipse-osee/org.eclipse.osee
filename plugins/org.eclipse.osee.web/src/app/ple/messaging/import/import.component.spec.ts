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
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { ActionDropdownStub } from 'src/app/shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchPickerStub } from 'src/app/shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';

import { ImportComponent } from './import.component';
import { ImportService } from './services/import.service';
import { importServiceMock } from './services/import.service.mock';

describe('ImportComponent', () => {
	let component: ImportComponent;
	let fixture: ComponentFixture<ImportComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatButtonModule,
				MatFormFieldModule,
				MatSelectModule,
				NoopAnimationsModule,
				RouterTestingModule,
			],
			providers: [
				{ provide: ImportService, useValue: importServiceMock },
			],
			declarations: [
				ImportComponent,
				ActionDropdownStub,
				BranchPickerStub,
			],
		}).compileComponents();
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
