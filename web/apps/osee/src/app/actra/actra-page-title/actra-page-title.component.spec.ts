/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { ActraPageTitleComponent } from './actra-page-title.component';

describe('ActraPageTitleComponent', () => {
	let component: ActraPageTitleComponent;
	let fixture: ComponentFixture<ActraPageTitleComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ActraPageTitleComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ActraPageTitleComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('icon', 'ac_unit');
		fixture.componentRef.setInput('title', 'some title');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
