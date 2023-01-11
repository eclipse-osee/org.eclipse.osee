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

import { SubElementTableNoEditFieldNameComponent } from './sub-element-table-no-edit-field-name.component';

describe('SubElementTableNoEditFieldNameComponent', () => {
	let component: SubElementTableNoEditFieldNameComponent;
	let fixture: ComponentFixture<SubElementTableNoEditFieldNameComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(
			SubElementTableNoEditFieldNameComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
