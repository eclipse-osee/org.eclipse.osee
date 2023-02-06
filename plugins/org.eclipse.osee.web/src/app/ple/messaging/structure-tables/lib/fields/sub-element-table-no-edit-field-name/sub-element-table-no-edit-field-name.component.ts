/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field-name',
	templateUrl: './sub-element-table-no-edit-field-name.component.html',
	styleUrls: ['./sub-element-table-no-edit-field-name.component.sass'],
	standalone: true,
	imports: [HighlightFilteredTextDirective, NgIf],
})
export class SubElementTableNoEditFieldNameComponent {
	@Input() filter: string = '';
	@Input() name: string = '';
	@Input() end: number = 0;
	@Input() start: number = 0;
	constructor() {}
}
