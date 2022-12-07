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
import { Component, Input } from '@angular/core';

@Component({
	selector: 'osee-structure-table-long-text-field',
	templateUrl: './structure-table-long-text-field.component.html',
	styleUrls: ['./structure-table-long-text-field.component.sass'],
})
export class StructureTableLongTextFieldComponent {
	@Input() text: string = '';
	@Input() searchTerms: string = '';
	@Input() width: string | undefined = '';
	@Input() wordWrap: boolean = false;
	@Input() data_cy: string = '';

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
