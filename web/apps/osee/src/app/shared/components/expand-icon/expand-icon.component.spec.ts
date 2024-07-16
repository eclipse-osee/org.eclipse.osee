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

import { ExpandIconComponent } from './expand-icon.component';

describe('ExpandIconComponent', () => {
	let component: ExpandIconComponent;
	let fixture: ComponentFixture<ExpandIconComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ExpandIconComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ExpandIconComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('open', false);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
