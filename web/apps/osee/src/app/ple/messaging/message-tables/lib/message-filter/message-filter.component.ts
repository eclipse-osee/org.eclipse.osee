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
	MatLabel,
	MatPrefix,
	MatSuffix,
	MatHint,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import MimHeaderComponent from '@osee/messaging/shared/headers';
import { MessageUiService } from '@osee/messaging/shared/services';

@Component({
	selector: 'osee-message-filter',
	standalone: true,
	imports: [
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
		MatSuffix,
		MatHint,
		FormsModule,
		MimHeaderComponent,
	],
	styles: [
		`
			:host {
				display: flex;
			}
		`,
	],
	template: `<mat-form-field
		subscriptSizing="dynamic"
		class="tw-w-[80vw] tw-min-w-[52vw] tw-max-w-[85vw] tw-flex-shrink tw-flex-grow tw-px-0 [&>*>*>.mat-mdc-form-field-infix]:tw-pt-4 [&>.mat-mdc-text-field-wrapper]:tw-rounded-3xl">
		<!-- this css is ugly and likely to break at some point... -->
		<mat-label class="tw-px-4">Filter Message Information</mat-label>
		<input
			matInput
			type="text"
			[(ngModel)]="filter"
			#input />
		<!-- class="tw-block xl:tw-visible" -->
		<div
			matTextPrefix
			class="tw-hidden xl:tw-block">
			<osee-messaging-header />
		</div>
		<mat-icon
			matSuffix
			class="tw-px-4"
			>search</mat-icon
		>
		<!-- TODO: enable below when showcasing right above table instead of in toolbar -->
		<!-- <mat-hint
    >Enter text to filter Message Table. Only full text matches will
    show results.
  </mat-hint> -->
	</mat-form-field>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessageFilterComponent {
	private messageService = inject(MessageUiService);
	protected filter = this.messageService.filter;
}
export default MessageFilterComponent;
