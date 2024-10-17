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
import { Component, Input, inject } from '@angular/core';
import { AsyncPipe, NgClass, NgStyle } from '@angular/common';
import { tap } from 'rxjs';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';

@Component({
	selector: 'osee-structure-table-long-text-field',
	template: `@if (globalPrefs | async) {
			<summary
				oseeHighlightFilteredText
				[searchTerms]="searchTerms"
				[text]="text"
				(click)="toggleExpanded()"
				classToApply="tw-text-accent-900"
				class="tw-inline-block tw-cursor-pointer"
				[ngStyle]="{
					width: width,
				}"
				[ngClass]="{
					'tw-overflow-hidden tw-text-ellipsis tw-whitespace-nowrap':
						wordWrap === null || !wordWrap,
				}"
				[attr.data-cy]="data_cy">
				{{ text }}
			</summary>
		} @else {
			<summary
				oseeHighlightFilteredText
				[searchTerms]="searchTerms"
				[text]="text"
				(click)="toggleExpanded()"
				classToApply="tw-text-accent-900"
				class="tw-inline-block tw-cursor-pointer"
				[ngStyle]="{
					width: width,
				}"
				[ngClass]="{
					'tw-overflow-hidden tw-text-ellipsis tw-whitespace-nowrap':
						wordWrap === null || !wordWrap,
				}"
				[attr.data-cy]="data_cy">
				{{ text }}
			</summary>
		}`,
	styles: [],
	standalone: true,
	imports: [NgClass, NgStyle, HighlightFilteredTextDirective, AsyncPipe],
})
export class StructureTableLongTextFieldComponent {
	private preferencesService = inject(PreferencesUIService);

	@Input() text = '';
	@Input() searchTerms = '';
	@Input() width: string | undefined = '';
	@Input() data_cy = '';

	wordWrap = false;
	globalPrefs = this.preferencesService.globalPrefs.pipe(
		tap((prefs) => {
			if (prefs !== null && prefs !== undefined) {
				this.wordWrap = prefs.wordWrap;
			}
		})
	);

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
