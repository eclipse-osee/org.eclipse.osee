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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { AsyncPipe, DatePipe, NgClass } from '@angular/common';
import { Component, Output, input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOption, provideNativeDateAdapter } from '@angular/material/core';
import {
	MatDatepicker,
	MatDatepickerInput,
	MatDatepickerToggle,
} from '@angular/material/datepicker';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { FormDirective } from '@osee/shared/directives';
import { attribute } from '@osee/shared/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { BehaviorSubject } from 'rxjs';
import { AttributeNameTrimPipe } from '../../pipes/attribute-name-trim/attribute-name-trim.pipe';
import { IfIdReturnFalsePipe } from '../../pipes/if-id-return-false/if-id-return-false.pipe';
import { StringToDatePipe } from '../../pipes/string-to-date/string-to-date.pipe';
import { MarkdownEditorComponent } from './../markdown-editor/markdown-editor.component';
import { AttributeEnumsDropdownComponent } from './attribute-enums-dropdown/attribute-enums-dropdown.component';

// Attributes Editor does not enforce required fields.
// It will just highlight required fields based on an attribute's multiplicityId.
// Output is the changed attributes list for parent component to handle.
@Component({
	selector: 'osee-attributes-editor',
	imports: [
		NgClass,
		AsyncPipe,
		AttributeEnumsDropdownComponent,
		FormsModule,
		MatFormField,
		MatInput,
		CdkTextareaAutosize,
		MatSelect,
		MatOption,
		MatDatepicker,
		MatDatepickerToggle,
		MatDatepickerInput,
		MatSuffix,
		FormDirective,
		MarkdownEditorComponent,
		AttributeNameTrimPipe,
		IfIdReturnFalsePipe,
		StringToDatePipe,
		DatePipe,
	],
	providers: [provideNativeDateAdapter()],
	templateUrl: './attributes-editor.component.html',
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class AttributesEditorComponent {
	attributes = input.required<attribute[]>();
	editable = input.required<boolean>();

	@Output() updatedAttributes = new BehaviorSubject<attribute[]>([]);

	emitUpdatedAttributes() {
		const formattedAttributes: attribute[] = this.attributes()
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
