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
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParameterMultipleSelectComponent } from './parameter-multiple-select.component';

describe('ParameterMultipleSelectComponent', () => {
	let component: ParameterMultipleSelectComponent;
	let fixture: ComponentFixture<ParameterMultipleSelectComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [HttpClientModule],
			declarations: [ParameterMultipleSelectComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ParameterMultipleSelectComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
