/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import { PageNotFoundComponent } from './page-not-found.component';

describe('PageNotFoundComponent', () => {
	let component: PageNotFoundComponent;
	let fixture: ComponentFixture<PageNotFoundComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PageNotFoundComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PageNotFoundComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
