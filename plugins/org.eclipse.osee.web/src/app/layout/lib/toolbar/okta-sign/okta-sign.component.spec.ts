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

import { OktaSignComponent } from './okta-sign.component';
import { OKTA_AUTH, OktaAuthStateService } from '@okta/okta-angular';
import { of } from 'rxjs';

describe('OktaSignComponent', () => {
	let component: OktaSignComponent;
	let fixture: ComponentFixture<OktaSignComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(OktaSignComponent, {
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
				imports: [OktaSignComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(OktaSignComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
