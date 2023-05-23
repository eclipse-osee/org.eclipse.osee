/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { AsyncPipe, NgIf } from '@angular/common';
import {
	ComponentFixture,
	fakeAsync,
	TestBed,
	tick,
	discardPeriodicTasks,
} from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatAutocompleteHarness } from '@angular/material/autocomplete/testing';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatInputHarness } from '@angular/material/input/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { ApplicabilityListUIService } from '@osee/shared/services';
import { applicabilityListUIServiceMock } from '@osee/shared/testing';

import { ApplicabilitySelectorComponent } from './applicability-selector.component';

describe('ApplicabilitySelectorComponent', () => {
	let component: ApplicabilitySelectorComponent;
	let fixture: ComponentFixture<ApplicabilitySelectorComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.overrideComponent(ApplicabilitySelectorComponent, {
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
				imports: [ApplicabilitySelectorComponent, NoopAnimationsModule],
				providers: [
					{
						provide: ApplicabilityListUIService,
						useValue: applicabilityListUIServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ApplicabilitySelectorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should open the autocomplete and type in an input', fakeAsync(async () => {
		let input = await loader.getHarness(MatInputHarness);
		let autocomplete = await loader.getHarness(MatAutocompleteHarness);
		let spy = spyOn(component, 'autoCompleteOpened').and.callThrough();
		let spy2 = spyOn(component, 'updateTypeAhead').and.callThrough();
		await autocomplete.focus();
		expect(spy).toHaveBeenCalled();
		tick(1000);
		expect(await autocomplete.isOpen()).toBe(true);
		await autocomplete.enterText('Secondary');

		await autocomplete.blur();
		expect(await autocomplete.isFocused()).toBeFalsy();
		//expect(await autocomplete.getValue()).toBe('Secondary');
		expect(spy2).toHaveBeenCalled();
		discardPeriodicTasks();
	}));
});
