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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ParameterSingleSelectComponent } from './parameter-single-select.component';

describe('ParameterSingleSelectComponent', () => {
	let component: ParameterSingleSelectComponent;
	let fixture: ComponentFixture<ParameterSingleSelectComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				HttpClientModule,
				MatSelectModule,
				MatFormFieldModule,
				NoopAnimationsModule,
				ParameterSingleSelectComponent,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ParameterSingleSelectComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
