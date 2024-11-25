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
import { AsyncPipe, NgClass, NgStyle } from '@angular/common';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';

@Component({
	selector: 'osee-structure-table-long-text-field',
	template: `<div></div>`,
	styles: [],
	imports: [NgClass, NgStyle, HighlightFilteredTextDirective, AsyncPipe],
})
export class MockStructureTableLongTextFieldComponent {
	@Input() text: string = '';
	@Input() searchTerms: string = '';
	@Input() width: string | undefined = '';
	@Input() data_cy: string = '';

	wordWrap: boolean = false;

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
