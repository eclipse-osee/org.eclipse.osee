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
import { NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ControlContainer, FormsModule, NgModelGroup } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { UniquePlatformTypeNameDirective } from '@osee/messaging/shared/directives';
import type {
	logicalTypeFieldInfo,
	PlatformType,
} from '@osee/messaging/shared/types';
import { FirstLetterLowerPipe } from '@osee/shared/utils';

@Component({
	selector: 'osee-new-attribute-form-field',
	templateUrl: './new-attribute-form-field.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatHint,
		MatInput,
		NgIf,
		NgFor,
		FirstLetterLowerPipe,
		UniquePlatformTypeNameDirective,
	],
	viewProviders: [{ provide: ControlContainer, useExisting: NgModelGroup }],
})
export class NewAttributeFormFieldComponent implements OnInit {
	@Input() form!: logicalTypeFieldInfo<keyof PlatformType>;
	@Input() units: string[] = [];
	@Output() formChanged = new EventEmitter<
		logicalTypeFieldInfo<keyof PlatformType>
	>();
	constructor() {}

	ngOnInit(): void {
		this.setDefaultValue();
	}
	setDefaultValue() {
		if (!this.form.editable) {
			const value =
				this.form.value !== undefined
					? JSON.parse(JSON.stringify(this.form.value))
					: '';
			this.form.value = this.form.defaultValue;
			if (value !== this.form.defaultValue) {
				this.change();
			}
		}
	}
	change() {
		this.formChanged.emit(this.form);
	}
}
