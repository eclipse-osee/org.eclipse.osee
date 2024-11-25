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
import { Component, Input, Output, model } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import {
	MatError,
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import { EnumSetUniqueDescriptionDirective } from '@osee/messaging/shared/directives';
import type { enumeration, enumerationSet } from '@osee/messaging/shared/types';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import { Subject } from 'rxjs';
import { EnumFormComponent } from '../../forms/enum-form/enum-form.component';

@Component({
	selector: 'osee-enum-set-form',
	templateUrl: './enum-set-form.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatIconButton,
		MatSuffix,
		MatIcon,
		MatError,
		CdkTextareaAutosize,
		EnumFormComponent,
		EnumSetUniqueDescriptionDirective,
		ApplicabilityDropdownComponent,
	],
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class EnumSetFormComponent {
	@Input() bitSize = '0';
	enumSet = model.required<enumerationSet>();
	protected name = writableSlice(this.enumSet, 'name');
	protected nameValue = writableSlice(this.name, 'value');
	protected description = writableSlice(this.enumSet, 'description');
	protected descriptionValue = writableSlice(this.description, 'value');
	protected applicability = writableSlice(this.enumSet, 'applicability');
	protected enumerations = writableSlice(this.enumSet, 'enumerations');
	@Output('closed') _closeForm = new Subject();

	updateDescription(value: string) {
		this.descriptionValue.set(value);
	}

	updateEnums(value: enumeration[]) {
		this.enumerations.set(value);
	}
	closeForm() {
		this._closeForm.next(true);
	}
}
