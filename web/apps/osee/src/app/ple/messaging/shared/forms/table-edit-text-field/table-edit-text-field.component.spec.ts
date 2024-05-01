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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { TableEditTextFieldComponent } from './table-edit-text-field.component';

describe('TableEditTextFieldComponent', () => {
	let component: TableEditTextFieldComponent;
	let fixture: ComponentFixture<TableEditTextFieldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [TableEditTextFieldComponent, NoopAnimationsModule],
		}).compileComponents();

		fixture = TestBed.createComponent(TableEditTextFieldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
