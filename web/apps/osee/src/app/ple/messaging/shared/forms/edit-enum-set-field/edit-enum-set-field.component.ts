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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import {
	Component,
	computed,
	inject,
	input,
	model,
	output,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { bitSize, type enumerationSet } from '@osee/messaging/shared/types';
import { writableSlice } from '@osee/shared/utils';
import { EnumFormComponent } from '../enum-form/enum-form.component';

@Component({
	selector: 'osee-edit-enum-set-field',
	templateUrl: './edit-enum-set-field.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		CdkTextareaAutosize,
		MatTable,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatColumnDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		EnumFormComponent,
		ApplicabilityDropdownComponent,
	],
})
export class EditEnumSetFieldComponent {
	private preferenceService = inject(PreferencesUIService);

	inEditMode = this.preferenceService.inEditMode;

	editable = input(false);

	bitSize = input.required<bitSize>();
	enumSet = model.required<enumerationSet>();
	protected id = computed(() => this.enumSet().id);
	protected nameAttr = writableSlice(this.enumSet, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	protected descriptionAttr = writableSlice(this.enumSet, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	protected applicability = writableSlice(this.enumSet, 'applicability');
	protected enums = writableSlice(this.enumSet, 'enumerations');

	unique = output<boolean>();

	updateUnique(value: boolean) {
		this.unique.emit(value);
	}

	setDescription(value: string) {
		this.description.set(value);
	}
}
