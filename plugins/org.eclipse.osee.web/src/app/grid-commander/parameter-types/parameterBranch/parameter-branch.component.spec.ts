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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
} from '@osee/shared/components';

import { ParameterBranchComponent } from './parameter-branch.component';

describe('ParameterBranchComponent', () => {
	let component: ParameterBranchComponent;
	let fixture: ComponentFixture<ParameterBranchComponent>;
	let httpTestingController: HttpTestingController;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				ActionDropDownComponent,
				BranchPickerComponent,
				HttpClientTestingModule,
				NoopAnimationsModule,
				ParameterBranchComponent,
			],
		});
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ParameterBranchComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
