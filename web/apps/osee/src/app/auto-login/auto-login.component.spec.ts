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

import { AutoLoginComponent } from './auto-login.component';
import { OktaAuthStateService, OKTA_AUTH } from '@okta/okta-angular';
import { of } from 'rxjs';

describe('AutoLoginComponent', () => {
	let component: AutoLoginComponent;
	let fixture: ComponentFixture<AutoLoginComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(AutoLoginComponent, {
			set: {
				providers: [
					{
						provide: OktaAuthStateService,
						useValue: {
							authState$: of(),
						},
					},
					{
						provide: OKTA_AUTH,
						useValue: {
							signInWithRedirect() {
								return new Promise<void>(() => {});
							},
							signOut() {
								return new Promise<void>(() => {});
							},
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [AutoLoginComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(AutoLoginComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
