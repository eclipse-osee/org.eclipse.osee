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
import { NgIf, AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterOutlet } from '@angular/router';

import { OseeToolbarComponent } from './osee-toolbar.component';

@Component({
	selector: 'osee-display-user',
	template: '<p>Mock Component</p>',
	standalone: true,
})
class MockOseeUserComponent {}

describe('ToolbarComponent', () => {
	let component: OseeToolbarComponent;
	let fixture: ComponentFixture<OseeToolbarComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(OseeToolbarComponent, {
			set: {
				imports: [
					MatToolbarModule,
					MatButtonModule,
					MatIconModule,
					RouterOutlet,
					NgIf,
					AsyncPipe,
					MatProgressSpinnerModule,
					MockOseeUserComponent,
				],
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
