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
import { PlMessagingTypesUIService } from '../services/pl-messaging-types-ui.service';

@Component({
	selector: 'osee-platform-types-filter',
	imports: [
		FormsModule,
		MatFormField,
		MatInput,
		MatLabel,
		MatHint,
		MatPrefix,
		MatIcon,
	],
	template: ` <mat-form-field class="tw-w-full tw-pb-4">
		<mat-label>Filter Types</mat-label>
		<input
			matInput
			type="text"
			#input
			[(ngModel)]="filter" />
		<mat-icon matPrefix>filter_list</mat-icon>
		<mat-hint>Enter text to filter Types</mat-hint>
	</mat-form-field>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlatformTypesFilterComponent {
	private uiService = inject(PlMessagingTypesUIService);
	protected filter = this.uiService.filter;
}
