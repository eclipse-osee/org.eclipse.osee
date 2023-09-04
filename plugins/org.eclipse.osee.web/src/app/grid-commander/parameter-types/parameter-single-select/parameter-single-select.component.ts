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
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { NgFor } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
	selector: 'osee-parameter-single-select',
	templateUrl: './parameter-single-select.component.html',
	styles: [],
	standalone: true,
	imports: [MatFormFieldModule, MatSelectModule, NgFor, MatOptionModule],
})
export class ParameterSingleSelectComponent {
	//TODO: Determine how to dynamically render options in template based on command -- paramater attribute?
	@Input() options!: string[];
	@Input() label!: string;

	@Output('selectionChange') selectionChange: EventEmitter<{
		selectedOption: string;
	}> = new EventEmitter<{ selectedOption: string }>();

	constructor() {}

	onSelectionChange(e: MatSelectChange) {
		this.selectionChange.emit({ selectedOption: e.value });
	}
}
