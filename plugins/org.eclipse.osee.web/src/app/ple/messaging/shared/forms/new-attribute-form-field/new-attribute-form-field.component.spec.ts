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
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { logicalTypeFormDetailMock } from '../../testing/logical-type-form-detail.response.mock';

import { NewAttributeFormFieldComponent } from './new-attribute-form-field.component';

describe('NewAttributeFormFieldComponent', () => {
	let component: NewAttributeFormFieldComponent;
	let fixture: ComponentFixture<NewAttributeFormFieldComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(NewAttributeFormFieldComponent, {
			set: {
				viewProviders: [],
			},
		})
			.configureTestingModule({
				imports: [
					MatFormFieldModule,
					FormsModule,
					MatInputModule,
					MatSelectModule,
					NoopAnimationsModule,
				],
				declarations: [],
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewAttributeFormFieldComponent);
		component = fixture.componentInstance;
		component.form = logicalTypeFormDetailMock.fields[0];
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
	it('should set default value', () => {
		component.form.editable = false;
		component.form.value = '123456789';
		component.setDefaultValue();
		expect(component.form.value).toEqual(component.form.defaultValue);
	});
});
