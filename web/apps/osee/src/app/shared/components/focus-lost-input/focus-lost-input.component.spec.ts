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

import { FocusLostInputComponent } from './focus-lost-input.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('FocusLostInputComponent', () => {
	let component: FocusLostInputComponent<unknown>;
	let fixture: ComponentFixture<FocusLostInputComponent<unknown>>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [FocusLostInputComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(FocusLostInputComponent);
		fixture.componentRef.setInput('value', '');
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
