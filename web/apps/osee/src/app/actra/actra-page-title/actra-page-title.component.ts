/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component, input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
	selector: 'osee-actra-page-title',
	imports: [MatIcon],
	template: `
		<div
			class="tw-flex tw-flex-row tw-gap-4 tw-p-4 tw-text-lg tw-font-bold">
			<div class="tw-flex tw-items-center">
				<mat-icon> {{ icon() }}</mat-icon>
			</div>
			<div class="tw-flex tw-items-center">
				{{ title() }}
			</div>
			<div class="tw-flex tw-items-center">
				<ng-content></ng-content>
			</div>
		</div>
	`,
})
export class ActraPageTitleComponent {
	icon = input.required<string>();
	title = input.required<string>();
}
