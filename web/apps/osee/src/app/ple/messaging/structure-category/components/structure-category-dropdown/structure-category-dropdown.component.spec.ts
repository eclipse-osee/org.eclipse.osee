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

import { StructureCategoryDropdownComponent } from './structure-category-dropdown.component';
import { CurrentStructureCategoriesService } from '@osee/messaging/structure-category/services';
import { CurrentStructureCategoriesServiceMock } from '@osee/messaging/structure-category/services/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('StructureCategoryDropdownComponent', () => {
	let component: StructureCategoryDropdownComponent;
	let fixture: ComponentFixture<StructureCategoryDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [StructureCategoryDropdownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CurrentStructureCategoriesService,
					useValue: CurrentStructureCategoriesServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(StructureCategoryDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
