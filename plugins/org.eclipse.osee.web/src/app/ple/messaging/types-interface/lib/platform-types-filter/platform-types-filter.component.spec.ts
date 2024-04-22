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

import { PlatformTypesFilterComponent } from './platform-types-filter.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('PlatformTypesFilterComponent', () => {
	let component: PlatformTypesFilterComponent;
	let fixture: ComponentFixture<PlatformTypesFilterComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PlatformTypesFilterComponent],
			providers: [provideNoopAnimations()],
		}).compileComponents();

		fixture = TestBed.createComponent(PlatformTypesFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
