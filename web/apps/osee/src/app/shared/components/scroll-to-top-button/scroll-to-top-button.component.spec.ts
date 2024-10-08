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
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrollToTopButtonComponent } from './scroll-to-top-button.component';

describe('ScrollToTopButtonComponent', () => {
	let component: ScrollToTopButtonComponent;
	let fixture: ComponentFixture<ScrollToTopButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ScrollToTopButtonComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ScrollToTopButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
