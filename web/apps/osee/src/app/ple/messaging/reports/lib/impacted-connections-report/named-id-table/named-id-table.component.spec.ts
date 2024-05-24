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

import { NamedIdTableComponent } from './named-id-table.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('NamedIdTableComponent', () => {
	let component: NamedIdTableComponent;
	let fixture: ComponentFixture<NamedIdTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NamedIdTableComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(NamedIdTableComponent);
		fixture.componentRef.setInput('content', []);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
