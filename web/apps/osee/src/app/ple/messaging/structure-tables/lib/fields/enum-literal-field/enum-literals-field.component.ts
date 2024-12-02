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
import { AsyncPipe } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { tap } from 'rxjs';

@Component({
	selector: 'osee-enum-literals-field',
	templateUrl: './enum-literals-field.component.html',
	styles: [],
	imports: [AsyncPipe],
})
export class EnumLiteralsFieldComponent {
	private preferencesService = inject(PreferencesUIService);

	@Input() enumLiterals = '';

	wordWrap = false;
	globalPrefs = this.preferencesService.globalPrefs.pipe(
		tap((prefs) => (this.wordWrap = prefs.wordWrap))
	);

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}

	getEnumLiteralsArray() {
		return this.enumLiterals.split('\n');
	}
}
