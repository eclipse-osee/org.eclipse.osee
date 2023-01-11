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
import { NgClass, NgStyle } from '@angular/common';
import { Component, Input } from '@angular/core';
import { OseeStringUtilsDirectivesModule } from '../../../../../../osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field-dynamic-width',
	templateUrl:
		'./sub-element-table-no-edit-field-dynamic-width.component.html',
	styleUrls: [
		'./sub-element-table-no-edit-field-dynamic-width.component.sass',
	],
	standalone: true,
	imports: [OseeStringUtilsDirectivesModule, NgStyle, NgClass],
})
export class SubElementTableNoEditFieldDynamicWidthComponent {
	@Input() field: string = '';
	@Input() width: string = '';
	@Input() filter: string = '';
	@Input() wordWrap: boolean = false;
	constructor() {}

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
