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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
} from '@osee/shared/components';

import { ParameterBranchComponent } from './parameter-branch.component';
import {
	ActionDropdownStub,
	BranchPickerStub,
} from '@osee/shared/components/testing';
import { ParameterDataService } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service';
import { parameterDataServiceMock } from '../../services/data-services/selected-command-data/parameter-data/parameter-data.service.mock';

describe('ParameterBranchComponent', () => {
	let component: ParameterBranchComponent;
	let fixture: ComponentFixture<ParameterBranchComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ParameterBranchComponent, {
			add: {
				imports: [BranchPickerStub, ActionDropdownStub],
			},
			remove: {
				imports: [BranchPickerComponent, ActionDropDownComponent],
			},
		}).configureTestingModule({
			imports: [NoopAnimationsModule, ParameterBranchComponent],
			providers: [
				{
					provide: ParameterDataService,
					useValue: parameterDataServiceMock,
				},
			],
		});
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
