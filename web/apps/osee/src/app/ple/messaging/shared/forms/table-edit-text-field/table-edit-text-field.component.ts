/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

@Component({
	selector: 'osee-table-edit-text-field',
	standalone: true,
	imports: [FormsModule, MatFormField, MatInput],
	templateUrl: './table-edit-text-field.component.html',
	styles: [],
})
export class TableEditTextFieldComponent {
	@Input() value = '';
	@Output() changeEvent = new EventEmitter<string>();
	@Output() focusLost = new EventEmitter<string>();
	@Output() enterPressed = new EventEmitter<string>();

	update() {
		this.changeEvent.emit(this.value);
	}

	onFocusLost() {
		this.focusLost.emit(this.value);
	}

	onKeyup() {
		this.enterPressed.emit(this.value);
	}
}
