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
import { provideRouter } from '@angular/router';

import { AsyncPipe } from '@angular/common';
import { MatSuffix } from '@angular/material/form-field';
import { MockPersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown/testing';
import { MockPersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle/testing';
import { MockPersistedNumberAttributeInputComponent } from '@osee/attributes/persisted-number-attribute-input/testing';
import { MockPersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input/testing';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import {
	CurrentStateServiceMock,
	elementsMock,
} from '@osee/messaging/shared/testing';
import { MockPersistedPlatformTypeRelationSelectorComponent } from '@osee/messaging/types/persisted-relation-selector/testing';
import { MockPersistedUnitDropdownComponent } from '@osee/messaging/units/persisted-unit-dropdown/testing';
import { EnumLiteralsFieldComponent } from '../enum-literal-field/enum-literals-field.component';
import { SubElementTableNoEditFieldComponent } from '../sub-element-table-no-edit-field/sub-element-table-no-edit-field.component';
import { SubElementTableFieldComponent } from './sub-element-table-field.component';
import { FormsModule } from '@angular/forms';
import { ElementImpactsValidatorDirective } from '../../element-impacts-validator.directive';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';

describe('SubElementTableFieldComponent', () => {
	let component: SubElementTableFieldComponent;
	let fixture: ComponentFixture<SubElementTableFieldComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(SubElementTableFieldComponent, {
			set: {
				imports: [
					SubElementTableNoEditFieldComponent,
					AsyncPipe,
					EnumLiteralsFieldComponent,
					AttributeToValuePipe,
					MatSuffix,
					MockPersistedStringAttributeInputComponent,
					MockPersistedApplicabilityDropdownComponent,
					MockPersistedBooleanAttributeToggleComponent,
					MockPersistedNumberAttributeInputComponent,
					MockPersistedPlatformTypeRelationSelectorComponent,
					MockPersistedUnitDropdownComponent,
					FormsModule,
					ElementImpactsValidatorDirective,
				],
			},
		})
			.configureTestingModule({
				providers: [
					provideRouter([]),
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementTableFieldComponent);
		fixture.componentRef.setInput('header', 'name');
		fixture.componentRef.setInput('editMode', true);
		fixture.componentRef.setInput('element', elementsMock[0]);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
