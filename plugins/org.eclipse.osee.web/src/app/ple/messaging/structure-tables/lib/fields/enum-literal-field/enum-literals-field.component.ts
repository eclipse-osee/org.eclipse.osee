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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { tap } from 'rxjs';

@Component({
	standalone: true,
	selector: 'osee-enum-literals-field',
	templateUrl: './enum-literals-field.component.html',
	styleUrls: ['./enum-literals-field.component.sass'],
	imports: [NgIf, NgFor, AsyncPipe],
})
export class EnumLiteralsFieldComponent {
	@Input() enumLiterals: string = '';

	wordWrap: boolean = false;
	globalPrefs = this.preferencesService.globalPrefs.pipe(
		tap((prefs) => (this.wordWrap = prefs.wordWrap))
	);

	constructor(private preferencesService: PreferencesUIService) {}

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}

	getEnumLiteralsArray() {
		return this.enumLiterals.split('\n');
	}
}
