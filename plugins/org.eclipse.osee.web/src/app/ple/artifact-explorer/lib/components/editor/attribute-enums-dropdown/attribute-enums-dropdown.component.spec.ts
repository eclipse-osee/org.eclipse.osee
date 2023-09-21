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
import { AttributeEnumsDropdownComponent } from './attribute-enums-dropdown.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AttributeEnumsDropdownComponent', () => {
	let component: AttributeEnumsDropdownComponent;
	let fixture: ComponentFixture<AttributeEnumsDropdownComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				AttributeEnumsDropdownComponent,
				HttpClientTestingModule,
				BrowserAnimationsModule,
			],
		});
		fixture = TestBed.createComponent(AttributeEnumsDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
