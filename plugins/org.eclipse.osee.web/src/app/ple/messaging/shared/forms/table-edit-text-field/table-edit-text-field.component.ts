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
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';

@Component({
	selector: 'osee-table-edit-text-field',
	standalone: true,
	imports: [CommonModule, FormsModule, MatFormFieldModule, MatInputModule],
	templateUrl: './table-edit-text-field.component.html',
	styleUrls: ['./table-edit-text-field.component.scss'],
})
export class TableEditTextFieldComponent {
	@Input() value: string = '';
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
