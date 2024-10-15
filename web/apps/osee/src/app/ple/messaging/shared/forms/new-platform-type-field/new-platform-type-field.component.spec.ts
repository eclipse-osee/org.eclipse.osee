/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { NewPlatformTypeFieldComponent } from './new-platform-type-field.component';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('NewPlatformTypeFieldComponent', () => {
	let component: NewPlatformTypeFieldComponent<'name'>;
	let fixture: ComponentFixture<NewPlatformTypeFieldComponent<'name'>>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NewPlatformTypeFieldComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(
			NewPlatformTypeFieldComponent<'name'>
		);
		fixture.componentRef.setInput('value', {
			id: '-1',
			typeId: ATTRIBUTETYPEIDENUM.NAME,
			gammaId: '-1',
			value: '',
		});
		fixture.componentRef.setInput('form', {
			attributeType: 'NAME',
			attributeTypeId: ATTRIBUTETYPEIDENUM.NAME,
			editable: true,
			name: 'name',
			required: true,
			defaultValue: '',
			value: '',
			jsonPropertyName: 'name',
		});
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
