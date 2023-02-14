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
import { InputControlComponent } from '../../../shared/input-control/input-control.component';

@Component({
	selector: 'osee-column-filter',
	templateUrl: './column-filter.component.html',
	styleUrls: ['./column-filter.component.sass'],
	standalone: true,
	imports: [InputControlComponent],
})
export class ColumnFilterComponent {
	@Input() filterInputLabel: string = '';
	@Output('update') updateInput: EventEmitter<{
		input: string;
	}> = new EventEmitter<{ input: string }>();

	constructor() {}

	update(e: { input: string }) {
		this.updateInput.emit({
			input: e.input,
		});
	}
}
