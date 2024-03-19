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
import { Component, Input, Optional, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { AttributeEnumsDropdownComponent } from './attribute-enums-dropdown/attribute-enums-dropdown.component';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { FormDirective } from '@osee/shared/directives';
import { attribute } from '@osee/shared/types';

function controlContainerFactory(controlContainer?: ControlContainer) {
	return controlContainer;
}

// Attributes Editor does not enforce required fields.
// It will just highlight required fields based on an attribute's multiplicityId.
// Output is the changed attributes list for parent component to handle.
@Component({
	selector: 'osee-attributes-editor',
	standalone: true,
	imports: [
		CommonModule,
		AttributeEnumsDropdownComponent,
		MatInputModule,
		MatFormFieldModule,
		FormsModule,
		MatSelectModule,
		MatDatepickerModule,
		MatNativeDateModule,
		FormDirective,
	],
	templateUrl: './attributes-editor.component.html',
	viewProviders: [
		{
			provide: ControlContainer,
			useFactory: controlContainerFactory,
			deps: [[new Optional(), NgForm]],
		},
	],
})
export class AttributesEditorComponent {
	@Input() set attributes(attributes: attribute[]) {
		this._attributes.next(attributes);
		this._attributes.value.forEach((attr) => {
			// Format the input attributes' date values to a format recognizable for the datepicker
			if (attr.storeType === 'Date' && attr.value !== '') {
				attr.value = new Date(attr.value);
			}
			// Format ats attribute type name
			if (attr.name.includes('ats.')) {
				attr.name = attr.name.replace('ats.', '');
			}
		});
	}
	@Input() editable: boolean = false;

	_attributes = new BehaviorSubject<attribute[]>([]);

	@Output() updatedAttributes = new BehaviorSubject<attribute<string>[]>([]);

	emitUpdatedAttributes() {
		const formattedAttributes: attribute<string>[] = this._attributes.value
			.map((attribute) => {
				const formattedAttribute = { ...attribute, value: '' };

				if (attribute.storeType === 'Date' && attribute.value) {
					const dateValue = new Date(attribute.value);
					formattedAttribute.value = `${dateValue.getTime()}`;
				} else {
					formattedAttribute.value = attribute.value.toString();
				}

				return formattedAttribute;
			})
			.filter((attribute) => attribute.value !== '');

		this.updatedAttributes.next(formattedAttributes);
	}

	// Input is required if attribute multiplicity AT_LEAST_ONE or EXACTLY_ONE

	isRequired(attribute: attribute) {
		return attribute.name === 'Id'
			? false
			: attribute.multiplicityId === '2' ||
					attribute.multiplicityId === '4';
	}
}
