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

import { PlatformTypeDropdownComponent } from './platform-type-dropdown.component';
import { TypesUIService } from '@osee/messaging/shared/services';
import { typesUIServiceMock } from '@osee/messaging/shared/testing';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('PlatformTypeDropdownComponent', () => {
	let component: PlatformTypeDropdownComponent;
	let fixture: ComponentFixture<PlatformTypeDropdownComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PlatformTypeDropdownComponent],
			providers: [
				provideRouter([]),
				provideNoopAnimations(),
				{ provide: TypesUIService, useValue: typesUIServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PlatformTypeDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
