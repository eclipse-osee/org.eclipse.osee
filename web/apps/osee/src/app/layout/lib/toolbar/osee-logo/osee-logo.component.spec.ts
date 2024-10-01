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

import OseeLogoComponent from './osee-logo.component';
import { provideRouter } from '@angular/router';
import { MatIconMock } from '@osee/shared/testing';
import { MatIcon } from '@angular/material/icon';

describe('OseeLogoComponent', () => {
	let component: OseeLogoComponent;
	let fixture: ComponentFixture<OseeLogoComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(OseeLogoComponent, {
			add: {
				imports: [MatIconMock],
			},
			remove: { imports: [MatIcon] },
		})
			.configureTestingModule({
				imports: [OseeLogoComponent],
				providers: [provideRouter([])],
			})
			.compileComponents();

		fixture = TestBed.createComponent(OseeLogoComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
