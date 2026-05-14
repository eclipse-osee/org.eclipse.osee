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
import { DatePipe, NgClass } from '@angular/common';
import { Component, Output, computed, input } from '@angular/core';
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
import { attribute } from '@osee/shared/types';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { BehaviorSubject } from 'rxjs';
import { AttributeNameTrimPipe } from '../../pipes/attribute-name-trim/attribute-name-trim.pipe';
import { IfIdReturnFalsePipe } from '../../pipes/if-id-return-false/if-id-return-false.pipe';
import { StringToDatePipe } from '../../pipes/string-to-date/string-to-date.pipe';
import { MarkdownEditorComponent } from './../markdown-editor/markdown-editor.component';
import { AttributeEnumsDropdownComponent } from './attribute-enums-dropdown/attribute-enums-dropdown.component';
import {
	ExtensionAttribute,
	NameAttribute,
	NativeContentAttribute,
	NativeContentEditorComponent,
	NativeEditorAttributes,
} from './native-content-editor/native-content-editor.component';
import {
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';

// Attributes Editor does not enforce required fields.
// It will just highlight required fields based on an attribute's multiplicityId.
// Output is the changed attributes list for parent component to handle.
@Component({
	selector: 'osee-attributes-editor',
	imports: [
		NgClass,
		AttributeEnumsDropdownComponent,
		FormsModule,
		MatFormField,
		MatInput,
		MatSelect,
		MatOption,
		MatDatepicker,
		MatDatepickerToggle,
		MatDatepickerInput,
		MatSuffix,
		MarkdownEditorComponent,
		AttributeNameTrimPipe,
		IfIdReturnFalsePipe,
		StringToDatePipe,
		NativeContentEditorComponent,
	],
	providers: [provideNativeDateAdapter()],
	templateUrl: './attributes-editor.component.html',
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class AttributesEditorComponent {
	attributes = input.required<attribute[]>();
	editable = input.required<boolean>();
	artifactId = input<string>('');
	branchId = input<string>('');

	@Output() updatedAttributes = new BehaviorSubject<attribute[]>([]);

	// Track native content changes separately so they aren't lost
	// when standard fields re-emit.
	private nativeContentChanges: attribute[] = [];

	emitUpdatedAttributes() {
		// Collect typeIds already handled by native content changes to avoid duplicates.
		const nativeChangeTypeIds = new Set(
			this.nativeContentChanges.map((a) => a.typeId)
		);

		const formattedAttributes: attribute[] = this.attributes()
			.map((attribute) => {
				// Skip Input Stream attributes — handled by the native content editor.
				if (attribute.storeType === 'Input Stream') {
					return null;
				}
				// Skip attributes already covered by native content changes
				// (e.g., Name and Extension when a file update changed them).
				if (nativeChangeTypeIds.has(attribute.typeId)) {
					return null;
				}
				const formattedAttribute = { ...attribute, value: '' };

				if (attribute.storeType === 'Date' && attribute.value) {
					const dateValue = new Date(attribute.value);
					formattedAttribute.value = `${dateValue.getTime()}`;
				} else {
					formattedAttribute.value = attribute.value.toString();
				}

				return formattedAttribute;
			})
			.filter(
				(attribute): attribute is attribute =>
					attribute !== null && attribute.value !== ''
			);

		this.updatedAttributes.next([
			...formattedAttributes,
			...this.nativeContentChanges,
		]);
	}

	handleNativeContentChanges(changes: attribute[]) {
		this.nativeContentChanges = changes;
		this.emitUpdatedAttributes();
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

	protected readonly nativeEditorAttrs =
		computed<NativeEditorAttributes | null>(() => {
			const attrs = this.attributes() ?? [];
			const name = attrs.find(
				(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
			) as NameAttribute | undefined;
			const ext = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
			) as ExtensionAttribute | undefined;
			const native = attrs.find(
				(a) => a.typeId === ATTRIBUTETYPEIDENUM.NATIVE_CONTENT
			) as NativeContentAttribute | undefined;
			return name && ext && native
				? { name, extension: ext, nativeContent: native }
				: null;
		});
}
