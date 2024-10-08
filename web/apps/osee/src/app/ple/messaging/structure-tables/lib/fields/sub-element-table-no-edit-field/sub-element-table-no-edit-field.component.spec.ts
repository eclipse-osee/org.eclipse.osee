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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { elementsMock } from '@osee/messaging/shared/testing';
import { SubElementTableNoEditFieldComponent } from './sub-element-table-no-edit-field.component';

describe('SubElementTableNoEditFieldComponent', () => {
	let component: SubElementTableNoEditFieldComponent;
	let fixture: ComponentFixture<SubElementTableNoEditFieldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [],
			providers: [provideRouter([])],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementTableNoEditFieldComponent);
		fixture.componentRef.setInput('header', 'name');
		fixture.componentRef.setInput('element', elementsMock[0]);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
