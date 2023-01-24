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
import { AsyncPipe, NgClass, NgIf, NgStyle } from '@angular/common';
import { HighlightFilteredTextDirective } from '../../../../../../osee-utils/osee-string-utils/osee-string-utils-directives/highlight-filtered-text.directive';
import { PreferencesUIService } from 'src/app/ple/messaging/shared/services/ui/preferences-ui.service';
import { tap } from 'rxjs';

@Component({
	selector: 'osee-structure-table-long-text-field',
	templateUrl: './structure-table-long-text-field.component.html',
	styleUrls: ['./structure-table-long-text-field.component.sass'],
	standalone: true,
	imports: [
		NgClass,
		NgIf,
		NgStyle,
		HighlightFilteredTextDirective,
		AsyncPipe,
	],
})
export class StructureTableLongTextFieldComponent {
	@Input() text: string = '';
	@Input() searchTerms: string = '';
	@Input() width: string | undefined = '';
	@Input() data_cy: string = '';

	wordWrap: boolean = false;
	globalPrefs = this.preferencesService.globalPrefs.pipe(
		tap((prefs) => (this.wordWrap = prefs.wordWrap))
	);

	constructor(private preferencesService: PreferencesUIService) {}

	toggleExpanded() {
		this.wordWrap = !this.wordWrap;
	}
}
