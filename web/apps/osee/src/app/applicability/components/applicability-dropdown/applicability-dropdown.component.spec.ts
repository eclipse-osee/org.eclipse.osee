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
import { AsyncPipe, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applicabilityListUIServiceMock } from '@osee/shared/testing';

import { ApplicabilityDropdownComponent } from './applicability-dropdown.component';

describe('ApplicabilitySelectorComponent', () => {
	let component: ApplicabilityDropdownComponent;
	let fixture: ComponentFixture<ApplicabilityDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ApplicabilityDropdownComponent, {
			set: {
				imports: [
					AsyncPipe,
					NgIf,
					FormsModule,
					MatInputModule,
					MatOptionModule,
					MatFormFieldModule,
					MatAutocompleteModule,
					MatIconModule,
					MockMatOptionLoadingComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [ApplicabilityDropdownComponent, NoopAnimationsModule],
				providers: [
					{
						provide: ApplicabilityListUIService,
						useValue: applicabilityListUIServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ApplicabilityDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
