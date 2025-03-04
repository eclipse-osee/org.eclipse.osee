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
import { AsyncPipe, NgClass, NgStyle } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { tap } from 'rxjs';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field-dynamic-width',
	templateUrl:
		'./sub-element-table-no-edit-field-dynamic-width.component.html',
	styles: [],
	imports: [HighlightFilteredTextDirective, NgStyle, NgClass, AsyncPipe],
})
export class SubElementTableNoEditFieldDynamicWidthComponent {
	private preferencesService = inject(PreferencesUIService);

	@Input() field = '';
	@Input() width = '';
	@Input() filter = '';

	wordWrap = false;
	globalPrefs = this.preferencesService.globalPrefs.pipe(
		tap((prefs) => (this.wordWrap = prefs.wordWrap))
	);

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
