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
import {
	Component,
	Input,
	Optional,
	Output,
	effect,
	input,
} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
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
import { MarkdownEditorComponent } from './../markdown-editor/markdown-editor.component';
import { AttributeNameTrimPipe } from '../../pipes/attribute-name-trim/attribute-name-trim.pipe';
import { IfIdReturnFalsePipe } from '../../pipes/if-id-return-false/if-id-return-false.pipe';
import { StringToDatePipe } from '../../pipes/string-to-date/string-to-date.pipe';

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
		MarkdownEditorComponent,
		AttributeNameTrimPipe,
		IfIdReturnFalsePipe,
		StringToDatePipe,
		DatePipe,
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
	attributes = input.required<attribute[]>();
	editable = input.required<boolean>();

	_attributes = new BehaviorSubject<attribute[]>([]);

	constructor() {
		effect(() => {
			this._attributes.next(this.attributes());
		});
	}

	@Output() updatedAttributes = new BehaviorSubject<attribute[]>([]);

	emitUpdatedAttributes() {
		const formattedAttributes: attribute[] = this._attributes.value
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

	setAttribute(val: string, attribute: attribute) {
		const datePipe = new DatePipe('en-US');
		const dateString = datePipe.transform(val);
		attribute.value = dateString ? dateString : '';
	}
}
