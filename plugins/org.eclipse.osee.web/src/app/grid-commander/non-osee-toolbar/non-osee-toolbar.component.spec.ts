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
import { NonOseeToolbarComponent } from './non-osee-toolbar.component';
import { Component, Input } from '@angular/core';
import { ToolbarComponent } from '@osee/toolbar';

@Component({
	selector: 'osee-toolbar',
	template: '<p>Mock Component</p>',
	standalone: true,
})
class MockOseeToolbarComponent {
	@Input() oseeToolbar = true;
}

describe('NonOseeToolbarComponent', () => {
	let component: NonOseeToolbarComponent;
	let fixture: ComponentFixture<NonOseeToolbarComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(NonOseeToolbarComponent, {
			add: {
				imports: [MockOseeToolbarComponent],
			},
			remove: {
				imports: [ToolbarComponent],
			},
		})
			.configureTestingModule({
				imports: [NonOseeToolbarComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(NonOseeToolbarComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
