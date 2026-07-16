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
import { DatePipe } from '@angular/common';
import { Component, Output, computed, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOption, provideNativeDateAdapter } from '@angular/material/core';
import {
	MatDatepicker,
	MatDatepickerInput,
	MatDatepickerToggle,
} from '@angular/material/datepicker';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
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
// It will just highlight required fields based on an attribute's multiplicity.
// Output is the changed attributes list for parent component to handle.
@Component({
	selector: 'osee-attributes-editor',
	imports: [
		AttributeEnumsDropdownComponent,
		FormsModule,
		MatFormField,
		MatLabel,
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
	attributes = input.required<attribute<string, ATTRIBUTETYPEID>[]>();
	editable = input.required<boolean>();
	artifactId = input<string>('');
	branchId = input<string>('');

	@Output() updatedAttributes = new BehaviorSubject<
		attribute<string, ATTRIBUTETYPEID>[]
	>([]);

	// Track native content changes separately so they aren't lost
	// when standard fields re-emit.
	private nativeContentChanges: attribute<string, ATTRIBUTETYPEID>[] = [];

	// Pending display values for the native content editor
	protected readonly pendingNativeName = signal<string | null>(null);
	protected readonly pendingNativeExtension = signal<string | null>(null);
	protected readonly hasUnsavedNativeChanges = signal<boolean>(false);

	emitUpdatedAttributes() {
		// Collect typeIds already handled by native content changes to avoid duplicates.
		const nativeChangeTypeIds = new Set(
			this.nativeContentChanges.map((a) => a.typeId)
		);

		const formattedAttributes: attribute<string, ATTRIBUTETYPEID>[] =
			this.attributes()
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
					(
						attribute
					): attribute is attribute<string, ATTRIBUTETYPEID> =>
						attribute !== null && attribute.value !== ''
				);

		this.updatedAttributes.next([
			...formattedAttributes,
			...this.nativeContentChanges,
		]);
	}

	handleNativeContentChanges(changes: attribute<string, ATTRIBUTETYPEID>[]) {
		this.nativeContentChanges = changes;

		// Extract pending display values from the changes
		const nameChange = changes.find(
			(a) => a.typeId === BASEATTRIBUTETYPEIDENUM.NAME
		);
		const extChange = changes.find(
			(a) => a.typeId === ATTRIBUTETYPEIDENUM.EXTENSION
		);

		this.pendingNativeName.set(nameChange?.value ?? null);
		this.pendingNativeExtension.set(extChange?.value ?? null);
		this.hasUnsavedNativeChanges.set(changes.length > 0);

		this.emitUpdatedAttributes();
	}

	/**
	 * Called by the parent after a successful save to reset native content state.
	 * The parent reloads the artifact data, so pending values can be cleared.
	 */
	resetAfterSave() {
		this.nativeContentChanges = [];
		this.pendingNativeName.set(null);
		this.pendingNativeExtension.set(null);
		this.hasUnsavedNativeChanges.set(false);
	}

	// Input is required if attribute multiplicity AT_LEAST_ONE or EXACTLY_ONE

	isRequired(attribute: attribute<string, ATTRIBUTETYPEID>) {
		return attribute.name === 'Id'
			? false
			: attribute.multiplicity?.id === '2' ||
					attribute.multiplicity?.id === '4';
	}

	setAttribute(val: string, attribute: attribute<string, ATTRIBUTETYPEID>) {
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
