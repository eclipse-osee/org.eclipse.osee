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
import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { OseeToolbarComponent } from './osee-toolbar.component';

@Component({
	selector: 'osee-toolbar',
	template: '<p>Mock Component</p>',
	standalone: true,
})
class MockOseeToolbarComponent {
	@Input() oseeToolbar = true;
}

describe('ToolbarComponent', () => {
	let component: OseeToolbarComponent;
	let fixture: ComponentFixture<OseeToolbarComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(OseeToolbarComponent, {
			set: {
				imports: [MockOseeToolbarComponent],
			},
		})
			.configureTestingModule({
				imports: [OseeToolbarComponent, NoopAnimationsModule],
			})
			.compileComponents();

		fixture = TestBed.createComponent(OseeToolbarComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
