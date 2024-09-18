/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatFormField,
	MatHint,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';

@Component({
	selector: 'osee-plconfig-filter',
	standalone: true,
	imports: [
		MatFormField,
		MatLabel,
		MatInput,
		FormsModule,
		MatIcon,
		MatPrefix,
		MatHint,
	],
	template: `<mat-form-field class="tw-w-full">
		<mat-label>Filter Configuration Information</mat-label>
		<input
			matInput
			type="text"
			[(ngModel)]="filter"
			#input />
		<mat-icon matPrefix>filter_list</mat-icon>
		<mat-hint>Enter text to filter Configuration Table</mat-hint>
	</mat-form-field>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PLConfigFilterComponent {
	private _uiService = inject(PlConfigUIStateService);
	protected filter = this._uiService.filter;
}
